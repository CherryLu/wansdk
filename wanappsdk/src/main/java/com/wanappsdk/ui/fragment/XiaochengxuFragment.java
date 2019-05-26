package com.wanappsdk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wanappsdk.R;
import com.wanappsdk.WanSdkManager;
import com.wanappsdk.baen.BaseItem;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.BaseXiaochengxu;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.ui.viewitem.XiaochengxuItem;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.TaskClickCallBack;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;


/**
 * Created by Administrator on 2018/9/9.
 */

public class XiaochengxuFragment extends BaseFragment implements TaskClickCallBack{


    RecyclerView recyclerview;
    SmartRefreshLayout refreshlayout;
    LinearLayout swiplayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    private String mid;
    private int count = 1;

    private MultiItemTypeAdapter adapter;
    private List<TaskData> mDatas;

    private void initView(View  view){
        recyclerview = view.findViewById(R.id.recyclerview);
        refreshlayout = view.findViewById(R.id.refreshlayout);
        swiplayout = view.findViewById(R.id.swiplayout);

    }
    private  AlertHelper helper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_find, null, false);
        initView(mRootView);
        mRootView.findViewById(R.id.title_pic).setVisibility(View.GONE);
        mid = getArguments().getString("MID");
        refreshlayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });

        refreshlayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMoreData();
            }
        });
         helper = new AlertHelper(getContext());

       // helper.showWaiting();
         getData();


        return mRootView;
    }


    private void initList() {
        initData();
        adapter = new MultiItemTypeAdapter(getContext(), mDatas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter.addItemViewDelegate(new XiaochengxuItem(this));
        EmptyWrapper wrapper = new EmptyWrapper(adapter);
        wrapper.setEmptyView(R.layout.empty_view);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(wrapper);


    }


    private void initData() {
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setType(BaseItem.ITEM_XIAOCHENGXU);
        }
    }


    private void addTestData(){
        TaskData data = new TaskData();
        data.setJobName("测试H5");
        data.setJobSubtitle("测试H5");

        mDatas.add(data);
    }


    private void getData() {

        ApiServiceManager.getXiaochengxu(new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    LogUtils.e("XCX",json);
                    if (refreshlayout != null) {
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (refreshlayout != null) {
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (refreshlayout != null) {
                    refreshlayout.finishRefresh(WAITE_TIME);
                }
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                LogUtils.e("XCX","onDecode : "+json);
                Gson gson = new Gson();
                BaseXiaochengxu xiaochengxu =gson.fromJson(json, BaseXiaochengxu.class);

                if (xiaochengxu==null){

                    return;
                }
                if (mDatas==null){
                    mDatas = new ArrayList<>();
                }else {
                    mDatas.clear();
                }

                mDatas.addAll(xiaochengxu.getXiaochengxuList());

                initData();
                initList();

                if (refreshlayout != null) {
                    refreshlayout.finishRefresh(WAITE_TIME);
                }
            }
        });

    }





    private void loadMoreData() {
        refreshlayout.finishLoadMore(WAITE_TIME);
        /*ApiServiceManager.getDataList(mid, count, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskList baseTaskList = gson.fromJson(json, BaseTaskList.class);
                    if (mDatas == null) {
                        mDatas = new ArrayList<>();
                    }
                    mDatas.addAll(baseTaskList.getTaskDatas());
                    initData();
                    adapter.notifyDataSetChanged();
                    count++;

                    if (refreshlayout != null) {
                        refreshlayout.finishLoadMore(WAITE_TIME);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (refreshlayout != null) {
                        refreshlayout.finishLoadMore(WAITE_TIME);
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                if (refreshlayout != null) {
                    refreshlayout.finishLoadMore(WAITE_TIME);
                }
            }
        });*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void taskClick(TaskData taskData) {
        if (WanSdkManager.getInstance(getContext()).getXiaoChengXuClick()!=null){
            WanSdkManager.getInstance(getContext()).getXiaoChengXuClick().xiaochengxuClick(taskData);
        }

       // Nagivator.startWebActivity(getContext(),taskData);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){

        }
    }
}
