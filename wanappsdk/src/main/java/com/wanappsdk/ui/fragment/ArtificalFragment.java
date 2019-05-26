package com.wanappsdk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wanappsdk.R;
import com.wanappsdk.baen.BaseItem;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.BaseTaskList;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.ui.viewitem.WebSmallIconItem;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.Nagivator;
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

public class ArtificalFragment extends BaseFragment implements TaskClickCallBack{


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
        mRootView.findViewById(R.id.title_pic).setVisibility(View.VISIBLE);
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
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),4);
        adapter.addItemViewDelegate(new WebSmallIconItem(this));
        EmptyWrapper wrapper = new EmptyWrapper(adapter);
        wrapper.setEmptyView(R.layout.empty_view);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(wrapper);


    }


    private void initData() {
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setType(BaseItem.ITEM_SMALL_PIC_WEB);
        }
    }


    private void addTestData(){
        TaskData data = new TaskData();
        data.setJobName("测试H5");
        data.setJobSubtitle("测试H5");

        mDatas.add(data);
    }


    private void getData() {

        ApiServiceManager.getArtificalDetail(new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskList taskList = gson.fromJson(json,BaseTaskList.class);
                    if (taskList!=null){
                        if (mDatas==null){
                            mDatas = new ArrayList<>();
                        }else {
                            mDatas.clear();
                        }
                        /*addTestData();
                        initList();*/
                        if (taskList.getTaskDatas()!=null&&taskList.getTaskDatas().size()>0){
                            mDatas.addAll(taskList.getTaskDatas());
                            initList();
                        }

                    }
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
                Gson gson = new Gson();
                List<TaskData> dataList = new ArrayList<>();
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(json).getAsJsonArray();

                for (JsonElement taskData:jsonArray){
                    TaskData data =  gson.fromJson(taskData,TaskData.class);
                    dataList.add(data);
                }

                if (dataList!=null){
                    if (mDatas==null){
                        mDatas = new ArrayList<>();
                    }else {
                        mDatas.clear();
                    }
                    if (dataList!=null&&dataList.size()>0){
                        mDatas.addAll(dataList);
                        initList();
                    }
                }
                if (refreshlayout != null) {
                    refreshlayout.finishRefresh(WAITE_TIME);
                }
            }
        });

    }


    private List<TaskData> getList(){
        List<TaskData> mDatas = new ArrayList<>();
        for (int i =0;i<10;i++){
            TaskData taskData = new TaskData();
            taskData.setType(BaseItem.ITEM_SMALL_PIC);
          /*  taskData.setTitle("测试任务标题");
            taskData.setSubtitle("下载可获取积分的APP");
            taskData.setDoneCount(200);
            taskData.setLimiteCount((i+1)*200);
            taskData.setJzGain(60);
            taskData.setIconUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539281487934&di=d5e3155b1d297891831e4f2b215cf728&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3D083790ed0323dd54357eaf2bb960d9ab%2F574e9258d109b3de5fa608e1c6bf6c81800a4c1a.jpg");*/
            mDatas.add(taskData);
        }

        return mDatas;
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

        Nagivator.startWebActivity(getContext(),taskData);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){

        }
    }
}
