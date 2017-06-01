package com.adrian.farley.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.farley.R;
import com.adrian.farley.adapter.PtrrvBaseAdapter;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.DevBaseInfo;
import com.adrian.farley.pojo.request.DevListReq;
import com.adrian.farley.pojo.request.LogoutReq;
import com.adrian.farley.pojo.response.DevListRes;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;
import com.adrian.farley.widget.DemoLoadMoreView;
import com.adrian.farley.widget.DividerItemDecoration;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements ConnMngr.IConnMngrCallback {

    private PullToRefreshRecyclerView mPtrrv;
    private PtrrvAdapter mAdapter;
    private static final int DEFAULT_ITEM_SIZE = 20;
    private static final int ITEM_SIZE_OFFSET = 20;

    private static final int MSG_CODE_REFRESH = 0;
    private static final int MSG_CODE_LOADMORE = 1;

    private static final int TIME = 1000;

    private List<Object> list = new ArrayList<>();

    private long firstBackMil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        CommUtils.verifyStoragePermissions(this);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);

        setTitle(R.string.dev_list);

        mPtrrv = (PullToRefreshRecyclerView) findViewById(R.id.ptrrv);
        mPtrrv.setSwipeEnable(true);//open swipe

        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(this, mPtrrv.getRecyclerView());
        loadMoreView.setLoadmoreString(getString(R.string.demo_loadmore));
        loadMoreView.setLoadMorePadding(100);
        mPtrrv.setLayoutManager(new LinearLayoutManager(this));
        mPtrrv.setPagingableListener(new PullToRefreshRecyclerView.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                mHandler.sendEmptyMessageDelayed(MSG_CODE_LOADMORE, TIME);
            }
        });
        mPtrrv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mHandler.sendEmptyMessageDelayed(MSG_CODE_REFRESH, TIME);
                Log.e("REFRESH", "refresh data");
                getDevList();
            }
        });
        mPtrrv.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
//        mPtrrv.addHeaderView(View.inflate(this, R.layout.header, null));
        mPtrrv.setEmptyView(View.inflate(this,R.layout.empty_view,null));
//        mPtrrv.removeHeader();
//        mPtrrv.setLoadMoreFooter(loadMoreView);
//        mPtrrv.getLoadMoreFooter().setOnDrawListener(new BaseLoadMoreView.OnDrawListener() {
//            @Override
//            public boolean onDrawLoadMore(Canvas c, RecyclerView parent) {
//                Log.i("onDrawLoadMore","draw load more");
//                return false;
//            }
//        });
        mAdapter = new PtrrvAdapter(this);
        mPtrrv.setAdapter(mAdapter);
//        mPtrrv.onFinishLoading(true, false);
    }

    @Override
    protected void loadData() {
        getDevList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnMngr.getInstance().closeConn();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBackMil > Constants.QUIT_DELAY) {
            CommUtils.showToast(R.string.back_again);
            firstBackMil = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    protected void back(View view) {
        if (System.currentTimeMillis() - firstBackMil > Constants.QUIT_DELAY) {
            CommUtils.showToast(R.string.back_again);
            firstBackMil = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void getDevList() {
        if (FarleyUtils.isRemote()){
            DevListReq req = new DevListReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid());
            ConnMngr.getInstance().setCallback(this);
            ConnMngr.getInstance().sendMsg(req.conv2JsonString());
//            LogoutReq req = new LogoutReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid());
//            ConnMngr.getInstance().setCallback(this);
//            ConnMngr.getInstance().sendMsg(req.conv2JsonString());
        } else {
            List devs = MyApplication.newInstance().getDevBaseInfos();
            Log.e("DEVSTATUS", "dev count : " + devs.size());
            mAdapter.setList(devs);
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE_REFRESH) {
                mAdapter.setCount(DEFAULT_ITEM_SIZE);
                mAdapter.notifyDataSetChanged();
                mPtrrv.setOnRefreshComplete();
                mPtrrv.onFinishLoading(false, false);
            } else if (msg.what == MSG_CODE_LOADMORE) {
                if(mAdapter.getItemCount() == DEFAULT_ITEM_SIZE + ITEM_SIZE_OFFSET){
                    //over
                    Toast.makeText(MainActivity.this, R.string.nomoredata, Toast.LENGTH_SHORT).show();
                    mPtrrv.onFinishLoading(false, false);
                }else {
                    mAdapter.setCount(DEFAULT_ITEM_SIZE + ITEM_SIZE_OFFSET);
                    mAdapter.notifyDataSetChanged();
                    mPtrrv.onFinishLoading(true, false);
                }
            }
        }
    };

    @Override
    public void rsp(final String resp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DevListRes res = new DevListRes(resp);
                if (res.getStatus() == 0) {
                    List devs = res.getList();
                    Log.e("DEVSTATUS", "dev count : " + devs.size());
                    mAdapter.setList(devs);
                    mPtrrv.setOnRefreshComplete();
//                    mPtrrv.onFinishLoading(true, false);
                }
            }
        });
    }

    @Override
    public void exc(final String exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPtrrv.setOnRefreshComplete();
                CommUtils.showToast(exception);
            }
        });
    }

    private class PtrrvAdapter extends PtrrvBaseAdapter<PtrrvAdapter.ViewHolder> {

        public PtrrvAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.ptrrv_item, null);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DevBaseInfo item = (DevBaseInfo) getItem(position);
            holder.getTv().setText(item.getId());
            if (!item.isOnline()) {
                holder.getTv().setTextColor(Color.rgb(150,150,150));
            } else {
                holder.getTv().setTextColor(Color.rgb(0, 0, 0));
            }
            holder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isOnline()) {
                        MyApplication.newInstance().setMonitorid(item.getCamera());
                        Bundle bundle = new Bundle();
                        bundle.putString("id", item.getId());
                        bundle.putString("ip", FarleyUtils.isRemote() ? Constants.SERVER_IP : item.getLanDev().getIp());
                        bundle.putInt("port", FarleyUtils.isRemote() ? Constants.port : item.getLanDev().getPort());
                        startActivity(DetailInfoActivity.class, bundle);
                    } else {
                        CommUtils.showToast(R.string.offline_prompt);
                    }
                }
            });
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private CircleImageView civ;
            private TextView tv;
            private View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                civ = (CircleImageView) itemView.findViewById(R.id.profile_image);
                tv = (TextView) itemView.findViewById(R.id.tvContent);
            }

            public CircleImageView getCiv() {
                return civ;
            }

            public TextView getTv() {
                return tv;
            }

            public View getView() {
                return view;
            }
        }
    }
}
