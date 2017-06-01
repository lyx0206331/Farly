package com.adrian.farley.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adrian on 16-12-13.
 * 过滤多种文件类型
 */

public class FileFilterTool implements FilenameFilter {

    List<String> types;

    public FileFilterTool() {
        types = new ArrayList<String>();
    }

    public FileFilterTool(List<String> types) {
        super();
        this.types = types;
    }

    @Override
    public boolean accept(File dir, String name) {
        for (Iterator<String> iterator = types.iterator(); iterator.hasNext();) {
            String type = (String) iterator.next();
            if (name.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加指定类型的文件。
     *
     * @param type 要添加的文件类型，如".jpg"。
     */
    public void addType(String type) {
        types.add(type);
    }
}
