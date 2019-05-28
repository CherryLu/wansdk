package com.wan.callring.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.wan.callring.R;
import com.wan.callring.bean.DetailsBean;
import com.wan.callring.ui.BaseFragment;
import com.wan.callring.ui.adapter.GridListAdapter;
import com.wan.callring.ui.widget.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class MyRingFragment extends BaseFragment {

    private Banner view_banner;
    private CheckBox  play_radom,play_one;
    private RecyclerView  recycleView;
    private StaggeredGridLayoutManager layoutManager;
    private GridListAdapter listAdapter;
    private XRefreshView  xrefreshview;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_myring,container,false);
        initView();
        initRefresh();
        initList();
        return mRootView;
    }

    private void initView(){
        view_banner = mRootView.findViewById(R.id.view_banner);
        play_radom = mRootView.findViewById(R.id.play_radom);
        play_one = mRootView.findViewById(R.id.play_one);
        xrefreshview = mRootView.findViewById(R.id.xrefreshview);
    }

    private void initRefresh(){

        xrefreshview.setPullLoadEnable(true);
        xrefreshview.setPinnedTime(1000);
        xrefreshview.setMoveForHorizontal(true);
        xrefreshview.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener(){

            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                super.onLoadMore(isSilence);
            }
        });
    }

    private void initList(){
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        listAdapter = new GridListAdapter(getList(),getContext());
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(listAdapter);

        listAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getContext()));

    }


    private List<DetailsBean> getList(){

        List<DetailsBean> beans = new ArrayList<>();

        for (int i = 0;i<5;i++){

            DetailsBean bean = new DetailsBean();
            beans.add(bean);
        }

       return beans;
    }
}
