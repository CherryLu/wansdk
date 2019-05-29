package com.wan.callring.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.wan.callring.R;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.example.widget.media.IjkVideoViewCallShow;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 推荐页中用到的小视频播放器
 *
 * @author kangshaozhe
 */
public class VidewView2 extends RelativeLayout implements OnPreparedListener, OnErrorListener, OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, View.OnClickListener {
    private Context mContext;
    private Activity mActivity;
    /**
     * 视图变量
     **/
    private View mainView;
    private IjkVideoViewCallShow videoView;
    private ProgressBar progressBar;
    private int skipDuration;
    private LinearLayout videoPauseBtn,screen_status_btn;
    private ImageView videoPauseImg,screen_status_img;
    private RelativeLayout viewBox;
    /**
     * 传进来参数
     **/
    private String playUrl;
    private int mPositionWhenPaused = -1;
    private boolean showBtn;

    public void setShowBtn(boolean showBtn) {
        this.showBtn = showBtn;
    }

    /**
     * handler相关变量
     **/
    public static final int TEST_REPLAY = 1075;// 在出现错误的时候尝试重新播放

    int lastDuration;
    int curDuration;

    private Handler mHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case TEST_REPLAY:
                    int erroDur = msg.arg1;
                    videoView.seekTo(erroDur);
                    videoView.start();
                    if (skipDuration > 0) {
                        videoView.seekTo(skipDuration);
                    }
                    skipDuration = 0;
                    break;
                case UPDATE_DURATION:
                    if(videoView==null){
                        break;
                    }
                    curDuration = videoView.getCurrentPosition();
                    if(lastDuration == curDuration && videoView.isPlaying()){
                        progressBar.setVisibility(View.GONE);//View.VISIBLE
                    }else{
                        progressBar.setVisibility(View.GONE);
                    }
                    lastDuration = curDuration;
                    break;
                case 0:
                    if (videoPauseBtn!=null){
                        videoPauseBtn.setVisibility(GONE);
                    }
                    break;
                case 1:
                    pause();
                    break;
            }
        }

    };
    /**
     * 启动进度检测定时器
     */
    public static final int UPDATE_DURATION = 1078;// 更新进度
    TimerTask durationTimeTask;
    private Timer durationTimer;
    private void startDurationTimer() {
        stopDurationTimer();
        if (durationTimer == null) {
            durationTimeTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(UPDATE_DURATION);
                }
            };
            durationTimer = new Timer();
            durationTimer.schedule(durationTimeTask, 0, 1000);
        }
    }
    /**
     * 关闭进度检测定时器
     */
    private void stopDurationTimer() {
        if (durationTimer != null) {
            durationTimer.cancel();
            durationTimer = null;
        }
        if (durationTimeTask != null) {
            durationTimeTask.cancel();
            durationTimeTask = null;
        }
    }
    public int  getCurrDuration(){
        return videoView.getDuration();
    }


    public VidewView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public VidewView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public VidewView2(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private ImageView iv_dlna;

    /**
     * 初始化视图
     */
    @SuppressLint("NewApi")
    private void initView() {
        mainView = LayoutInflater.from(mContext).inflate(R.layout.video_callshow, null);
        videoView = (IjkVideoViewCallShow) mainView.findViewById(R.id.videoView);
        videoView.isCallShow = true;
        progressBar = (ProgressBar) mainView.findViewById(R.id.progressBar);
        videoPauseBtn = (LinearLayout) mainView.findViewById(R.id.videoPauseBtn);
        videoPauseImg = (ImageView) mainView.findViewById(R.id.videoPauseImg);
        screen_status_btn = (LinearLayout) mainView.findViewById(R.id.screen_status_btn);
        screen_status_img = (ImageView) mainView.findViewById(R.id.screen_status_img);
        viewBox = (RelativeLayout) mainView.findViewById(R.id.viewBox);
        progressBar.setVisibility(View.GONE);//View.VISIBLE
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        if (Build.VERSION.SDK_INT >= 17) {
            videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                    return false;
                }
            });
        }
        // 注册在设置或播放过程中发生错误时调用的回调函数。如果未指定回调函数，或回调函数返回false，VideoView 会通知用户发生了错误。
        videoView.setOnErrorListener(this);
        //videoView.setSettingsPlayer(Settings.PV_PLAYER__Auto);
        addView(mainView);
    }

    public void init() {
        // initAn();
    }




    /***
     * 根据存储进度设置播放进度
     * @param skipDuration
     */
    public void setSkipDuration(int skipDuration) {
        this.skipDuration = skipDuration;
    }


    /**
     * 传入参数
     *
     * @param url
     */
    public void setData(String url, int parentHeight) {
        this.playUrl = url;
    }

    /**
     * 设置activity
     *
     * @param activity
     */
    public void setActivity(Activity activity) {
        mActivity = activity;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        if(onJikPreparedListener!=null){
//            onJikPreparedListener.onprepared(mp);
//        }
        progressBar.setVisibility(View.GONE);
        mp.start();
        mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//				NewLogUtils.d("videoview", "", "percent="+percent);
            }
        });
//        NewLogUtils.d("VideoSmallView", "", "onPrepared playUrl=" + playUrl);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        NewLogUtils.d("VideoSmallView", "", "onError");
//		NewLogUtils.d("videoview", "erro", mp+":what="+what+":extra="+extra+"erro dur="+videoView.getCurrentPosition());
//        Message msg = new Message();
//        msg.what = TEST_REPLAY;
//        msg.arg1 = videoView.getCurrentPosition();
//        mHandler.sendMessageDelayed(msg, 2000);
        //不用循环播放，防止界面卡死
        videoView.stopPlayback();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        videoView.seekTo(0);
    }


    /**
     * 开始播放
     */
    public void start() {
        if (!TextUtils.isEmpty(playUrl)) {
            doPlay();
        }
    }

    private void doPlay() {
        progressBar.setVisibility(View.GONE);//View.VISIBLE
        HashMap<String, String> headerMap = new HashMap<String, String>();
//		headerMap.put("User-Agent", "MOBILE");
        headerMap.put("User-Agent", "Android");
        headerMap.put("Accept-Encoding", "gzip,deflate");
        headerMap.put("Accept-Language", "zh-cn");
        headerMap.put("Accept", "*/*");
        if(videoView ==null ){
            return;
        }
        videoView.setVideoURI(Uri.parse(playUrl), headerMap);
        videoView.requestFocus();
       if (showBtn){
           if (mHandler!=null){
               mHandler.sendEmptyMessageDelayed(1,1000);
           }
       }
        stopDurationTimer();
        startDurationTimer();
//        NewLogUtils.d("VideoSmallView", "play playUrl = ", playUrl);
    }

    private String resouce_id;


    public void start(String play_url) {
        if (!TextUtils.isEmpty(play_url)) {
            this.playUrl = play_url;
//            NewLogUtils.d("VideoSmallView", "", "play_url=" + play_url);
            if (showBtn){
                screen_status_btn.setVisibility(GONE);
                screen_status_btn.setOnClickListener(this);
                videoPauseBtn.setVisibility(VISIBLE);
                videoPauseBtn.setOnClickListener(this);
                viewBox.setOnClickListener(this);
                start();
            }else {
                start();
            }

        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        videoView.pause();
        stopDurationTimer();
    }

    /**
     * 继续播放
     */
    public void resume() {
        videoView.start();
        startDurationTimer();
    }

    /**
     * activity 执行onPause时调用
     */
    public void onPause() {
        if (videoView != null && videoView.isPlaying()) {
            progressBar.setVisibility(View.GONE);//View.VISIBLE
            stopDurationTimer();
            pause();
            mPositionWhenPaused = videoView.getCurrentPosition();
        }
//        NewLogUtils.d("VideoSmallView", "", "onPause");
    }

    /**
     * activity 执行onResume时调用
     */
    public void onResume() {
        if (videoView != null && mPositionWhenPaused >= 0) {
            videoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
            progressBar.setVisibility(View.GONE);//View.VISIBLE
            startDurationTimer();
        }
//        NewLogUtils.d("VideoSmallView", "", "onResume");
    }

    /**
     * 移除handler消息
     */
    private void removeHandler() {
        if (mHandler != null) {
            mHandler.removeMessages(TEST_REPLAY);
        }
    }

    /**
     * 销毁方法
     */
    public void destory() {
        if (videoView != null) {
            stopDurationTimer();
            pause();
            videoView.stopPlayback();
            videoView = null;
        }
        removeHandler();
//        NewLogUtils.d("VideoSmallView", "", "destory");
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
//        NewLogUtils.d("VideoSmallView", "", "onError");
//		NewLogUtils.d("videoview", "erro", mp+":what="+what+":extra="+extra+"erro dur="+videoView.getCurrentPosition());
        stopDurationTimer();
        Message msg = new Message();
        msg.what = TEST_REPLAY;
        msg.arg1 = videoView.getCurrentPosition();
        mHandler.sendMessageDelayed(msg, 2000);
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        if(onJikPreparedListener!=null){
            onJikPreparedListener.onprepared(iMediaPlayer);
        }
        progressBar.setVisibility(View.GONE);

        if (!showBtn){
            iMediaPlayer.start();
        }

        iMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
//                NewLogUtils.d("videoview", "", "percent=" + percent);
            }

        });
//        NewLogUtils.d("VideoSmallView", "", "onPrepared playUrl=" + playUrl);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        videoView.seekTo(0);
        if(onJikPreparedListener!=null){
            onJikPreparedListener.onJikCompletion(iMediaPlayer);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.videoPauseBtn) {
            if (videoView.isPlaying()) {
                pause();
//                    videoPauseImg.setImageResource(R.drawable.icon_play);
            } else {
//                    videoPauseImg.setImageResource(R.drawable.icon_stop);
                resume();
            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        } else if (i == R.id.viewBox) {
            if (videoPauseBtn.getVisibility() == VISIBLE) {
                videoPauseBtn.setVisibility(GONE);
                screen_status_btn.setVisibility(GONE);
            } else {
                videoPauseBtn.setVisibility(VISIBLE);
                screen_status_btn.setVisibility(VISIBLE);
            }
        } else if (i == R.id.screen_status_btn) {
            setOrientation();
        }
    }

    public interface OnJikPreparedListener{
        public void onprepared(IMediaPlayer iMediaPlayer);
        public void onJikCompletion(IMediaPlayer iMediaPlayer);
    }
    OnJikPreparedListener onJikPreparedListener;
    public void setOnJikPreparedListener(OnJikPreparedListener onJikPreparedListener){
        this.onJikPreparedListener = onJikPreparedListener;
    }

    /**
     * 设置横竖屏
     */
    private void setOrientation() {
//        NewLogUtils.d("videoview", "", "mActivity.getRequestedOrientation()=" + mActivity.getRequestedOrientation());
        if (mActivity != null) {
            if (mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                NewLogUtils.d("videoview", "", "SCREEN_ORIENTATION_PORTRAIT=");
                if (mActivity != null) {
                    WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
                    attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mActivity.getWindow().setAttributes(attrs);
                    mActivity.getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            } else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                NewLogUtils.d("videoview", "", "SCREEN_ORIENTATION_LANDSCAPE=");
                if (mActivity != null) {
                    WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    mActivity.getWindow().setAttributes(attrs);
                    mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
            }
        }
    }

    public boolean onKeyDown() {
        if (mActivity != null) {
            if (mActivity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setOrientation();
                return true;
            }
        }
        return false;
    }
}
