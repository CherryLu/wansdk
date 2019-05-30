package com.wan.callring.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andview.refreshview.XRefreshView;
import com.wan.callring.R;
import com.wan.callring.bean.DetailsBean;
import com.wan.callring.ui.BaseFragment;
import com.wan.callring.ui.widget.banner.Banner;
import com.wan.callring.ui.widget.banner.view.BannerViewPager;
import com.wan.callring.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class ShowFragment extends BaseFragment {

    FragmentActivity activity;
    RecyclerView recyclerView;
    //    SimpleAdapter adapter;
    StageredListAdapter stageredListAdapter;
    XRefreshView xRefreshView;
    int lastVisibleItem = 0;
    //    LinearLayoutManager linearLayoutManager;
    StaggeredGridLayoutManager layoutManager;
    private boolean isBottom = false;
    private int mLoadCount = 0;
    private BannerViewPager mBannerViewPager;
    private FrameLayout layoutContainer;
    private RelativeLayout layout_home_title;
    public List<DetailsBean> mainlist = new ArrayList<DetailsBean>();
    Banner banner;
    boolean isloadMore;

    public  List<String> images=new ArrayList<String>();
    public  List<String> titles=new ArrayList<>();

    boolean isAddSelf = false;
    LinearLayout ll_view;
    List<ImageView> list_arror = new ArrayList<>();
    RelativeLayout rl_diy;
    String icode = "1";
    boolean isRequestMyself = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.activity_recylerview, container,
                    false);
            activity = getActivity();
            initView();
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if(Preferences.getString(Preferences.UserMobile,null) == null){
            UserManager.getInstance().setDetailsBean(null);
            isRequestMyself = false;
            if(isAddSelf&&currPos ==0){
                isAddSelf = false;
//                mainlist.remove(0);
                stageredListAdapter.notifyDataSetChanged();
            }
        }else {
            if(UserManager.getInstance().getDetailsBean()!=null&&mainlist.size()>0){
                if(currPos == 0){
                    if(!isAddSelf){
                        mainlist.add(0,UserManager.getInstance().getDetailsBean());
                    }else {
//                        mainlist.remove(0);
                        mainlist.add(0,UserManager.getInstance().getDetailsBean());
                    }
                    isAddSelf = true;
                }
                for(int i = 0;i<mainlist.size(); i++){
                    if(mainlist.get(i).iringid.equals(UserManager.getInstance().getDetailsBean().iringid)){
                        mainlist.get(i).heat = UserManager.getInstance().getDetailsBean().heat;
                        if(currPos == 0 && isAddSelf && mainlist.get(i).iringid.equals(UserManager.getInstance().getDetailsBean().iringid)&&i!=0){
                            mainlist.remove(i);
                            i--;
                        }
                    }
                }
                stageredListAdapter.notifyDataSetChanged();
            }else if(UserManager.getInstance().getDetailsBean() == null &&isRequestMyself == false){
                String myphone = Preferences.getString(Preferences.UserMobile,"");
                if(!TextUtils.isEmpty(myphone)){
                    isRequestMyself = true;
                    getVideo(myphone);
                }
            }
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * 初始化数据
     */
    private void initData() {
    }

    /**
     * 初始化视图
     */
    private void initView() {
        CallShowActivity callShowActivity = (CallShowActivity)activity;
        if(callShowActivity.status){
            layout_home_title = (RelativeLayout)mRootView.findViewById(R.id.layout_home_title);
            layout_home_title.setPadding(0,callShowActivity.statusHeight,0,0);
        }
        ll_view = (LinearLayout) mRootView.findViewById(R.id.ll_view);
        rl_diy = (RelativeLayout)mRootView.findViewById(R.id.rl_diy);
        rl_diy.setOnClickListener(this);
        layoutContainer = (FrameLayout) mRootView.findViewById(R.id.layoutContainer);
        layoutContainer.setOnClickListener(this);
        xRefreshView = (XRefreshView) mRootView.findViewById(R.id.xrefreshview);
        xRefreshView.setPullLoadEnable(true);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_test_rv);

        recyclerView.setHasFixedSize(true);

//        adapter = new SimpleAdapter(list, activity);
        stageredListAdapter = new StageredListAdapter(mainlist,activity);
        // 设置静默加载模式
//		xRefreshView1.setSilenceLoadMore();
//        linearLayoutManager = new LinearLayoutManager(activity);
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(20));
//        banner = (Banner) adapter.setHeaderView(R.layout.bannerview, recyclerView);//headerView
        recyclerView.setAdapter(stageredListAdapter);
        xRefreshView.setAutoLoadMore(false);
        xRefreshView.setPinnedTime(1000);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setSilenceLoadMore();
//        recyclerviewAdapter.setHeaderView(headerView, recyclerView);
        stageredListAdapter.setCustomLoadMoreView(new XRefreshViewFooter(activity));
//        xRefreshView1.setPullRefreshEnable(false);
        //设置在下拉刷新被禁用的情况下，是否允许界面被下拉,默认是true
//        xRefreshView1.setMoveHeadWhenDisablePullRefresh(false);
//        xRefreshView1.enablePullUpWhenLoadCompleted(false);
//		xRefreshView1.setPullLoadEnable(false);
//        xRefreshView1.enableRecyclerViewPullUp(false);
        //设置静默加载时提前加载的item个数
//		xRefreshView1.setPreLoadCount(2);

        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                pageindex = 1;
                getHomeList();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                isloadMore = true;
                pageindex ++;
                getHomeList();
            }
        });
//        getHomeBannerInfo();
//        getHomePageInfo();
        String myphone = Preferences.getString(Preferences.UserMobile,"");
        if(!TextUtils.isEmpty(myphone)){
            isRequestMyself = true;
            getVideo(myphone);
        }else {
            getHomeList();
        }
        getSorts();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutContainer:
                startActivity(new Intent(activity, SearchActivity.class));
                break;
            case R.id.rl_diy:
                Intent intent2 = new Intent(activity, UpLoadListActivity.class);
                startActivity(intent2);
//                startActivity(new Intent(activity, com.example.zhaoshuang.weixinrecordeddemo.MainActivity.class));
//                RecordVieoUtils.intoRecordVieoActivity(activity,PreviewActivity.class.getName());
                break;
            default:
                break;
        }
    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case HomeBannerProtocol.MSG_WHAT_OK:
//                    final HomeBannerBean homeBannerBean = homeBannerProtocol.homeBannerBean;
//                    images.clear();
//                    titles.clear();
//                    for(int i=0;i<homeBannerBean.bannerList.size();i++){
//                        images.add(homeBannerBean.bannerList.get(i).getPic_url());
//                        titles.add("");//homeBannerBean.bannerList.get(i).getHint_text()
//                    }
//                    banner.setImages(images)
//                            .setBannerTitles(titles)
//                            .setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
//                            .setImageLoader(new GlideImageLoader())
//                            .start();
//                    banner.setOnBannerListener(new OnBannerListener() {
//                        @Override
//                        public void OnBannerClick(int position) {
//                            Intent intent = new Intent(activity, PreviewActivity.class);
//                            intent.putExtra(PreviewActivity._DATA,homeBannerBean.bannerList.get(position).getAction().get(0));
//                            activity.startActivity(intent);
//                        }
//                    });
//                    break;
//                case HomeBannerProtocol.MSG_WHAT_FAIL:
//                    break;
//                case HomePageProtocol.MSG_WHAT_OK:
//                    xRefreshView.stopRefresh();
//                    HomeResultDataBean homeResultDataBean= homePageProtocol.mData;
//                    list.clear();
//                    list.addAll(homeResultDataBean.dataList);
//                    adapter.setData(list);
//                    break;
//                case HomePageProtocol.MSG_WHAT_FAIL:
//                    break;
                case GetSortsProtocol.MSG_WHAT_OK:
                    if(getSortsProtocol.sortsList !=null && getSortsProtocol.sortsList.size()>0){
                        ll_view.removeAllViews();
                        list_arror.clear();
                        for ( int i = 0 ; i < getSortsProtocol.sortsList.size(); i++){
                            View v = LayoutInflater.from(activity).inflate(R.layout.main_category_scrollview, null);
                            ImageView imageView = v.findViewById(R.id.imageView_top);
                            final ImageView imageView_arrow = v.findViewById(R.id.imageView_arrow);
                            final SortsBean sortsBean = getSortsProtocol.sortsList.get(i);
                            if(!TextUtils.isEmpty(sortsBean.image)){
                                Glide.with(activity.getApplicationContext())
                                        .load(sortsBean.image)
                                        .into(imageView);
                            }
                            if(!TextUtils.isEmpty(sortsBean.image_icon)){
                                Glide.with(activity.getApplicationContext())
                                        .load(sortsBean.image_icon)
                                        .into(imageView_arrow);
                            }
                            if(i == 0){
                                imageView_arrow.setVisibility(View.VISIBLE);
                            }else {
                                imageView_arrow.setVisibility(View.INVISIBLE);
                            }
//                            TextView textView = v.findViewById(R.id.textView_name);
//                            textView.setText(sortsBean.sname);
                            ll_view.addView(v);
                            list_arror.add(imageView_arrow);
                            final int pos = i;
                            v.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    for ( int i = 0 ; i < getSortsProtocol.sortsList.size() ; i++){
                                        list_arror.get(i).setVisibility(View.INVISIBLE);
                                    }
                                    imageView_arrow.setVisibility(View.VISIBLE);
                                    pageindex = 1;
                                    currPos = pos;
                                    icode = sortsBean.icode;
                                    getHomeList();
                                }
                            });
                        }
                    }
                    break;
                case GetSortsProtocol.MSG_WHAT_FAIL:
                    break;
                case GetCallVideoInfoProtocol.MSG_WHAT_OK:
                    UserManager.getInstance().setDetailsBean(getCallVideoInfoProtocol.detailsBean);
                    getHomeList();
                    break;
                case GetCallVideoInfoProtocol.MSG_WHAT_FAIL:
                    getHomeList();
                    break;
                case GetMainlistProtocol.MSG_WHAT_OK:

                    if(isloadMore){
                        xRefreshView.stopLoadMore();
                        for (int i = 0; i < getMainlistProtocol.list.size(); i++) {
                            if(currPos == 0 && isAddSelf && getMainlistProtocol.list.get(i).iringid.equals(UserManager.getInstance().getDetailsBean().iringid)&&i!=0){
                                getMainlistProtocol.list.remove(i);
                                i--;
                            }else {
                                stageredListAdapter.insert(getMainlistProtocol.list.get(i),
                                        stageredListAdapter.getAdapterItemCount());
                            }
                        }
                        isloadMore = false;
//                        if(getMainlistProtocol.list.size() == 0){
//                            rl_nothing.setVisibility(View.VISIBLE);
//                        }else {
//                            rl_nothing.setVisibility(View.GONE);
//                        }
                    }else {
                        if(pageindex == 1){
                            stageredListAdapter.clear2();
                            if(UserManager.getInstance().getDetailsBean()!=null&&currPos ==0){
                                getMainlistProtocol.list.add(0,UserManager.getInstance().getDetailsBean());
                                isAddSelf = true;
                            }
                        }

                        for (int i = 0; i < getMainlistProtocol.list.size(); i++) {
                            if(currPos == 0 && isAddSelf && getMainlistProtocol.list.get(i).iringid.equals(UserManager.getInstance().getDetailsBean().iringid)&&i!=0){
                                getMainlistProtocol.list.remove(i);
                                i--;
                            }else {
                                stageredListAdapter.insert(getMainlistProtocol.list.get(i),
                                        stageredListAdapter.getAdapterItemCount());
                            }
                        }
                        xRefreshView.stopRefresh();
//                        if(list.size() == 0){
//                            rl_nothing.setVisibility(View.VISIBLE);
//                        }else {
//                            rl_nothing.setVisibility(View.GONE);
//                        }
                    }
                    break;
                case GetMainlistProtocol.MSG_WHAT_FAIL:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    HomeBannerProtocol homeBannerProtocol;
    HomePageProtocol homePageProtocol;
    UpHomePageData upHomePageData;
    public void getHomeBannerInfo(){
        if (homeBannerProtocol == null) {
            upHomePageData = new UpHomePageData();
            upHomePageData.record_type= "home";
            upHomePageData.record_id = "";
            upHomePageData.datatype = "2";
            homeBannerProtocol = new HomeBannerProtocol(null, upHomePageData, handler);
            homeBannerProtocol.showWaitDialog();
        } else {
            upHomePageData.record_type= "home";
            upHomePageData.record_id = "";
            upHomePageData.datatype = "2";

        }
        homeBannerProtocol.stratDownloadThread(null, ServiceUri.homepage, upHomePageData, handler,true);
    }
    public void getHomePageInfo(){
        if (homePageProtocol == null) {
            upHomePageData = new UpHomePageData();
            upHomePageData.record_type= "home";
            upHomePageData.record_id = "";
            upHomePageData.datatype = "1";
            homePageProtocol = new HomePageProtocol(null, upHomePageData, handler);
            homePageProtocol.showWaitDialog();
        } else {
            upHomePageData.record_type= "home";
            upHomePageData.record_id = "";
            upHomePageData.datatype = "1";

        }
        homePageProtocol.stratDownloadThread(null, ServiceUri.homepage, upHomePageData, handler,true);
    }

    GetMainlistProtocol getMainlistProtocol;
    UpDianZanListData upDianZanListData;
    int pageindex = 1;
    int pagesize = 10;
    int currPos = 0;//当前选择的是不是第一个
    public void getHomeList(){
        Log.e("getHomeList",icode);
        if (homePageProtocol == null) {
            upDianZanListData = new UpDianZanListData();
            upDianZanListData.pageindex = pageindex+"";
            upDianZanListData.pagesize = pagesize+"";
            upDianZanListData.icode = icode;
            getMainlistProtocol = new GetMainlistProtocol(null, upDianZanListData, handler);
            getMainlistProtocol.showWaitDialog();
        } else {
            upDianZanListData.pageindex = pageindex+"";
            upDianZanListData.pagesize = pagesize+"";
            upDianZanListData.icode = icode;

        }
        getMainlistProtocol.stratDownloadThread(null, ServiceUri.Spcl, upDianZanListData, handler,true);
    }

    GetCallVideoInfoProtocol getCallVideoInfoProtocol;

    private void getVideo(String phone){
        Log.e(TAG, "getVideo"+phone);
        UpGetCallVideoData upGetCallVideoData = new UpGetCallVideoData();
        upGetCallVideoData.sothermobile = phone;
        upGetCallVideoData.smobile ="";
        if (getCallVideoInfoProtocol == null) {
            getCallVideoInfoProtocol = new GetCallVideoInfoProtocol(null, upGetCallVideoData, handler);
            getCallVideoInfoProtocol.showWaitDialog();
        }
        getCallVideoInfoProtocol.stratDownloadThread(null, ServiceUri.Spcl, upGetCallVideoData, handler,true);
    }


    GetSortsProtocol getSortsProtocol;
    private void getSorts(){
        if (getSortsProtocol == null) {
            getSortsProtocol = new GetSortsProtocol(null, null, handler);
            getSortsProtocol.showWaitDialog();
        }
        getSortsProtocol.stratDownloadThread(null, ServiceUri.Spcl, null, handler,true);
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
//            if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpace;
//            }

        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }
}
