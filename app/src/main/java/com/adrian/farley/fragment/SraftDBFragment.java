package com.adrian.farley.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.adrian.farley.R;
import com.adrian.farley.activity.DetailInfoActivity;
import com.adrian.farley.adapter.SimpleTreeAdapter;
import com.adrian.farley.adapter.TreeListViewAdapter;
import com.adrian.farley.application.MyApplication;
import com.adrian.farley.pojo.FileBean;
import com.adrian.farley.pojo.Node;
import com.adrian.farley.pojo.request.DevInfoReq;
import com.adrian.farley.tools.CommUtils;
import com.adrian.farley.tools.ConnMngr;
import com.adrian.farley.tools.ConnMngr.IConnMngrCallback;
import com.adrian.farley.tools.Constants;
import com.adrian.farley.tools.FarleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SraftDBFragment extends BaseFragment implements IConnMngrCallback {

    private List<FileBean> mDatas = new ArrayList<>();
    private ListView mTree;
    private Button mRefreshBtn;
    private TreeListViewAdapter mAdapter;

    public SraftDBFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.sraft_database);
        View mLayout = inflater.inflate(R.layout.fragment_sraft_db, container, false);

        mTree = (ListView) mLayout.findViewById(R.id.lv_sraft_db);
        mRefreshBtn = (Button) mLayout.findViewById(R.id.btn_refresh);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSraftDBInfo();
                v.setVisibility(View.GONE);
            }
        });
//        initDatas();
//        initDataTree();
        getSraftDBInfo();
        return mLayout;
    }

    private void initDataTree() {
        try
        {
            mAdapter = new SimpleTreeAdapter<FileBean>(mTree, getContext(), mDatas, 0);
            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener()
            {
                @Override
                public void onClick(Node node, int position)
                {
                    if (node.isLeaf())
                    {
                        CommUtils.showToast(node.getName());
                    }
                }

            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        mTree.setAdapter(mAdapter);
    }

    private void initDatas()
    {
        mDatas.add(new FileBean(1, 0, "文件管理系统"));
        mDatas.add(new FileBean(2, 1, "游戏"));
        mDatas.add(new FileBean(3, 1, "文档"));
        mDatas.add(new FileBean(4, 1, "程序"));
        mDatas.add(new FileBean(5, 2, "war3"));
        mDatas.add(new FileBean(6, 2, "刀塔传奇"));

        mDatas.add(new FileBean(7, 4, "面向对象"));
        mDatas.add(new FileBean(8, 4, "非面向对象"));

        mDatas.add(new FileBean(9, 7, "C++"));
        mDatas.add(new FileBean(10, 7, "JAVA"));
        mDatas.add(new FileBean(11, 7, "Javascript"));
        mDatas.add(new FileBean(12, 8, "C"));

    }

    @Override
    protected void lazyLoad() {
        getActivity().setTitle(R.string.sraft_database);
    }

    private void getSraftDBInfo() {
        String id = ((DetailInfoActivity)getActivity()).getId();
        List<String> des = new ArrayList<>();
        des.add("工艺数据库");
        DevInfoReq devInfoReq = new DevInfoReq(FarleyUtils.getUserid(), MyApplication.newInstance().getSessionid(), id, des);
        ConnMngr.getInstance().setCallback(this);
        ConnMngr.getInstance().sendMsg(devInfoReq.conv2JsonString());
    }

    private boolean isNotNull(JSONObject jsonObject) {
        if (jsonObject != null) {
            return true;
        }
        return false;
    }

    @Override
    public void rsp(final String resp) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parseJson(resp);
            }
        });
    }

    private void parseJson(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.optInt("status") != 0) {
                CommUtils.showToast(jsonObject.optString("err"));
                mTree.setVisibility(View.GONE);
                mRefreshBtn.setVisibility(View.VISIBLE);
                return;
            } else {
                mTree.setVisibility(View.VISIBLE);
                mRefreshBtn.setVisibility(View.GONE);
            }
            JSONObject content = jsonObject.optJSONObject("content");
            if (isNotNull(content)) {
                JSONObject sraft_db = content.optJSONObject("工艺数据库");
                if (isNotNull(sraft_db)) {
                    JSONObject cut = sraft_db.optJSONObject("切割");
                    if (isNotNull(cut)) {
                        mDatas.add(new FileBean(1, 0, "切割"));
                        JSONObject scan_cut = cut.optJSONObject("扫描切割");
                        if (isNotNull(scan_cut)) {
                            mDatas.add(new FileBean(2, 1, "扫描切割"));
                            JSONObject cut_power = scan_cut.optJSONObject("切割功率");
                            if (isNotNull(cut_power)) mDatas.add(new FileBean(3, 2, "切割功率:" + cut_power.optString(Constants.VALUE) + cut_power.optString(Constants.UNIT)));
                            JSONObject cut_acc = scan_cut.optJSONObject("切割加速度");
                            if (isNotNull(cut_acc)) mDatas.add(new FileBean(4, 2, "切割加速度:" + cut_acc.optString(Constants.VALUE) + cut_acc.optString(Constants.UNIT)));
                            JSONObject cut_acc_time = scan_cut.optJSONObject("切割加速时间");
                            if (isNotNull(cut_acc_time)) mDatas.add(new FileBean(5, 2, "切割加速时间:" + cut_acc_time.optString(Constants.VALUE) + cut_acc_time.optString(Constants.UNIT)));
                            JSONObject cut_duty = scan_cut.optJSONObject("切割占空比");
                            if (isNotNull(cut_duty)) mDatas.add(new FileBean(6, 2, "切割占空比:" + cut_duty.optString(Constants.VALUE) + cut_duty.optString(Constants.UNIT)));
                            JSONObject cut_gas = scan_cut.optJSONObject("切割气体");
                            if (isNotNull(cut_gas)) mDatas.add(new FileBean(7, 2, "切割气体:" + cut_gas.optString(Constants.VALUE) + cut_gas.optString(Constants.UNIT)));
                            JSONObject cut_gas_pre = scan_cut.optJSONObject("切割气压");
                            if (isNotNull(cut_gas_pre)) mDatas.add(new FileBean(8, 2, "切割气压:" + cut_gas_pre.optString(Constants.VALUE) + cut_gas_pre.optString(Constants.UNIT)));
                            JSONObject cut_focus = scan_cut.optJSONObject("切割焦点");
                            if (isNotNull(cut_focus)) mDatas.add(new FileBean(9, 2, "切割焦点:" + cut_focus.optString(Constants.VALUE) + cut_focus.optString(Constants.UNIT)));
                            JSONObject cut_accuracy = scan_cut.optJSONObject("切割精度");
                            if (isNotNull(cut_accuracy)) mDatas.add(new FileBean(10, 2, "切割精度:" + cut_accuracy.optString(Constants.VALUE) + cut_accuracy.optString(Constants.UNIT)));
                            JSONObject cut_speed = scan_cut.optJSONObject("切割速度");
                            if (isNotNull(cut_speed)) mDatas.add(new FileBean(11, 2, "切割速度:" + cut_speed.optString(Constants.VALUE) + cut_speed.optString(Constants.UNIT)));
                            JSONObject cut_freq = scan_cut.optJSONObject("切割频率");
                            if (isNotNull(cut_freq)) mDatas.add(new FileBean(12, 2, "切割频率:" + cut_freq.optString(Constants.VALUE) + cut_freq.optString(Constants.UNIT)));
                            JSONObject cut_height = scan_cut.optJSONObject("切割高度");
                            if (isNotNull(cut_height)) mDatas.add(new FileBean(13, 2, "切割高度:" + cut_height.optString(Constants.VALUE) + cut_height.optString(Constants.UNIT)));
                            JSONObject power_ctrl = scan_cut.optJSONObject("功率控制");
                            if (isNotNull(power_ctrl)) mDatas.add(new FileBean(14, 2, "功率控制:" + power_ctrl.optString(Constants.VALUE) + power_ctrl.optString(Constants.UNIT)));
                            JSONObject arc_acc_time = scan_cut.optJSONObject("圆弧加速时间");
                            if (isNotNull(arc_acc_time)) mDatas.add(new FileBean(15, 2, "圆弧加速时间:" + arc_acc_time.optString(Constants.VALUE) + arc_acc_time.optString(Constants.UNIT)));
                            JSONObject rise_height = scan_cut.optJSONObject("抬头高度");
                            if (isNotNull(rise_height)) mDatas.add(new FileBean(16, 2, "抬头高度:" + rise_height.optString(Constants.VALUE) + rise_height.optString(Constants.UNIT)));
                            JSONObject tresis_mode = scan_cut.optJSONObject("穿孔模式");
                            if (isNotNull(tresis_mode)) mDatas.add(new FileBean(17, 2, "穿孔模式:" + tresis_mode.optString(Constants.VALUE) + tresis_mode.optString(Constants.UNIT)));
                            JSONObject follow_mode = scan_cut.optJSONObject("随动模式");
                            if (isNotNull(follow_mode)) mDatas.add(new FileBean(18, 2, "随动模式:" + follow_mode.optString(Constants.VALUE) + follow_mode.optString(Constants.UNIT)));
                        }
                        JSONObject stand_cut = cut.optJSONObject("标准切割");
                        if (isNotNull(stand_cut)) {
                            mDatas.add(new FileBean(19, 1, "标准切割"));
                            JSONObject cut_power = stand_cut.optJSONObject("切割功率");
                            if (isNotNull(cut_power)) mDatas.add(new FileBean(20, 19, "切割功率:" + cut_power.optString(Constants.VALUE) + cut_power.optString(Constants.UNIT)));
                            JSONObject cut_acc = stand_cut.optJSONObject("切割加速度");
                            if (isNotNull(cut_acc)) mDatas.add(new FileBean(21, 19, "切割加速度:" + cut_acc.optString(Constants.VALUE) + cut_acc.optString(Constants.UNIT)));
                            JSONObject cut_acc_time = stand_cut.optJSONObject("切割加速时间");
                            if (isNotNull(cut_acc_time)) mDatas.add(new FileBean(22, 19, "切割加速时间:" + cut_acc_time.optString(Constants.VALUE) + cut_acc_time.optString(Constants.UNIT)));
                            JSONObject cut_duty = stand_cut.optJSONObject("切割占空比");
                            if (isNotNull(cut_duty)) mDatas.add(new FileBean(23, 19, "切割占空比:" + cut_duty.optString(Constants.VALUE) + cut_duty.optString(Constants.UNIT)));
                            JSONObject cut_gas = stand_cut.optJSONObject("切割气体");
                            if (isNotNull(cut_gas)) mDatas.add(new FileBean(24, 19, "切割气体:" + cut_gas.optString(Constants.VALUE) + cut_gas.optString(Constants.UNIT)));
                            JSONObject cut_gas_pre = stand_cut.optJSONObject("切割气压");
                            if (isNotNull(cut_gas_pre)) mDatas.add(new FileBean(25, 19, "切割气压:" + cut_gas_pre.optString(Constants.VALUE) + cut_gas_pre.optString(Constants.UNIT)));
                            JSONObject cut_focus = stand_cut.optJSONObject("切割焦点");
                            if (isNotNull(cut_focus)) mDatas.add(new FileBean(26, 19, "切割焦点:" + cut_focus.optString(Constants.VALUE) + cut_focus.optString(Constants.UNIT)));
                            JSONObject cut_accuracy = stand_cut.optJSONObject("切割精度");
                            if (isNotNull(cut_accuracy)) mDatas.add(new FileBean(27, 19, "切割精度:" + cut_accuracy.optString(Constants.VALUE) + cut_accuracy.optString(Constants.UNIT)));
                            JSONObject cut_speed = stand_cut.optJSONObject("切割速度");
                            if (isNotNull(cut_speed)) mDatas.add(new FileBean(28, 19, "切割速度:" + cut_speed.optString(Constants.VALUE) + cut_speed.optString(Constants.UNIT)));
                            JSONObject cut_freq = stand_cut.optJSONObject("切割频率");
                            if (isNotNull(cut_freq)) mDatas.add(new FileBean(29, 19, "切割频率:" + cut_freq.optString(Constants.VALUE) + cut_freq.optString(Constants.UNIT)));
                            JSONObject cut_height = stand_cut.optJSONObject("切割高度");
                            if (isNotNull(cut_height)) mDatas.add(new FileBean(30, 19, "切割高度:" + cut_height.optString(Constants.VALUE) + cut_height.optString(Constants.UNIT)));
                            JSONObject power_ctrl = stand_cut.optJSONObject("功率控制");
                            if (isNotNull(power_ctrl)) mDatas.add(new FileBean(31, 19, "功率控制:" + power_ctrl.optString(Constants.VALUE) + power_ctrl.optString(Constants.UNIT)));
                            JSONObject arc_acc_time = stand_cut.optJSONObject("圆弧加速时间");
                            if (isNotNull(arc_acc_time)) mDatas.add(new FileBean(32, 19, "圆弧加速时间:" + arc_acc_time.optString(Constants.VALUE) + arc_acc_time.optString(Constants.UNIT)));
                            JSONObject rise_height = stand_cut.optJSONObject("抬头高度");
                            if (isNotNull(rise_height)) mDatas.add(new FileBean(33, 19, "抬头高度:" + rise_height.optString(Constants.VALUE) + rise_height.optString(Constants.UNIT)));
                            JSONObject tresis_mode = stand_cut.optJSONObject("穿孔模式");
                            if (isNotNull(tresis_mode)) mDatas.add(new FileBean(34, 19, "穿孔模式:" + tresis_mode.optString(Constants.VALUE) + tresis_mode.optString(Constants.UNIT)));
                            JSONObject follow_mode = stand_cut.optJSONObject("随动模式");
                            if (isNotNull(follow_mode)) mDatas.add(new FileBean(35, 19, "随动模式:" + follow_mode.optString(Constants.VALUE) + follow_mode.optString(Constants.UNIT)));
                        }
                        JSONObject mark = cut.optJSONObject("标刻");
                        if (isNotNull(mark)) {
                            mDatas.add(new FileBean(36, 1, "标刻"));
                            JSONObject cut_power = mark.optJSONObject("切割功率");
                            if (isNotNull(cut_power)) mDatas.add(new FileBean(37, 36, "切割功率:" + cut_power.optString(Constants.VALUE) + cut_power.optString(Constants.UNIT)));
                            JSONObject cut_acc = mark.optJSONObject("切割加速度");
                            if (isNotNull(cut_acc)) mDatas.add(new FileBean(38, 36, "切割加速度:" + cut_acc.optString(Constants.VALUE) + cut_acc.optString(Constants.UNIT)));
                            JSONObject cut_acc_time = mark.optJSONObject("切割加速时间");
                            if (isNotNull(cut_acc_time)) mDatas.add(new FileBean(39, 36, "切割加速时间:" + cut_acc_time.optString(Constants.VALUE) + cut_acc_time.optString(Constants.UNIT)));
                            JSONObject cut_duty = mark.optJSONObject("切割占空比");
                            if (isNotNull(cut_duty)) mDatas.add(new FileBean(40, 36, "切割占空比:" + cut_duty.optString(Constants.VALUE) + cut_duty.optString(Constants.UNIT)));
                            JSONObject cut_gas = mark.optJSONObject("切割气体");
                            if (isNotNull(cut_gas)) mDatas.add(new FileBean(41, 36, "切割气体:" + cut_gas.optString(Constants.VALUE) + cut_gas.optString(Constants.UNIT)));
                            JSONObject cut_gas_pre = mark.optJSONObject("切割气压");
                            if (isNotNull(cut_gas_pre)) mDatas.add(new FileBean(42, 36, "切割气压:" + cut_gas_pre.optString(Constants.VALUE) + cut_gas_pre.optString(Constants.UNIT)));
                            JSONObject cut_focus = mark.optJSONObject("切割焦点");
                            if (isNotNull(cut_focus)) mDatas.add(new FileBean(43, 36, "切割焦点:" + cut_focus.optString(Constants.VALUE) + cut_focus.optString(Constants.UNIT)));
                            JSONObject cut_accuracy = mark.optJSONObject("切割精度");
                            if (isNotNull(cut_accuracy)) mDatas.add(new FileBean(44, 36, "切割精度:" + cut_accuracy.optString(Constants.VALUE) + cut_accuracy.optString(Constants.UNIT)));
                            JSONObject cut_speed = mark.optJSONObject("切割速度");
                            if (isNotNull(cut_speed)) mDatas.add(new FileBean(45, 36, "切割速度:" + cut_speed.optString(Constants.VALUE) + cut_speed.optString(Constants.UNIT)));
                            JSONObject cut_freq = mark.optJSONObject("切割频率");
                            if (isNotNull(cut_freq)) mDatas.add(new FileBean(46, 36, "切割频率:" + cut_freq.optString(Constants.VALUE) + cut_freq.optString(Constants.UNIT)));
                            JSONObject cut_height = mark.optJSONObject("切割高度");
                            if (isNotNull(cut_height)) mDatas.add(new FileBean(47, 36, "切割高度:" + cut_height.optString(Constants.VALUE) + cut_height.optString(Constants.UNIT)));
                            JSONObject power_ctrl = mark.optJSONObject("功率控制");
                            if (isNotNull(power_ctrl)) mDatas.add(new FileBean(48, 36, "功率控制:" + power_ctrl.optString(Constants.VALUE) + power_ctrl.optString(Constants.UNIT)));
                            JSONObject arc_acc_time = mark.optJSONObject("圆弧加速时间");
                            if (isNotNull(arc_acc_time)) mDatas.add(new FileBean(49, 36, "圆弧加速时间:" + arc_acc_time.optString(Constants.VALUE) + arc_acc_time.optString(Constants.UNIT)));
                            JSONObject rise_height = mark.optJSONObject("抬头高度");
                            if (isNotNull(rise_height)) mDatas.add(new FileBean(50, 36, "抬头高度:" + rise_height.optString(Constants.VALUE) + rise_height.optString(Constants.UNIT)));
                            JSONObject tresis_mode = mark.optJSONObject("穿孔模式");
                            if (isNotNull(tresis_mode)) mDatas.add(new FileBean(51, 36, "穿孔模式:" + tresis_mode.optString(Constants.VALUE) + tresis_mode.optString(Constants.UNIT)));
                            JSONObject follow_mode = mark.optJSONObject("随动模式");
                            if (isNotNull(follow_mode)) mDatas.add(new FileBean(52, 36, "随动模式:" + follow_mode.optString(Constants.VALUE) + follow_mode.optString(Constants.UNIT)));
                        }
                        JSONObject spline_cut = cut.optJSONObject("样条切割");
                        if (isNotNull(spline_cut)) {
                            mDatas.add(new FileBean(53, 1, "样条切割"));
                            JSONObject cut_power = spline_cut.optJSONObject("切割功率");
                            if (isNotNull(cut_power)) mDatas.add(new FileBean(54, 53, "切割功率:" + cut_power.optString(Constants.VALUE) + cut_power.optString(Constants.UNIT)));
                            JSONObject cut_acc = spline_cut.optJSONObject("切割加速度");
                            if (isNotNull(cut_acc)) mDatas.add(new FileBean(55, 53, "切割加速度:" + cut_acc.optString(Constants.VALUE) + cut_acc.optString(Constants.UNIT)));
                            JSONObject cut_acc_time = spline_cut.optJSONObject("切割加速时间");
                            if (isNotNull(cut_acc_time)) mDatas.add(new FileBean(56, 53, "切割加速时间:" + cut_acc_time.optString(Constants.VALUE) + cut_acc_time.optString(Constants.UNIT)));
                            JSONObject cut_duty = spline_cut.optJSONObject("切割占空比");
                            if (isNotNull(cut_duty)) mDatas.add(new FileBean(57, 53, "切割占空比:" + cut_duty.optString(Constants.VALUE) + cut_duty.optString(Constants.UNIT)));
                            JSONObject cut_gas = spline_cut.optJSONObject("切割气体");
                            if (isNotNull(cut_gas)) mDatas.add(new FileBean(58, 53, "切割气体:" + cut_gas.optString(Constants.VALUE) + cut_gas.optString(Constants.UNIT)));
                            JSONObject cut_gas_pre = spline_cut.optJSONObject("切割气压");
                            if (isNotNull(cut_gas_pre)) mDatas.add(new FileBean(59, 53, "切割气压:" + cut_gas_pre.optString(Constants.VALUE) + cut_gas_pre.optString(Constants.UNIT)));
                            JSONObject cut_focus = spline_cut.optJSONObject("切割焦点");
                            if (isNotNull(cut_focus)) mDatas.add(new FileBean(60, 53, "切割焦点:" + cut_focus.optString(Constants.VALUE) + cut_focus.optString(Constants.UNIT)));
                            JSONObject cut_accuracy = spline_cut.optJSONObject("切割精度");
                            if (isNotNull(cut_accuracy)) mDatas.add(new FileBean(61, 53, "切割精度:" + cut_accuracy.optString(Constants.VALUE) + cut_accuracy.optString(Constants.UNIT)));
                            JSONObject cut_speed = spline_cut.optJSONObject("切割速度");
                            if (isNotNull(cut_speed)) mDatas.add(new FileBean(62, 53, "切割速度:" + cut_speed.optString(Constants.VALUE) + cut_speed.optString(Constants.UNIT)));
                            JSONObject cut_freq = spline_cut.optJSONObject("切割频率");
                            if (isNotNull(cut_freq)) mDatas.add(new FileBean(63, 53, "切割频率:" + cut_freq.optString(Constants.VALUE) + cut_freq.optString(Constants.UNIT)));
                            JSONObject cut_height = spline_cut.optJSONObject("切割高度");
                            if (isNotNull(cut_height)) mDatas.add(new FileBean(64, 53, "切割高度:" + cut_height.optString(Constants.VALUE) + cut_height.optString(Constants.UNIT)));
                            JSONObject power_ctrl = spline_cut.optJSONObject("功率控制");
                            if (isNotNull(power_ctrl)) mDatas.add(new FileBean(65, 53, "功率控制:" + power_ctrl.optString(Constants.VALUE) + power_ctrl.optString(Constants.UNIT)));
                            JSONObject arc_acc_time = spline_cut.optJSONObject("圆弧加速时间");
                            if (isNotNull(arc_acc_time)) mDatas.add(new FileBean(66, 53, "圆弧加速时间:" + arc_acc_time.optString(Constants.VALUE) + arc_acc_time.optString(Constants.UNIT)));
                            JSONObject rise_height = spline_cut.optJSONObject("抬头高度");
                            if (isNotNull(rise_height)) mDatas.add(new FileBean(67, 53, "抬头高度:" + rise_height.optString(Constants.VALUE) + rise_height.optString(Constants.UNIT)));
                            JSONObject tresis_mode = spline_cut.optJSONObject("穿孔模式");
                            if (isNotNull(tresis_mode)) mDatas.add(new FileBean(68, 53, "穿孔模式:" + tresis_mode.optString(Constants.VALUE) + tresis_mode.optString(Constants.UNIT)));
                            JSONObject follow_mode = spline_cut.optJSONObject("随动模式");
                            if (isNotNull(follow_mode)) mDatas.add(new FileBean(69, 53, "随动模式:" + follow_mode.optString(Constants.VALUE) + follow_mode.optString(Constants.UNIT)));
                        }
                        JSONObject burn_membrane = cut.optJSONObject("烧膜");
                        if (isNotNull(burn_membrane)) {
                            mDatas.add(new FileBean(70, 1, "烧膜"));
                            JSONObject cut_power = burn_membrane.optJSONObject("切割功率");
                            if (isNotNull(cut_power)) mDatas.add(new FileBean(71, 70, "切割功率:" + cut_power.optString(Constants.VALUE) + cut_power.optString(Constants.UNIT)));
                            JSONObject cut_acc = burn_membrane.optJSONObject("切割加速度");
                            if (isNotNull(cut_acc)) mDatas.add(new FileBean(72, 70, "切割加速度:" + cut_acc.optString(Constants.VALUE) + cut_acc.optString(Constants.UNIT)));
                            JSONObject cut_acc_time = burn_membrane.optJSONObject("切割加速时间");
                            if (isNotNull(cut_acc_time)) mDatas.add(new FileBean(73, 70, "切割加速时间:" + cut_acc_time.optString(Constants.VALUE) + cut_acc_time.optString(Constants.UNIT)));
                            JSONObject cut_duty = burn_membrane.optJSONObject("切割占空比");
                            if (isNotNull(cut_duty)) mDatas.add(new FileBean(74, 70, "切割占空比:" + cut_duty.optString(Constants.VALUE) + cut_duty.optString(Constants.UNIT)));
                            JSONObject cut_gas = burn_membrane.optJSONObject("切割气体");
                            if (isNotNull(cut_gas)) mDatas.add(new FileBean(75, 70, "切割气体:" + cut_gas.optString(Constants.VALUE) + cut_gas.optString(Constants.UNIT)));
                            JSONObject cut_gas_pre = burn_membrane.optJSONObject("切割气压");
                            if (isNotNull(cut_gas_pre)) mDatas.add(new FileBean(76, 70, "切割气压:" + cut_gas_pre.optString(Constants.VALUE) + cut_gas_pre.optString(Constants.UNIT)));
                            JSONObject cut_focus = burn_membrane.optJSONObject("切割焦点");
                            if (isNotNull(cut_focus)) mDatas.add(new FileBean(77, 70, "切割焦点:" + cut_focus.optString(Constants.VALUE) + cut_focus.optString(Constants.UNIT)));
                            JSONObject cut_accuracy = burn_membrane.optJSONObject("切割精度");
                            if (isNotNull(cut_accuracy)) mDatas.add(new FileBean(78, 70, "切割精度:" + cut_accuracy.optString(Constants.VALUE) + cut_accuracy.optString(Constants.UNIT)));
                            JSONObject cut_speed = burn_membrane.optJSONObject("切割速度");
                            if (isNotNull(cut_speed)) mDatas.add(new FileBean(79, 70, "切割速度:" + cut_speed.optString(Constants.VALUE) + cut_speed.optString(Constants.UNIT)));
                            JSONObject cut_freq = burn_membrane.optJSONObject("切割频率");
                            if (isNotNull(cut_freq)) mDatas.add(new FileBean(80, 70, "切割频率:" + cut_freq.optString(Constants.VALUE) + cut_freq.optString(Constants.UNIT)));
                            JSONObject cut_height = burn_membrane.optJSONObject("切割高度");
                            if (isNotNull(cut_height)) mDatas.add(new FileBean(81, 70, "切割高度:" + cut_height.optString(Constants.VALUE) + cut_height.optString(Constants.UNIT)));
                            JSONObject power_ctrl = burn_membrane.optJSONObject("功率控制");
                            if (isNotNull(power_ctrl)) mDatas.add(new FileBean(82, 70, "功率控制:" + power_ctrl.optString(Constants.VALUE) + power_ctrl.optString(Constants.UNIT)));
                            JSONObject arc_acc_time = burn_membrane.optJSONObject("圆弧加速时间");
                            if (isNotNull(arc_acc_time)) mDatas.add(new FileBean(83, 70, "圆弧加速时间:" + arc_acc_time.optString(Constants.VALUE) + arc_acc_time.optString(Constants.UNIT)));
                            JSONObject rise_height = burn_membrane.optJSONObject("抬头高度");
                            if (isNotNull(rise_height)) mDatas.add(new FileBean(84, 70, "抬头高度:" + rise_height.optString(Constants.VALUE) + rise_height.optString(Constants.UNIT)));
                            JSONObject tresis_mode = burn_membrane.optJSONObject("穿孔模式");
                            if (isNotNull(tresis_mode)) mDatas.add(new FileBean(85, 70, "穿孔模式:" + tresis_mode.optString(Constants.VALUE) + tresis_mode.optString(Constants.UNIT)));
                            JSONObject follow_mode = burn_membrane.optJSONObject("随动模式");
                            if (isNotNull(follow_mode)) mDatas.add(new FileBean(86, 70, "随动模式:" + follow_mode.optString(Constants.VALUE) + follow_mode.optString(Constants.UNIT)));
                        }
                        JSONObject exq_cut = cut.optJSONObject("精细切割");
                        if (isNotNull(exq_cut)) {
                            mDatas.add(new FileBean(87, 1, "精细切割"));
                            JSONObject cut_power = exq_cut.optJSONObject("切割功率");
                            if (isNotNull(cut_power)) mDatas.add(new FileBean(88, 87, "切割功率:" + cut_power.optString(Constants.VALUE) + cut_power.optString(Constants.UNIT)));
                            JSONObject cut_acc = exq_cut.optJSONObject("切割加速度");
                            if (isNotNull(cut_acc)) mDatas.add(new FileBean(89, 87, "切割加速度:" + cut_acc.optString(Constants.VALUE) + cut_acc.optString(Constants.UNIT)));
                            JSONObject cut_acc_time = exq_cut.optJSONObject("切割加速时间");
                            if (isNotNull(cut_acc_time)) mDatas.add(new FileBean(90, 87, "切割加速时间:" + cut_acc_time.optString(Constants.VALUE) + cut_acc_time.optString(Constants.UNIT)));
                            JSONObject cut_duty = exq_cut.optJSONObject("切割占空比");
                            if (isNotNull(cut_duty)) mDatas.add(new FileBean(91, 87, "切割占空比:" + cut_duty.optString(Constants.VALUE) + cut_duty.optString(Constants.UNIT)));
                            JSONObject cut_gas = exq_cut.optJSONObject("切割气体");
                            if (isNotNull(cut_gas)) mDatas.add(new FileBean(92, 87, "切割气体:" + cut_gas.optString(Constants.VALUE) + cut_gas.optString(Constants.UNIT)));
                            JSONObject cut_gas_pre = exq_cut.optJSONObject("切割气压");
                            if (isNotNull(cut_gas_pre)) mDatas.add(new FileBean(93, 87, "切割气压:" + cut_gas_pre.optString(Constants.VALUE) + cut_gas_pre.optString(Constants.UNIT)));
                            JSONObject cut_focus = exq_cut.optJSONObject("切割焦点");
                            if (isNotNull(cut_focus)) mDatas.add(new FileBean(94, 87, "切割焦点:" + cut_focus.optString(Constants.VALUE) + cut_focus.optString(Constants.UNIT)));
                            JSONObject cut_accuracy = exq_cut.optJSONObject("切割精度");
                            if (isNotNull(cut_accuracy)) mDatas.add(new FileBean(95, 87, "切割精度:" + cut_accuracy.optString(Constants.VALUE) + cut_accuracy.optString(Constants.UNIT)));
                            JSONObject cut_speed = exq_cut.optJSONObject("切割速度");
                            if (isNotNull(cut_speed)) mDatas.add(new FileBean(96, 87, "切割速度:" + cut_speed.optString(Constants.VALUE) + cut_speed.optString(Constants.UNIT)));
                            JSONObject cut_freq = exq_cut.optJSONObject("切割频率");
                            if (isNotNull(cut_freq)) mDatas.add(new FileBean(97, 87, "切割频率:" + cut_freq.optString(Constants.VALUE) + cut_freq.optString(Constants.UNIT)));
                            JSONObject cut_height = exq_cut.optJSONObject("切割高度");
                            if (isNotNull(cut_height)) mDatas.add(new FileBean(98, 87, "切割高度:" + cut_height.optString(Constants.VALUE) + cut_height.optString(Constants.UNIT)));
                            JSONObject power_ctrl = exq_cut.optJSONObject("功率控制");
                            if (isNotNull(power_ctrl)) mDatas.add(new FileBean(99, 87, "功率控制:" + power_ctrl.optString(Constants.VALUE) + power_ctrl.optString(Constants.UNIT)));
                            JSONObject arc_acc_time = exq_cut.optJSONObject("圆弧加速时间");
                            if (isNotNull(arc_acc_time)) mDatas.add(new FileBean(100, 87, "圆弧加速时间:" + arc_acc_time.optString(Constants.VALUE) + arc_acc_time.optString(Constants.UNIT)));
                            JSONObject rise_height = exq_cut.optJSONObject("抬头高度");
                            if (isNotNull(rise_height)) mDatas.add(new FileBean(101, 87, "抬头高度:" + rise_height.optString(Constants.VALUE) + rise_height.optString(Constants.UNIT)));
                            JSONObject tresis_mode = exq_cut.optJSONObject("穿孔模式");
                            if (isNotNull(tresis_mode)) mDatas.add(new FileBean(102, 87, "穿孔模式:" + tresis_mode.optString(Constants.VALUE) + tresis_mode.optString(Constants.UNIT)));
                            JSONObject follow_mode = exq_cut.optJSONObject("随动模式");
                            if (isNotNull(follow_mode)) mDatas.add(new FileBean(103, 87, "随动模式:" + follow_mode.optString(Constants.VALUE) + follow_mode.optString(Constants.UNIT)));
                        }
                    }
                    JSONObject base = sraft_db.optJSONObject("基本");
                    if (isNotNull(base)) {
                        mDatas.add(new FileBean(104, 0, "基本"));
                        JSONObject cut_mouth = base.optJSONObject("切割嘴");
                        if (isNotNull(cut_mouth)) mDatas.add(new FileBean(105, 104, "切割嘴:" + cut_mouth.optString(Constants.VALUE) + cut_mouth.optString(Constants.UNIT)));
                        JSONObject thickness = base.optJSONObject("厚度");
                        if (isNotNull(thickness)) mDatas.add(new FileBean(106, 104, "厚度:" + thickness.optString(Constants.VALUE) + thickness.optString(Constants.UNIT)));
                        JSONObject material = base.optJSONObject("材料");
                        if (isNotNull(material)) mDatas.add(new FileBean(107, 104, "材料:" + material.optString(Constants.VALUE) + material.optString(Constants.UNIT)));
                        JSONObject gas = base.optJSONObject("气体");
                        if (isNotNull(gas)) mDatas.add(new FileBean(108, 104, "气体:" + gas.optString(Constants.VALUE) + gas.optString(Constants.UNIT)));
                        JSONObject laser_power = base.optJSONObject("激光功率");
                        if (isNotNull(laser_power)) mDatas.add(new FileBean(109, 104, "激光功率:" + laser_power.optString(Constants.VALUE) + laser_power.optString(Constants.UNIT)));
                        JSONObject focus_lens = base.optJSONObject("聚焦镜");
                        if (isNotNull(focus_lens)) mDatas.add(new FileBean(110, 104, "聚焦镜:" + focus_lens.optString(Constants.VALUE) + focus_lens.optString(Constants.UNIT)));
                        JSONObject explain = base.optJSONObject("说明");
                        if (isNotNull(explain)) mDatas.add(new FileBean(111, 104, "说明:" + explain.optString(Constants.VALUE) + explain.optString(Constants.UNIT)));
                    }
                    JSONObject machine = sraft_db.optJSONObject("机床");
                    if (isNotNull(machine)) {
                        mDatas.add(new FileBean(112, 0, "机床"));
                        JSONObject slow_start = machine.optJSONObject("慢速起刀功能");
                        if (isNotNull(slow_start)) {
                            mDatas.add(new FileBean(113, 112, "慢速起刀功能"));
                            JSONObject on_off = slow_start.optJSONObject("功能开关");
                            if (isNotNull(on_off)) mDatas.add(new FileBean(114, 113, "功能开关:" + on_off.optString(Constants.VALUE) + on_off.optString(Constants.UNIT)));
                            JSONObject start_rate = slow_start.optJSONObject("起刀倍率");
                            if (isNotNull(start_rate)) mDatas.add(new FileBean(115, 113, "起刀倍率:" + start_rate.optString(Constants.VALUE) + start_rate.optString(Constants.UNIT)));
                            JSONObject start_time = slow_start.optJSONObject("起刀时间");
                            if (isNotNull(start_time)) mDatas.add(new FileBean(116, 113, "起刀时间:" + start_time.optString(Constants.VALUE) + start_time.optString(Constants.UNIT)));
                        }
                        JSONObject gas_delay = machine.optJSONObject("气体延时功能");
                        if (isNotNull(gas_delay)) {
                            mDatas.add(new FileBean(117, 112, "气体延时功能"));
                            JSONObject on_off = gas_delay.optJSONObject("功能开关");
                            if (isNotNull(on_off)) mDatas.add(new FileBean(118, 117, "功能开关:" + on_off.optString(Constants.VALUE) + on_off.optString(Constants.UNIT)));
                            JSONObject oxygen_delay = gas_delay.optJSONObject("氧气延时时间");
                            if (isNotNull(oxygen_delay)) mDatas.add(new FileBean(119, 117, "氧气延时时间:" + oxygen_delay.optString(Constants.VALUE) + oxygen_delay.optString(Constants.UNIT)));
                            JSONObject nitrogen_delay = gas_delay.optJSONObject("氮气延时时间");
                            if (isNotNull(nitrogen_delay)) mDatas.add(new FileBean(120, 117, "氮气延时时间:" + nitrogen_delay.optString(Constants.VALUE) + nitrogen_delay.optString(Constants.UNIT)));
                        }
                        JSONObject tresis_fillet = machine.optJSONObject("穿孔圆角功能");
                        if (isNotNull(tresis_fillet)) {
                            mDatas.add(new FileBean(121, 112, "穿孔圆角功能"));
                            JSONObject circle_radius = tresis_fillet.optJSONObject("切圆半径");
                            if (isNotNull(circle_radius)) mDatas.add(new FileBean(122, 121, "切圆半径:" + circle_radius.optString(Constants.VALUE) + circle_radius.optString(Constants.UNIT)));
                            JSONObject circle_speed = tresis_fillet.optJSONObject("切圆速度");
                            if (isNotNull(circle_speed)) mDatas.add(new FileBean(123, 121, "切圆速度:" + circle_speed.optString(Constants.VALUE) + circle_speed.optString(Constants.UNIT)));
                            JSONObject on_off = tresis_fillet.optJSONObject("功能开关");
                            if (isNotNull(on_off)) mDatas.add(new FileBean(124, 121, "功能开关:" + on_off.optString(Constants.VALUE) + on_off.optString(Constants.UNIT)));
                        }
                        JSONObject energy_param = machine.optJSONObject("能量控制参数");
                        if (isNotNull(energy_param)) {
                            mDatas.add(new FileBean(125, 112, "能量控制参数"));
                            JSONObject boot_speed = energy_param.optJSONObject("启动速度");
                            if (isNotNull(boot_speed)) mDatas.add(new FileBean(126, 125, "启动速度:" + boot_speed.optString(Constants.VALUE) + boot_speed.optString(Constants.UNIT)));
                            JSONObject corner_power = energy_param.optJSONObject("拐角功率");
                            if (isNotNull(corner_power)) mDatas.add(new FileBean(127, 125, "拐角功率:" + corner_power.optString(Constants.VALUE) + corner_power.optString(Constants.UNIT)));
                            JSONObject corner_duty = energy_param.optJSONObject("拐角占空比");
                            if (isNotNull(corner_duty)) mDatas.add(new FileBean(128, 125, "拐角占空比:" + corner_duty.optString(Constants.VALUE) + corner_duty.optString(Constants.UNIT)));
                            JSONObject corner_speed = energy_param.optJSONObject("拐角速度");
                            if (isNotNull(corner_speed)) mDatas.add(new FileBean(129, 125, "拐角速度:" + corner_speed.optString(Constants.VALUE) + corner_speed.optString(Constants.UNIT)));
                            JSONObject ctrl_type = energy_param.optJSONObject("控制类型");
                            if (isNotNull(ctrl_type)) mDatas.add(new FileBean(130, 125, "控制类型:" + ctrl_type.optString(Constants.VALUE) + ctrl_type.optString(Constants.UNIT)));
                        }
                        JSONObject leapfrog_param = machine.optJSONObject("蛙跳功能参数");
                        if (isNotNull(leapfrog_param)) {
                            mDatas.add(new FileBean(131, 112, "蛙跳功能参数"));
                            JSONObject ahead_distance = leapfrog_param.optJSONObject("提前距离");
                            if (isNotNull(ahead_distance)) mDatas.add(new FileBean(132, 131, "提前距离:" + ahead_distance.optString(Constants.VALUE) + ahead_distance.optString(Constants.UNIT)));
                            JSONObject hump_height = leapfrog_param.optJSONObject("起跳高度");
                            if (isNotNull(hump_height)) mDatas.add(new FileBean(133, 131, "起跳高度:" + hump_height.optString(Constants.VALUE) + hump_height.optString(Constants.UNIT)));
                        }
                    }
                    JSONObject tresis = sraft_db.optJSONObject("穿孔");
                    if (isNotNull(tresis)) {
                        mDatas.add(new FileBean(134, 0, "穿孔"));
                        JSONObject tresis_center = tresis.optJSONObject("多级穿孔中位");
                        if (isNotNull(tresis_center)) {
                            mDatas.add(new FileBean(135, 134, "多级穿孔中位"));
                            JSONObject tresis_power = tresis_center.optJSONObject("穿孔功率");
                            if (isNotNull(tresis_power)) mDatas.add(new FileBean(136, 135, "穿孔功率:" + tresis_power.optString(Constants.VALUE) + tresis_power.optString(Constants.UNIT)));
                            JSONObject tresis_duty = tresis_center.optJSONObject("穿孔占空比");
                            if (isNotNull(tresis_duty)) mDatas.add(new FileBean(137, 135, "穿孔占空比:" + tresis_duty.optString(Constants.VALUE) + tresis_duty.optString(Constants.UNIT)));
                            JSONObject tresis_time = tresis_center.optJSONObject("穿孔时间");
                            if (isNotNull(tresis_time)) mDatas.add(new FileBean(138, 135, "穿孔时间:" + tresis_time.optString(Constants.VALUE) + tresis_time.optString(Constants.UNIT)));
                            JSONObject tresis_pressure = tresis_center.optJSONObject("穿孔气压");
                            if (isNotNull(tresis_pressure)) mDatas.add(new FileBean(139, 135, "穿孔气压:" + tresis_pressure.optString(Constants.VALUE) + tresis_pressure.optString(Constants.UNIT)));
                            JSONObject tresis_freq = tresis_center.optJSONObject("穿孔频率");
                            if (isNotNull(tresis_freq)) mDatas.add(new FileBean(140, 135, "穿孔频率:" + tresis_freq.optString(Constants.VALUE) + tresis_freq.optString(Constants.UNIT)));
                            JSONObject tresis_height = tresis_center.optJSONObject("穿孔高度");
                            if (isNotNull(tresis_height)) mDatas.add(new FileBean(141, 135, "穿孔高度:" + tresis_height.optString(Constants.VALUE) + tresis_height.optString(Constants.UNIT)));
                        }
                        JSONObject tresis_low = tresis.optJSONObject("多级穿孔低位");
                        if (isNotNull(tresis_low)) {
                            mDatas.add(new FileBean(142, 134, "多级穿孔低位"));
                            JSONObject tresis_power = tresis_low.optJSONObject("穿孔功率");
                            if (isNotNull(tresis_power)) mDatas.add(new FileBean(143, 142, "穿孔功率:" + tresis_power.optString(Constants.VALUE) + tresis_power.optString(Constants.UNIT)));
                            JSONObject tresis_duty = tresis_low.optJSONObject("穿孔占空比");
                            if (isNotNull(tresis_duty)) mDatas.add(new FileBean(144, 142, "穿孔占空比:" + tresis_duty.optString(Constants.VALUE) + tresis_duty.optString(Constants.UNIT)));
                            JSONObject tresis_time = tresis_low.optJSONObject("穿孔时间");
                            if (isNotNull(tresis_time)) mDatas.add(new FileBean(145, 142, "穿孔时间:" + tresis_time.optString(Constants.VALUE) + tresis_time.optString(Constants.UNIT)));
                            JSONObject tresis_pressure = tresis_low.optJSONObject("穿孔气压");
                            if (isNotNull(tresis_pressure)) mDatas.add(new FileBean(146, 142, "穿孔气压:" + tresis_pressure.optString(Constants.VALUE) + tresis_pressure.optString(Constants.UNIT)));
                            JSONObject tresis_freq = tresis_low.optJSONObject("穿孔频率");
                            if (isNotNull(tresis_freq)) mDatas.add(new FileBean(147, 142, "穿孔频率:" + tresis_freq.optString(Constants.VALUE) + tresis_freq.optString(Constants.UNIT)));
                            JSONObject tresis_height = tresis_low.optJSONObject("穿孔高度");
                            if (isNotNull(tresis_height)) mDatas.add(new FileBean(148, 142, "穿孔高度:" + tresis_height.optString(Constants.VALUE) + tresis_height.optString(Constants.UNIT)));
                        }
                        JSONObject tresis_and_gas = tresis.optJSONObject("多级穿孔次数及气体选择");
                        if (isNotNull(tresis_and_gas)) {
                            mDatas.add(new FileBean(149, 134, "多级穿孔次数及气体选择"));
                            JSONObject tresis_times = tresis_and_gas.optJSONObject("穿孔次数");
                            if (isNotNull(tresis_times)) mDatas.add(new FileBean(150, 149, "穿孔次数:" + tresis_times.optString(Constants.VALUE) + tresis_times.optString(Constants.UNIT)));
                            JSONObject tresis_gas = tresis_and_gas.optJSONObject("穿孔气体");
                            if (isNotNull(tresis_gas)) mDatas.add(new FileBean(151, 149, "穿孔气体:" + tresis_gas.optString(Constants.VALUE) + tresis_gas.optString(Constants.UNIT)));
                        }
                        JSONObject tresis_high = tresis.optJSONObject("多级穿孔高位");
                        if (isNotNull(tresis_high)) {
                            mDatas.add(new FileBean(152, 134, "多级穿孔高位"));
                            JSONObject tresis_power = tresis_high.optJSONObject("穿孔功率");
                            if (isNotNull(tresis_power)) mDatas.add(new FileBean(153, 152, "穿孔功率:" + tresis_power.optString(Constants.VALUE) + tresis_power.optString(Constants.UNIT)));
                            JSONObject tresis_duty = tresis_high.optJSONObject("穿孔占空比");
                            if (isNotNull(tresis_duty)) mDatas.add(new FileBean(154, 152, "穿孔占空比:" + tresis_duty.optString(Constants.VALUE) + tresis_duty.optString(Constants.UNIT)));
                            JSONObject tresis_time = tresis_high.optJSONObject("穿孔时间");
                            if (isNotNull(tresis_time)) mDatas.add(new FileBean(155, 152, "穿孔时间:" + tresis_time.optString(Constants.VALUE) + tresis_time.optString(Constants.UNIT)));
                            JSONObject tresis_pressure = tresis_high.optJSONObject("穿孔气压");
                            if (isNotNull(tresis_pressure)) mDatas.add(new FileBean(156, 152, "穿孔气压:" + tresis_pressure.optString(Constants.VALUE) + tresis_pressure.optString(Constants.UNIT)));
                            JSONObject tresis_freq = tresis_high.optJSONObject("穿孔频率");
                            if (isNotNull(tresis_freq)) mDatas.add(new FileBean(157, 152, "穿孔频率:" + tresis_freq.optString(Constants.VALUE) + tresis_freq.optString(Constants.UNIT)));
                            JSONObject tresis_height = tresis_high.optJSONObject("穿孔高度");
                            if (isNotNull(tresis_height)) mDatas.add(new FileBean(158, 152, "穿孔高度:" + tresis_height.optString(Constants.VALUE) + tresis_height.optString(Constants.UNIT)));
                        }
                        JSONObject tresis_normal = tresis.optJSONObject("普通穿孔");
                        if (isNotNull(tresis_normal)) {
                            mDatas.add(new FileBean(159, 134, "普通穿孔"));
                            JSONObject tresis_power = tresis_normal.optJSONObject("穿孔功率");
                            if (isNotNull(tresis_power)) mDatas.add(new FileBean(160, 159, "穿孔功率:" + tresis_power.optString(Constants.VALUE) + tresis_power.optString(Constants.UNIT)));
                            JSONObject tresis_duty = tresis_normal.optJSONObject("穿孔占空比");
                            if (isNotNull(tresis_duty)) mDatas.add(new FileBean(161, 159, "穿孔占空比:" + tresis_duty.optString(Constants.VALUE) + tresis_duty.optString(Constants.UNIT)));
                            JSONObject tresis_time = tresis_normal.optJSONObject("穿孔时间");
                            if (isNotNull(tresis_time)) mDatas.add(new FileBean(162, 159, "穿孔时间:" + tresis_time.optString(Constants.VALUE) + tresis_time.optString(Constants.UNIT)));
                            JSONObject tresis_gas = tresis_normal.optJSONObject("穿孔气体");
                            if (isNotNull(tresis_gas)) mDatas.add(new FileBean(163, 159, "穿孔气体:" + tresis_gas.optString(Constants.VALUE) + tresis_gas.optString(Constants.UNIT)));
                            JSONObject tresis_pressure = tresis_normal.optJSONObject("穿孔气压");
                            if (isNotNull(tresis_pressure)) mDatas.add(new FileBean(164, 159, "穿孔气压:" + tresis_pressure.optString(Constants.VALUE) + tresis_pressure.optString(Constants.UNIT)));
                            JSONObject tresis_freq = tresis_normal.optJSONObject("穿孔频率");
                            if (isNotNull(tresis_freq)) mDatas.add(new FileBean(165, 159, "穿孔频率:" + tresis_freq.optString(Constants.VALUE) + tresis_freq.optString(Constants.UNIT)));
                            JSONObject tresis_height = tresis_normal.optJSONObject("穿孔高度");
                            if (isNotNull(tresis_height)) mDatas.add(new FileBean(166, 159, "穿孔高度:" + tresis_height.optString(Constants.VALUE) + tresis_height.optString(Constants.UNIT)));
                        }
                        JSONObject tresis_prev = tresis.optJSONObject("预爆孔");
                        if (isNotNull(tresis_prev)) {
                            mDatas.add(new FileBean(167, 134, "预爆孔"));
                            JSONObject tresis_power = tresis_prev.optJSONObject("穿孔功率");
                            if (isNotNull(tresis_power)) mDatas.add(new FileBean(168, 167, "穿孔功率:" + tresis_power.optString(Constants.VALUE) + tresis_power.optString(Constants.UNIT)));
                            JSONObject tresis_duty = tresis_prev.optJSONObject("穿孔占空比");
                            if (isNotNull(tresis_duty)) mDatas.add(new FileBean(169, 167, "穿孔占空比:" + tresis_duty.optString(Constants.VALUE) + tresis_duty.optString(Constants.UNIT)));
                            JSONObject tresis_time = tresis_prev.optJSONObject("穿孔时间");
                            if (isNotNull(tresis_time)) mDatas.add(new FileBean(170, 167, "穿孔时间:" + tresis_time.optString(Constants.VALUE) + tresis_time.optString(Constants.UNIT)));
                            JSONObject tresis_gas = tresis_prev.optJSONObject("穿孔气体");
                            if (isNotNull(tresis_gas)) mDatas.add(new FileBean(171, 167, "穿孔气体:" + tresis_gas.optString(Constants.VALUE) + tresis_gas.optString(Constants.UNIT)));
                            JSONObject tresis_pressure = tresis_prev.optJSONObject("穿孔气压");
                            if (isNotNull(tresis_pressure)) mDatas.add(new FileBean(172, 167, "穿孔气压:" + tresis_pressure.optString(Constants.VALUE) + tresis_pressure.optString(Constants.UNIT)));
                            JSONObject tresis_freq = tresis_prev.optJSONObject("穿孔频率");
                            if (isNotNull(tresis_freq)) mDatas.add(new FileBean(173, 167, "穿孔频率:" + tresis_freq.optString(Constants.VALUE) + tresis_freq.optString(Constants.UNIT)));
                            JSONObject tresis_height = tresis_prev.optJSONObject("穿孔高度");
                            if (isNotNull(tresis_height)) mDatas.add(new FileBean(174, 167, "穿孔高度:" + tresis_height.optString(Constants.VALUE) + tresis_height.optString(Constants.UNIT)));
                        }
                    }
                }
            }
            initDataTree();
        } catch (JSONException e) {
            e.printStackTrace();
            mTree.setVisibility(View.GONE);
            mRefreshBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void exc(final String exception) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(exception)) {
                    CommUtils.showToast(exception);
                }
                getActivity().finish();
            }
        });
    }
}
