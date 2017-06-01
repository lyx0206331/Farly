package com.adrian.farley.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.adapter.FileExpAdapter;
import com.adrian.farley.pojo.UploadFileInfo;
import com.adrian.farley.tools.FileFilterTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExpActivity extends BaseActivity {

    private TextView mTipsTV;
    private GridView mFileGV;
    private TextView mEmptyTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_file_exp);
        findViewById(R.id.ib_more).setVisibility(View.GONE);
        mTipsTV = (TextView) findViewById(R.id.tv_tips);
        mFileGV = (GridView) findViewById(R.id.gv_file_exp);
        mEmptyTV = (TextView) findViewById(R.id.tv_empty);

        mFileGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadFileInfo info = (UploadFileInfo) ((FileExpAdapter)parent.getAdapter()).getItem(position);
                Intent intent = new Intent();
                intent.putExtra("file", info.getPath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        setTitle("farley");
        mTipsTV.setText(getString(R.string.tips, Environment.getExternalStorageDirectory().getAbsolutePath() + "/farley"));
    }

    @Override
    protected void loadData() {
        List<UploadFileInfo> data = getFiles(Environment.getExternalStorageDirectory().getAbsolutePath() + "/farley");
        if (data != null && data.size() > 0) {
            mFileGV.setVisibility(View.VISIBLE);
            mEmptyTV.setVisibility(View.GONE);
            mFileGV.setAdapter(new FileExpAdapter(this, data));
        } else {
            mFileGV.setVisibility(View.GONE);
            mEmptyTV.setVisibility(View.VISIBLE);
        }
    }

    private List<UploadFileInfo> getFiles(String path) {
        File fpath = new File(path);
        if (!fpath.exists() || !fpath.isDirectory()) {
            return null;
        }
        List<UploadFileInfo> list = new ArrayList<>();
        FileFilterTool filter = new FileFilterTool();
        filter.addType(".nc");
        File[] files = fpath.listFiles(filter);
        for (File f : files) {
            list.add(new UploadFileInfo(f.getName(), f.getAbsolutePath()));
        }
        return list;
    }
}
