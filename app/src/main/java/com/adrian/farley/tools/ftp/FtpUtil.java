package com.adrian.farley.tools.ftp;

import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by adrian on 16-11-22.
 */

public class FtpUtil {

    private FTPClient ftpClient;

    public boolean connectFtp(Ftp f) throws IOException {
        ftpClient = new FTPClient();
        boolean flag = false;
        int reply;
        if (f.getPort() == 0) {
            ftpClient.connect(f.getIpAddr(), 21);
        } else {
            ftpClient.connect(f.getIpAddr(), f.getPort());
        }
        ftpClient.login(f.getUserName(), f.getPwd());
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            return flag;
        }
        ftpClient.changeWorkingDirectory(f.getPath());
        flag = true;
        return flag;
    }

    public void closeFtp() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传文件
     *
     * @param f
     * @throws IOException
     */
    public void upload(File f) throws IOException {
        if (f.isDirectory()) {
            ftpClient.makeDirectory(f.getName());
            ftpClient.changeWorkingDirectory(f.getName());
            String[] files = f.list();
            for (String fstr :
                    files) {
                File file1 = new File(f.getPath() + "/" + fstr);
                if (file1.isDirectory()) {
                    upload(file1);
                    ftpClient.changeToParentDirectory();
                } else {
                    File file2 = new File(f.getPath() + "/" + fstr);
                    FileInputStream fis = new FileInputStream(file2);
                    ftpClient.storeFile(file2.getName(), fis);
                    fis.close();
                }
            }
        } else {
            File file = new File(f.getPath());
            FileInputStream fis = new FileInputStream(file);
            ftpClient.storeFile(f.getName(), fis);
            Log.e("upload_name", f.getName());
            fis.close();
        }
    }

    public void startDown(Ftp f, String localBaseDir, String remoteBaseDir) throws IOException {
        if (connectFtp(f)) {
            FTPFile[] files = null;
            boolean changdir = ftpClient.changeWorkingDirectory(remoteBaseDir);
            if (changdir) {
                ftpClient.setControlEncoding("UTF-8");
                files = ftpClient.listFiles();
                for (FTPFile file :
                        files) {
                    downloadFile(file, localBaseDir, remoteBaseDir);
                }
            }
        } else {
            Log.e("FTP", "connect failed");
        }
    }

    private void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
        if (ftpFile.isFile()) {
            if (ftpFile.getName().indexOf("?") == -1) {
                OutputStream os = null;
                File localFile = new File(relativeLocalPath + ftpFile.getName());
                if (localFile.exists()) {
                    return;
                } else {
                    try {
                        os = new FileOutputStream(relativeLocalPath + ftpFile.getName());
                        ftpClient.retrieveFile(ftpFile.getName(), os);
                        os.flush();
                        os.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else {
            String newlocalRelatePath = relativeLocalPath + ftpFile.getName();
            String newRemote = new String(relativeRemotePath + ftpFile.getName().toString());
            File fl = new File(newlocalRelatePath);
            if (!fl.exists()) {
                fl.mkdirs();
            }
            try {
                newlocalRelatePath = newlocalRelatePath + '/';
                newRemote = newRemote + "/";
                String currentWorkDir = ftpFile.getName().toString();
                boolean changedir = ftpClient.changeWorkingDirectory(currentWorkDir);
                if (changedir) {
                    FTPFile[] files = null;
                    files = ftpClient.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        downloadFile(files[i], newlocalRelatePath, newRemote);
                    }
                }
                if (changedir) {
                    ftpClient.changeToParentDirectory();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
