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
import com.wanappsdk.baen.BaseAllTaskList;
import com.wanappsdk.baen.BaseItem;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.SortBean;
import com.wanappsdk.baen.SortData;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.ui.viewitem.SmallPicItem;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.TaskClickCallBack;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.ResponseBody;


/**
 * Created by Administrator on 2018/9/9.
 */

public class SortFragment extends BaseFragment implements TaskClickCallBack{


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
        mRootView = inflater.inflate(R.layout.fragment_find, null,
                false);
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

        helper.showWaiting();
        LogUtils.e("Fragment_TIME","Net_Start : "+System.currentTimeMillis());
         getData();


        return mRootView;
    }


    private void initList(List<TaskData> mDatas, SortData sortData) {
        initData(sortData);
        adapter = new MultiItemTypeAdapter(getContext(), mDatas);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        adapter.addItemViewDelegate(new SmallPicItem(this));
        EmptyWrapper wrapper = new EmptyWrapper(adapter);
        wrapper.setEmptyView(R.layout.empty_view);
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(wrapper);


    }


    private void initData(SortData sortData) {
        if (mDatas == null) {
            return;
        }
        List<TaskData> taskDatas = new ArrayList<>();

        for (int i = 0;i<mDatas.size();i++){
            TaskData taskData = mDatas.get(i);
            //屏蔽黑白名单
            for (int m = 0;m<sortData.getTii_JidAname().size();m++){
                SortBean bean = sortData.getTii_JidAname().get(m);
                    if (taskData.getApkName().equals(bean.getApkName())){
                        if (taskData.getTid().equals(bean.getJobId())){//必须显示
                            taskData.setWhite(true);
                            break;
                        }/*else {//必须不显示
                            taskData.setWhite(false);
                        }*/
                    }
            }

            //屏蔽渠道
            for (int n = 0;n<sortData.getTcbm_jid().size();n++){
                SortBean bean = sortData.getTcbm_jid().get(n);
                if (bean.getJobId().equals(taskData.getTid())){
                    taskData.setBlack(true);//屏蔽
                }

            }


            if (taskData.getJobMarket().equals("平台")||taskData.getJobMarket().equals("跳转")){

                if (taskData.isWhite()){
                    taskDatas.add(taskData);
                } else if ((!ApkUtils.isInList(taskData.getApkName()))&&(!taskData.isBlack())){
                    taskDatas.add(taskData);
                }
            }else {
                taskDatas.add(taskData);
            }
        }

        if (mDatas!=null){
            mDatas.clear();
            mDatas.addAll(taskDatas);
        }
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setType(BaseItem.ITEM_SMALL_PIC);
        }
    }


    private void getData() {

        ApiServiceManager.getDataList(0, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseAllTaskList baseTaskList = gson.fromJson(json, BaseAllTaskList.class);
                    mDatas = new ArrayList<>();
                    if (baseTaskList!=null&&baseTaskList.getData()!=null){
                        mDatas.addAll(baseTaskList.getData().gettJob_list0());
                        initList(mDatas,baseTaskList.getData());
                        helper.cancleWaiting();
                        count = 1;
                    }

                    if (refreshlayout != null) {
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if (refreshlayout != null) {
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }
                    helper.cancleWaiting();
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (refreshlayout != null) {
                    refreshlayout.finishRefresh(WAITE_TIME);
                }

                helper.cancleWaiting();
            }

            @Override
            public void onDecode(BaseString baseString, String json) {

                    helper.cancleWaiting();
                    try {
                        Gson gson = new Gson();
                        SortData baseTaskList = gson.fromJson(json, SortData.class);
                        mDatas = new ArrayList<>();
                        if (baseTaskList!=null){
                            mDatas.addAll(baseTaskList.gettJob_list0());

                            sortList(mDatas);

                            initList(mDatas,baseTaskList);
                            count = 1;
                        }
                    }catch (Exception e){

                    }

                    if (refreshlayout != null) {
                        refreshlayout.finishRefresh(WAITE_TIME);
                    }
            }
        });

    }

    private void sortList(List<TaskData> mDatas){
        Collections.shuffle(mDatas);
        Collections.sort(mDatas, new Comparator<TaskData>() {
            @Override
            public int compare(TaskData o1, TaskData o2) {
                if (o1.getSort()>=o2.getSort()){
                    return 1;
                }else {
                    return -1;
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
        Nagivator.startTaskDetailActivity(this,taskData);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            refreshlayout.autoRefresh();
        }
    }
}
