package com.wan.callring.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.wan.callring.R;
import com.wan.callring.bean.DetailsBean;
import com.wan.callring.ui.widget.VidewView2;
import com.wan.callring.utils.Preferences;

import java.util.Timer;
import java.util.TimerTask;




/**
 * 预览页
 */
public class PreviewActivity extends BaseActivity implements View.OnClickListener{
    public static String URL= "url";
    public static String _DATA= "data";
    public static String comeRecod = "comeRecod";
    VidewView2 videoView;
    ImageView goback;
    ProgressBar video_progressbar;
    DetailsBean detailsBean;
   // Action action;//直接传action过来
   // CallShowListData callShowListData;
    boolean isUsed;
    int currPosintion;
    ImageButton iv_phone_center;
    Button nextstep;
    boolean comeTo;//是否需要显示next
    String fileUrl;
    String screenPhoto;
    RelativeLayout rl_call_layout;
    LinearLayout ll_call_info;//拨打信息
    LinearLayout ll_opeartion;//操作栏
    LinearLayout ll_video_detail;//视频信息
    TextView setting_counts;//设置数
    TextView tv_preivew;
    TextView tv_share;
    TextView tv_setting;
    TextView tv_video_title;
    TextView tv_owner;
    ImageView bg_guid_view;
    int guidState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.callshow_activity_pre_view);
        fileUrl = getIntent().getExtras().getString(URL) ;
        screenPhoto =  getIntent().getExtras().getString("video_screenshot") ;
        comeTo = getIntent().getBooleanExtra(comeRecod,false);
        ll_call_info = (LinearLayout)findViewById(R.id.ll_call_info);
        ll_opeartion = (LinearLayout)findViewById(R.id.ll_opeartion);
        ll_video_detail = (LinearLayout)findViewById(R.id.ll_video_detail);
        rl_call_layout = (RelativeLayout)findViewById(R.id.rl_call_layout);
        setting_counts = (TextView)findViewById(R.id.setting_counts);
        tv_preivew = (TextView)findViewById(R.id.tv_preivew);
        tv_share = (TextView)findViewById(R.id.tv_share);
        tv_setting = (TextView)findViewById(R.id.tv_setting);
        tv_video_title = (TextView)findViewById(R.id.tv_video_title);
        tv_owner = (TextView)findViewById(R.id.tv_owner);
        bg_guid_view = (ImageView)findViewById(R.id.bg_guid_view);
        rl_call_layout.setVisibility(View.GONE);
        ll_call_info.setVisibility(View.GONE);

        tv_preivew.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_setting.setOnClickListener(this);

        videoView = (VidewView2)findViewById(R.id.videoview);

        goback = (ImageView)findViewById(R.id.goback);
        nextstep = (Button)findViewById(R.id.nextstep);
        nextstep.setOnClickListener(this);
        iv_phone_center = (ImageButton)findViewById(R.id.iv_phone_center);
        if(comeTo){
            nextstep.setVisibility(View.VISIBLE);
            ll_opeartion.setVisibility(View.GONE);
            rl_call_layout.setVisibility(View.VISIBLE);
            ll_video_detail.setVisibility(View.GONE);
            ll_call_info.setVisibility(View.VISIBLE);
        }else {
            videoView.setOnClickListener(this);
            iv_phone_center.setOnClickListener(this);
            boolean isFirst = Preferences.getBoolean(Preferences.GUID_PRE_FIRST,true);
            if(isFirst){
                Preferences.saveBoolean(Preferences.GUID_PRE_FIRST,false);
                bg_guid_view.setVisibility(View.VISIBLE);
                bg_guid_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(guidState == 0){
                            bg_guid_view.setImageResource(R.mipmap.bg_guid_share);
                            guidState = 1;
                        }else if(guidState == 1){
                            bg_guid_view.setImageResource(R.mipmap.bg_guid_setting);
                            guidState = 2;
                        }else {
                            bg_guid_view.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
        video_progressbar = (ProgressBar)findViewById(R.id.video_progressbar);
        video_progressbar.setVisibility(View.GONE);



        if(!TextUtils.isEmpty(fileUrl)){
            if(fileUrl.equals("-1")){
                fileUrl = "android.resource://" + getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
                Uri uri = Uri.parse(fileUrl);
//                videoView.setVideoURI(uri);
//                videoView.start();
                videoView.start(fileUrl);
            }else {
                Uri uri = Uri.parse(fileUrl);
//                videoView.setVideoURI(uri);
//                videoView.start();
                videoView.start(fileUrl);
            }
        }
//        videoView.setData(fileUrl);

        goback.setOnClickListener(this);
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                video_progressbar.setVisibility(View.GONE);
////                mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//                //TODO
////                mediaPlayer.setVolume(0,0);
//            }
//        });
//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                videoView.stopPlayback();
//                return true;
//            }
//        });
//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                return true;
//            }
//        });

        Bundle bundle = getIntent().getExtras();
       /* if(bundle.getSerializable(_DATA) instanceof Action){
            action = (Action)bundle.getSerializable(_DATA) ;
        }else if(bundle.getSerializable(_DATA) instanceof DetailsBean){
            detailsBean = (DetailsBean)bundle.getSerializable(_DATA) ;
        }else if(bundle.getSerializable(_DATA) instanceof CallShowListData){
            callShowListData = (CallShowListData)bundle.getSerializable(_DATA) ;
        }else if(bundle.getSerializable(_DATA) instanceof HomeBannerBean.BannerBean){
//            bannerBean = (HomeBannerBean.BannerBean)bundle.getSerializable(_DATA) ;
        }

        if(action!=null){
            getDetailInfo(action.callshowBean.id);
        }else if(detailsBean!=null){
            getDetailInfo(detailsBean.iringid);
        }else if(callShowListData!=null){
            getDetailInfo(callShowListData.id);
        }*/

    }

    public static final int UPDATE_DURATION = 1071;// 更新进度
    private void startDurationTimer() {
        handler.sendEmptyMessageDelayed(UPDATE_DURATION,1000);
    }

    /**
     * 关闭进度检测定时器
     */
    private void stopDurationTimer() {
        handler.removeMessages(UPDATE_DURATION);
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
//        videoView.resume();
                    mp.start();
            mp.setLooping(true);
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        stopDurationTimer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        videoView.stopPlayback();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoView.destory();
        stopDurationTimer();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.goback) {
            finish();

        } else if (i == R.id.tv_setting) {
            String myMobile = Preferences.getString(Preferences.UserMobile, null);
            if (TextUtils.isEmpty(myMobile)) {//登录

                return;
            }
            //TODO 设置管理
            /*if (UserManager.getInstance().isCallshow) {
                if (isUsed) {
                    showToast("已设置");
                } else {
                    setVideo();
                }
            } else {
                showConfirmDialog();
            }*/


        } else if (i == R.id.tv_share) {
        /*    //                showToast("暂未开通");
            String myMobile2 = Preferences.getString(Preferences.UserMobile, null);
            if (TextUtils.isEmpty(myMobile2)) {
                startActivity(new Intent(PreviewActivity.this, LoginActivity.class));
                return;
            }
            if (detailsBean != null) {
                String token = CommUtils.getMobileAdEncrypt(Preferences.getString(Preferences.UserMobile, ""));
                String url = "http://html5.china-plus.net/mix/zhclshare?id=" + detailsBean.iringid + "&token=" + token;
                showShare(detailsBean.sname, url, detailsBean.sthumbnail);
            } else {
               // showToast("没有获取到分享信息!");
            }
//                showShare("测试2","http://223.87.179.157/image/a5/extension_name/_2017_10_26_11_26_52.mp4","http://image2.china-plus.net/image/base/callShow/smallPic/1508987887579_20171026111820.PNG?12");
*/
        } else if (i == R.id.nextstep) {
            /*Intent intent = new Intent(PreviewActivity.this, SendVideoPostActivity.class);
            intent.putExtra(MediaRecorderActivity.VIDEO_URI, fileUrl);
            intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, screenPhoto);
            startActivity(intent);
            finish();*/

        } else if (i == R.id.tv_preivew) {
            ll_opeartion.setVisibility(View.GONE);
            rl_call_layout.setVisibility(View.VISIBLE);
            ll_video_detail.setVisibility(View.GONE);
            ll_call_info.setVisibility(View.VISIBLE);

        } else if (i == R.id.iv_phone_center || i == R.id.videoview) {
            ll_opeartion.setVisibility(View.VISIBLE);
            rl_call_layout.setVisibility(View.GONE);
            ll_video_detail.setVisibility(View.VISIBLE);
            ll_call_info.setVisibility(View.GONE);

        } else {
        }
    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
               /* case CallShowSetVideoProtocol.MSG_WHAT_OK:
                    if(!TextUtils.isEmpty(callShowSetVideoProtocol.msg)&&callShowSetVideoProtocol.msg.equals("Success")){
                        Toast.makeText(PreviewActivity.this,"应用成功",Toast.LENGTH_SHORT).show();
                        try {
                            detailsBean.heat = callShowSetVideoProtocol.heat;
                            setting_counts.setText(callShowSetVideoProtocol.heat+"");
                            UserManager.getInstance().setDetailsBean(detailsBean);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        isUsed = true;
                    }else{
                        Toast.makeText(PreviewActivity.this,"应用失败",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CallShowSetVideoProtocol.MSG_WHAT_FAIL:
                    break;
                case CallShowGetVideoInfoProtocol.MSG_WHAT_OK:
                    detailsBean =  callShowGetVideoInfoProtocol.detailsBean;
                    if(TextUtils.isEmpty(detailsBean.surl)){
                        showToast("播放信息不存在!");
                        return;
                    }
                        Uri uri = Uri.parse(detailsBean.surl);
//                        videoView.setVideoURI(uri);
                        videoView.start(detailsBean.surl);
                    if(!TextUtils.isEmpty(detailsBean.smobile)&&StringUtil.isMobileNum(detailsBean.smobile)){
                        if(detailsBean.smobile.length() == 11){
                            String maskNumber = detailsBean.smobile.substring(0,3)+"****"+detailsBean.smobile.substring(7,detailsBean.smobile.length());
                            tv_owner.setText("本视频是由 "+maskNumber +" 上传");
                        }
                    }else {
                        if(TextUtils.isEmpty(detailsBean.smobile)){
                            tv_owner.setText("");
                        }else {
                            tv_owner.setText("本视频是由 "+detailsBean.smobile +" 上传");
                        }

                    }

                    tv_video_title.setText(detailsBean.sname);
                    setting_counts.setText(detailsBean.heat+"");
                    if(UserManager.getInstance().getDetailsBean()!=null){
                        if(UserManager.getInstance().getDetailsBean().iringid.equals(detailsBean.iringid)){
                            isUsed = true;
                        }
                    }
//                    if(!TextUtils.isEmpty(detailsBean.sthumbnail)){
//                        Glide.with(PreviewActivity.this.getApplicationContext())
//                                .load(detailsBean.sthumbnail)
//                                .into(imageView);
//                    }
                    break;*/
                case UPDATE_DURATION:
                   int duration = videoView.getCurrDuration();//getCurrentPosition();
                    Log.e("duration",duration+":"+currPosintion);
                    if(duration==currPosintion){
                        video_progressbar.setVisibility(View.GONE);//VISIBLE
                    }else {
                        video_progressbar.setVisibility(View.GONE);
                    }
                    currPosintion = duration;
                    handler.sendEmptyMessageDelayed(UPDATE_DURATION,1000);
                    break;
            }
            super.handleMessage(msg);
        }

    };
    /*CallShowSetVideoProtocol callShowSetVideoProtocol;
    UpCallShowSetVideoData upCallShowSetVideoData;*/
    private void setVideo(){
       /* if(detailsBean==null){
            showToast("没有获取到信息");
            return;
        }
        UserManager.getInstance().setDetailsBean(detailsBean);
        upCallShowSetVideoData = new UpCallShowSetVideoData();
        upCallShowSetVideoData.smobile = Preferences.getString(Preferences.UserMobile,null);
        upCallShowSetVideoData.iringid = detailsBean.iringid;
        if (callShowSetVideoProtocol == null) {
            callShowSetVideoProtocol = new CallShowSetVideoProtocol(null, upCallShowSetVideoData, handler);
            callShowSetVideoProtocol.showWaitDialog();
        }
        callShowSetVideoProtocol.stratDownloadThread(null, ServiceUri.Spcl, upCallShowSetVideoData, handler,true);*/
    }


   // CallShowGetVideoInfoProtocol callShowGetVideoInfoProtocol;
    public void getDetailInfo(String id){
      /*  upCallShowSetVideoData = new UpCallShowSetVideoData();
        upCallShowSetVideoData.iringid = id;
        if (callShowGetVideoInfoProtocol == null) {
            callShowGetVideoInfoProtocol = new CallShowGetVideoInfoProtocol(null, upCallShowSetVideoData, handler);
            callShowGetVideoInfoProtocol.showWaitDialog();
        }
        callShowGetVideoInfoProtocol.stratDownloadThread(null, ServiceUri.Spcl, upCallShowSetVideoData, handler,true);*/
    }
    @Override
    protected void onResume() {
        super.onResume();
//        videoView.seekTo(currPosintion);
//        videoView.start();
        videoView.resume();//resume
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(videoView.getVisibility()!=View.VISIBLE)
                        videoView.setVisibility(View.VISIBLE);
                        startDurationTimer();
                    }
                });
            }
        },50);
    }

   /* public void showConfirmDialog() {
        if(this.isFinishing()){
            return;
        }
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        builder.setTitle("需要订购中华彩铃会员才能设置彩铃");

        builder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(PreviewActivity.this, OpenVipActivity.class));
                    }
                });

        builder.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    private void showShare(final String title,final String url,final String imageUrl) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams shareParams) {
//                if(platform.getName().equals(SinaWeibo.NAME)){
//                    shareParams.setTitle(getString(R.string.app_name));
//                    shareParams.setText(title+url);
//                    shareParams.setImageUrl(imageUrl);
//                }else
                if(platform.getName().equals(QQ.NAME)){
                    shareParams.setTitle(getString(R.string.app_name));
                    shareParams.setText(title);
                    shareParams.setTitleUrl(url);
                    shareParams.setImageUrl(imageUrl);
                }else if(platform.getName().equals(QZone.NAME)){
                    shareParams.setTitle(getString(R.string.app_name));
                    shareParams.setText(title);
                    shareParams.setTitleUrl(url);
                    shareParams.setImageUrl(imageUrl);
                }else if(platform.getName().equals(Wechat.NAME)){
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    shareParams.setTitle(getString(R.string.app_name));
                    shareParams.setText(title);
                    shareParams.setUrl(url);
                    shareParams.setImageUrl(imageUrl);
                }else if(platform.getName().equals(WechatMoments.NAME)){
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    shareParams.setTitle(getString(R.string.app_name));
                    shareParams.setText(title);
                    shareParams.setUrl(url);
                    shareParams.setImageUrl(imageUrl);
                }
            }
        });

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(getString(R.string.app_name));
//        oks.setImageUrl("http://image2.china-plus.net/image/base/callShow/smallPic/1508987887579_20171026111820.PNG?12");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://223.87.179.157/image/a5/extension_name/_2017_10_26_11_26_52.mp4");
        // text是分享文本，所有平台都需要这个字段
//        oks.setText(title);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(url);

// 启动分享GUI
        oks.show(this);
    }*/
}
