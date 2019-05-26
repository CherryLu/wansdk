package com.wanappsdk.ui.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wanappsdk.R;
import com.wanappsdk.animotion.ShowGold;
import com.wanappsdk.baen.ArtificalRecommand;
import com.wanappsdk.baen.BaseBean;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.keeper.ArtificalManager;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.ArtificalUtils;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.Nagivator;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;

public class WebSlideActivity extends BaseActivity {

    private WebView ActionwebView;
    private LinearLayout buttom_layout;
    private TextView tips,title_tips;
    private TaskData taskData;
    private int stayTime ;
    private boolean  hasCommit;
    private ArtificalRecommand recommand;
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case TIME:
                    stayTime++;
                    int ma = (int) (Math.sqrt(taskData.getMinstaySec())+1);

                    if (stayTime<=taskData.getMinstaySec()){
                        title_tips.setText("认真阅读至底部，即可完成阅读");
                        //title_tips.setText("阅读至底部并继续观看"+(taskData.getMinstaySec()-stayTime)+"s，即可完成阅读");
                    }else {
                        title_tips.setText("本篇阅读已经完成");
                    }

                    //checkAll();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    AlertHelper alertHelper;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        taskData = (TaskData) getIntent().getSerializableExtra("TASK");
        if (taskData==null){
            finish();
        }
        setTitleBar(taskData.getReadName());
        initView();
        initWebView();

        recommand = ArtificalManager.getInstance().artificalData.getHashMap().get(taskData.getTid());
        if (recommand==null){
            recommand = new ArtificalRecommand();
        }
        alertHelper  = new AlertHelper(this);

        int count = recommand.getHasCommit();
        if (count>=taskData.getTimesaday()){
            hasCommit = true;
            title_tips.setText("您已经完成该任务");

           /* Toast.makeText(this,"您已完成该任务",Toast.LENGTH_SHORT).show();
            Nagivator.finishActivity(this);*/
            return;
        }

        int thisCount = recommand.getCount();
        showTips(thisCount);
        title_tips.setText("您的浏览进度是"+0+"%,");

    }


    private void initView(){
        buttom_layout = findViewById(R.id.buttom_layout);
        tips = findViewById(R.id.tips);
        title_tips = findViewById(R.id.title_tips);

    }




    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        ActionwebView =  findViewById(R.id.webView);

        ActionwebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //对离线应用的支持
        ActionwebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);//设置缓冲大小，10M
        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        ActionwebView.getSettings().setAppCachePath(appCacheDir);
        ActionwebView.getSettings().setAllowFileAccess(true);
        ActionwebView.getSettings().setAppCacheEnabled(true);
        ActionwebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        ActionwebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        ActionwebView.getSettings().setJavaScriptEnabled(true);
        ActionwebView.setHorizontalScrollBarEnabled(true);
        ActionwebView.setVerticalScrollBarEnabled(true);
        ActionwebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        ActionwebView.setBackgroundColor(0);
        ActionwebView.setDownloadListener(new MyWebViewDownLoadListener());
        //ActionwebView.setOnTouchListener(this);
        ActionwebView.setWebViewClient(new MyWebViewClient());
        ActionwebView.setWebChromeClient(new MyWebChromeClient());

        // 设置可以访问文件
        ActionwebView.getSettings().setAllowFileAccess(true);
        ActionwebView.getSettings().setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        ActionwebView.getSettings().setDatabasePath(dir);
        // 使用localStorage则必须打开
        ActionwebView.getSettings().setDomStorageEnabled(true);
        ActionwebView.getSettings().setGeolocationEnabled(true);
        //必须加这个视频点击的时候 会转圈下后面就加载失败
        ActionwebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        ActionwebView.getSettings().setUseWideViewPort(true); // 关键点
        ActionwebView.getSettings().setSupportZoom(true); // 支持缩放
        ActionwebView.getSettings().setLoadWithOverviewMode(true);
//        ActionwebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
        ActionwebView.getSettings().setDefaultTextEncodingName("utf-8") ;

//        5.0 以上的手机要加这个
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActionwebView.getSettings().setMixedContentMode(WebSettings.LOAD_NORMAL);
        }
        ActionwebView.loadUrl(taskData.getReadUrl());

       // ActionwebView.loadUrl("http://ttkj.allibook.com/app/reader/506605/1?bookStore=3");


        ActionwebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                float webViewContentHeight = ActionwebView.getContentHeight()*ActionwebView.getScale();
                float webViewCurrentHeight=(ActionwebView.getHeight() + ActionwebView.getScrollY());
                LogUtils.e("ZXZXZXZX","ContentHeight :"+webViewContentHeight);
                LogUtils.e("ZXZXZXZX","CurrentHeight :"+webViewCurrentHeight);
                if (webViewContentHeight-webViewCurrentHeight<=20){
                    /*if (!hasAdd){
                        checkAll();
                        hasAdd = true;
                    }*/

                }
            }
        });

        ActionwebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (event.getY()>0){
                            Date date = new Date();
                            int ma = (int) (Math.sqrt(taskData.getMinstaySec())+1);
                            if (date.getTime()-lastTime>=(taskData.getMinstaySec()/ma)*1000){
                                count++;
                                checkAll();
                                lastTime = date.getTime();
                            }

                        }
                        break;

                }
                return false;
            }
        });

    }
    private long  lastTime;
    int  count;
    boolean hasAdd;
    boolean isUrlUse = true;



    private void  checkAll(){



        //先判断是否网址
        if (!isUrlUse||hasCommit){
            return;
        }


        int ma = (int) (Math.sqrt(taskData.getMinstaySec())+1);

        int press = (count*100/ma);
        title_tips.setText("您的浏览进度是"+press+"%,");
        if (press>=100){
            int counts =  ArtificalManager.getInstance().getcurrentCount(taskData.getTid());
            counts++;
            recommand.setCount(counts);
            ArtificalManager.getInstance().addTask(taskData.getTid(),recommand);
            showTips(counts);
            count = 0;
            title_tips.setText("您的浏览进度是"+(count*100/ma)+"%,");
            if (counts>=taskData.getReadInterval()&&!hasCommit){
                alertHelper.showWaiting();
                ApiServiceManager.getReadTaskComplete(taskData.getTid(), new HttpResponse() {
                    @Override
                    public void onNext(ResponseBody body) {
                        alertHelper.cancleWaiting();
                        if (body!=null){
                            try {
                                String json = new String(body.bytes());
                                Gson gson = new Gson();
                                BaseBean bean = gson.fromJson(json,BaseBean.class);
                                if (bean!=null){
                                    if (bean.getCode()==110){

                                        ShowGold showGold = new ShowGold(WebSlideActivity.this);
                                        showGold.show((LinearLayout) findViewById(R.id.container), WebSlideActivity.this, new ShowGold.AnimotionFinish() {
                                            @Override
                                            public void onFinish() {
                                                int count = ArtificalManager.getInstance().gethasCompleteCount(taskData.getTid());
                                                count++;
                                                recommand.setHasCommit(count);
                                                recommand.setCount(0);
                                                ArtificalManager.getInstance().addTask(taskData.getTid(),recommand);
                                                ArtificalUtils.storeData(WebSlideActivity.this,count);
                                                if (count>=taskData.getTimesaday()){
                                                    hasCommit = true;
                                                    if (tips!=null){
                                                        tips.setText("您已经完成今天的任务");
                                                    }
                                                    //showTips(count);
                                                }else {
                                                    title_tips.setText("您的浏览进度是"+0+"%,");
                                                }
                                            }
                                        });


                                        Toast.makeText(WebSlideActivity.this,"您已完成阅读任务",Toast.LENGTH_SHORT).show();

                                    }else {
                                        Toast.makeText(WebSlideActivity.this,"任务提交失败",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(WebSlideActivity.this,"任务提交失败",Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(WebSlideActivity.this,"任务提交失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        alertHelper.cancleWaiting();
                    }

                    @Override
                    public void onDecode(BaseString baseString, String json) {
                        alertHelper.cancleWaiting();

                        Gson gson = new Gson();
                        BaseBean bean = gson.fromJson(json,BaseBean.class);
                        if (bean!=null){
                            if (bean.getCode()==110){
                                int count = ArtificalManager.getInstance().gethasCompleteCount(taskData.getTid());
                                count++;
                                recommand.setHasCommit(count);
                                recommand.setCount(0);
                                ArtificalManager.getInstance().addTask(taskData.getTid(),recommand);
                                ArtificalUtils.storeData(WebSlideActivity.this,count);
                                if (count>=taskData.getTimesaday()){
                                    hasCommit = true;
                                    if (tips!=null){
                                        tips.setText("您已经完成今天的任务");
                                    }
                                    //showTips(count);
                                }else {
                                    title_tips.setText("您的浏览进度是"+0+"%,");
                                }
                                Toast.makeText(WebSlideActivity.this,"您已完成阅读任务",Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(WebSlideActivity.this,"任务提交失败",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(WebSlideActivity.this,"任务提交失败",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


    @Override
    public void onBackPressed() {
        Nagivator.finishActivity(this);
    }

    private void showTips(int count){
        if (tips!=null&&!hasCommit){
            String str = "看"+taskData.getReadInterval()+"篇可得"+ ApkUtils.getMuchMoney(taskData.getPoints())+",已经看了"+count+"篇，加油";
            tips.setText(str);
        }else {
            if (tips!=null){
                tips.setText("您今天已经完成任务");
            }
        }
    }

    private TimerTask timerTask;
    private Timer durationTimer;
    private final int TIME = 200;

    /**
     * 启动计时
     */
    private void startTimer(){
        if (timerTask==null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (handler!=null){
                        handler.sendEmptyMessage(TIME);
                    }
                }
            };
            durationTimer = new Timer();
            durationTimer.schedule(timerTask,0,1000);
        }
    }

    /**
     * 停止计时
     */
    private void stopTimer(){
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        if (durationTimer!=null){
            durationTimer.cancel();
            durationTimer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopTimer();
    }




    @Override
    protected void onStart() {
        super.onStart();
        if (timerTask!=null){

        }
    }

    @Override
    protected void backImageOnClick() {
        super.backImageOnClick();
        Nagivator.finishActivity(this);

    }



    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }
    private String currentUrl;
    private class MyWebViewClient extends WebViewClient {


        public boolean shouldOverrideUrlLoading(final WebView view, String url) {

                    isUrlUse = true;
                    title_tips.setVisibility(View.VISIBLE);
                    //tips.setVisibility(View.GONE);
                    hasAdd = false;
                    stayTime = 0;

                view.loadUrl(url);
            return true;
        }









        //        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
//            //handler.cancel(); 默认的处理方式，WebView变成空白页
//            handler.proceed();//接受证书
//
//            //handleMessage(Message msg); 其他处理
//        }
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {

        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //startTimer();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

        }

    }


    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            // TODO Auto-generated method stub
            super.onReceivedTitle(view, title);

        }

        @Override
        public void onExceededDatabaseQuota(String url,
                                            String databaseIdentifier, long currentQuota,
                                            long estimatedSize, long totalUsedQuota,
                                            WebStorage.QuotaUpdater quotaUpdater) {
        }








    }
}
