package com.adrian.farley.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.pojo.UploadFileInfo;

import java.util.List;

/**
 * Created by adrian on 16-12-13.
 */

public class FileExpAdapter extends BaseAdapter {

    private Context context;
    private List<UploadFileInfo> files;

    public FileExpAdapter(Context context) {
        this.context = context;
    }

    public FileExpAdapter(Context context, List<UploadFileInfo> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files != null ? files.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return files != null ? files.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return files != null ? position : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_file_item, null, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_file_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UploadFileInfo info = files.get(position);
        holder.name.setText(info.getName());
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
}
