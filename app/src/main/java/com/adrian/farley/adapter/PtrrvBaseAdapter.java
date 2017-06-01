package com.adrian.farley.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

/**
 * Created by RanQing on 16-9-18 17:56.
 */
public class PtrrvBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{
    protected LayoutInflater mInflater;
    protected int mCount = 0;
    protected Context mContext = null;
    protected List<Object> list;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_HISVIDEO = 1;
    public static final int TYPE_MESSAGE = 2;

    public PtrrvBaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setCount(int count){
        mCount = count;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public Object getItem(int position){
        return list == null ? null : list.get(position);
    }
}
