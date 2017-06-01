package com.adrian.farley.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adrian.farley.R;
import com.adrian.farley.pojo.WarningInfo;

import java.util.List;

/**
 * Created by adrian on 16-12-17.
 */

public class WarningAdapter extends RecyclerView.Adapter {
    private List<WarningInfo> list;
    public WarningAdapter() {
    }

    public WarningAdapter(List<WarningInfo> list) {
        this.list = list;
    }

    public void setList(List<WarningInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning_info, null, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WarningViewHolder viewHolder = (WarningViewHolder) holder;
        WarningInfo info = list.get(position);
        viewHolder.mInfoTV.setText(/*info.getIndex() + "." + */info.getValue());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class WarningViewHolder extends RecyclerView.ViewHolder {

        private TextView mInfoTV;

        public WarningViewHolder(View itemView) {
            super(itemView);
            mInfoTV = (TextView) itemView.findViewById(R.id.tv_warning_info);
        }
    }
}
