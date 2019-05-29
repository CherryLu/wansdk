package com.wan.callring.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.gghl.callshow.R;
import com.gghl.callshow.bean.Contact;
import com.gghl.callshow.util.CommUtils;
import com.gghl.callshow.util.ContactUtils;
import com.gghl.callshow.util.PhoneLocationUtils;

import java.io.IOException;
import java.lang.reflect.Method;


public class PhoneTipController2 implements View.OnClickListener, View.OnTouchListener, ViewContainer.KeyEventHandler {

    private static final String TAG = "PhoneTipController2";
    private WindowManager mWindowManager;
    private Context mContext;
    private RelativeLayout mWholeView;
    private ViewDismissHandler mViewDismissHandler;
    private String mNumber;
    VidewView2 videoView;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_location;
    private ImageView iv_right_close;
//    private ImageView iv_bg;
    private WindowManager.LayoutParams layoutParams;
    private HomeKeyBroadcastReceiver mHomeKeyBroadcastReceiver;
    private boolean isUnRegister;
    private ContactUtils contactUtils;
    private String videoName;//视频保存在本地的名称

    TextView tv_jingyin;
    LinearLayout ll_layout;
    AudioManager mAudioManager;
//    int currentRing;
    int currentMusic;
    tv.danmaku.ijk.media.player.IMediaPlayer mp;
    ProgressBar video_progressbar;
    public PhoneTipController2(Context application, String number, String videoName) {
        mContext = application;
        mNumber = number;
        contactUtils=new ContactUtils(application);
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);

        mHomeKeyBroadcastReceiver=new HomeKeyBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.registerReceiver(mHomeKeyBroadcastReceiver, intentFilter);
        this.videoName = videoName;

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //铃声音量
//        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_RING );
//        currentRing = mAudioManager.getStreamVolume( AudioManager.STREAM_RING );
        currentMusic = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC);
//        Log.e("RING", " current : " + currentRing+" max:"+max);
//        if(currentRing>0){
//            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,0,AudioManager.FLAG_ALLOW_RINGER_MODES);
//        }


    }

    public void setViewDismissHandler(ViewDismissHandler viewDismissHandler) {
        mViewDismissHandler = viewDismissHandler;
    }

    ImageView imageview_shelldow;
    public void show() {
        mWholeView = (RelativeLayout) View.inflate(mContext, R.layout.callshow_answer_view, null);
        ll_layout = (LinearLayout)mWholeView.findViewById(R.id.ll_layout);
        imageview_shelldow = (ImageView) mWholeView.findViewById(R.id.imageview_shelldow);
        tv_name= (TextView) mWholeView.findViewById(R.id.tv_name);
        tv_phone= (TextView) mWholeView.findViewById(R.id.tv_phone);
        tv_location= (TextView) mWholeView.findViewById(R.id.tv_location);
        iv_right_close = (ImageView)mWholeView.findViewById(R.id.iv_right_close);
        tv_jingyin = (TextView)mWholeView.findViewById(R.id.tv_jingyin);
        tv_jingyin.setVisibility(View.GONE);
        video_progressbar = (ProgressBar)mWholeView.findViewById(R.id.video_progressbar);
        iv_right_close.setOnClickListener(this);
        tv_jingyin.setOnClickListener(this);
//        iv_bg= (ImageView) mWholeView.findViewById(R.id.iv_bg);

        videoView = (VidewView2)mWholeView.findViewById(R.id.videoview);
//        videoView.setZOrderOnTop(true);

//        iv_bg.setImageResource(imgs[new Random().nextInt(5)]);

//        mWholeView.setOnTouchListener(this);
//        mWholeView.setKeyEventHandler(this);
        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.MATCH_PARENT;
//WindowManager.LayoutParams.FLAG_FULLSCREEN

        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        int type = 0;
        if(Build.VERSION.SDK_INT >= 26){
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//            type = WindowManager.LayoutParams.TYPE_TOAST;
//            Log.e("netutil",Build.MANUFACTURER+"2");
        } else  if(Build.VERSION.SDK_INT >= 25){
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if("Meizu".equals(Build.MANUFACTURER)&& Build.VERSION.SDK_INT==25){
                type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }else{
                if("Xiaomi".equals(Build.MANUFACTURER)){
                    type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//TYPE_TOAST
                }else {
                    type = WindowManager.LayoutParams.TYPE_TOAST;//TYPE_TOAST
                }
            }
        } else {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
//        ScreenUtils.getScreenHeight(mContext)

        int width = CommUtils.getScreenWidth()*2/3;
        int height = width*16/9;
        layoutParams = new WindowManager.LayoutParams(width, height, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.y = CommUtils.getScreenHeight()*1/8;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL| Gravity.TOP ;


        //设置视频控制器
//        videoView.setMediaController(null);

        //播放完成回调
//        videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
        //        //本地的视频 需要在手机SD卡根目录添加一个 fl1234.mp4 视频
//        String videoUrl1 = Environment.getExternalStorageDirectory().getPath()+"/fl1234.mp4" ;
//        //网络视频
//        String videoUrl2 = Utils.videoUrl ;
        String url =null;
//        if(videoName.equals("-1")){
//            url = "android.resource://" + mContext.getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
//        }else{
//             url = Environment.getExternalStorageDirectory()+"/callshowdown/"+videoName;
////            url = "android.resource://" + mContext.getPackageName() + "/" + R.raw.nba_01;
//            if(!new File(url).exists()){
//                url = "android.resource://" + mContext.getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
//            }
//        }
//        Uri uri = Uri.parse(url);
//        //设置视频路径
//        videoView.setVideoURI(uri);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ll_layout.setFocusableInTouchMode(true);
        ll_layout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                return false;
            }
        });
        String[] ret=getTips();
        tv_name.setText(ret[0]);
        tv_phone.setText(ret[1]);
        tv_location.setText(ret[2]);
        //开始播放视频
        if(videoName.equals("-1")){
           String urllocal = "android.resource://" + mContext.getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
            Uri uri = Uri.parse(urllocal);
//        //设置视频路径
//            videoView.setVideoURI(uri);
            videoView.start(urllocal);//
        }else{
//             url = Environment.getExternalStorageDirectory()+"/callshowdown/"+videoName;
////            url = "android.resource://" + mContext.getPackageName() + "/" + R.raw.nba_01;
//            if(!new File(url).exists()){
//                url = "android.resource://" + mContext.getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
//            }
            Uri uri = Uri.parse(videoName);
//        //设置视频路径
//            videoView.setVideoURI(uri);
            videoView.start(videoName);//
        }
//        videoView.start();
        mWindowManager.addView(mWholeView, layoutParams);
        System.out.println("下载结果："+"mWindowManager.addView");
        boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
        if(!videoSwitch){
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_callshow_jingyin_h);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            tv_jingyin.setCompoundDrawables(null,drawable,null,null);
        }else{
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_callshow_jingyin_n);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            tv_jingyin.setCompoundDrawables(null,drawable,null,null);
        }
        startDurationTimer();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                imageview_shelldow.setVisibility(View.GONE);
//                video_progressbar.setVisibility(View.GONE);
//                PhoneTipController2.this.mp = mediaPlayer;
////                mp.setVolume(current,current);
//                boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
//                Log.e("netutil","iMediaPlayer onprepared "+"videoSwitch"+videoSwitch);
//                if(!videoSwitch){
//                    mp.setVolume(0,0);
////                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_PLAY_SOUND);
//                }
//            }
//        });
        videoView.setOnJikPreparedListener(new VidewView2.OnJikPreparedListener() {
            @Override
            public void onprepared(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                imageview_shelldow.setVisibility(View.GONE);
                video_progressbar.setVisibility(View.GONE);
                PhoneTipController2.this.mp = iMediaPlayer;
//                mp.setVolume(current,current);
                boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
                Log.e("netutil","iMediaPlayer onprepared "+"videoSwitch"+videoSwitch);
                if(!videoSwitch){
                    mp.setVolume(0,0);
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_PLAY_SOUND);
                }
            }

            @Override
            public void onJikCompletion(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {

            }
        });
//        setMode();
    }
    public void closeVoice() {
        mp.setVolume(0,0);
    }
    /**
     * isCall=true拨打否则接听
     */
    public void setMode(){
//            rl_answer_layout.setVisibility(View.GONE);
//            rl_call_layout.setVisibility(View.GONE);
    }


    boolean stateCome;
    public void setState(boolean state) {
        this.stateCome = state;
        setMode();
    }

    public void upDateUrl(String videoName2) {
//        videoView.pause();
//        String url = Environment.getExternalStorageDirectory()+"/callshowdown/"+videoName2;
//        if(!new File(url).exists()){
//            url = "android.resource://" + mContext.getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
//        }
        Uri uri = Uri.parse(videoName2);
//        videoView.setVideoURI(uri);
        videoView.start(videoName2);//
//        mWholeView.requestFocus();
    }
    public void upDateLiveUrl(String url) {
//        videoView.pause();
        Uri uri = Uri.parse(url);
//        videoView.setVideoURI(uri);
        videoView.start(url);//
//        mWholeView.requestFocus();
    }

    public void hide(){
        stopDurationTimer();
        if(!isUnRegister){
            mContext.unregisterReceiver(mHomeKeyBroadcastReceiver);
            isUnRegister=true;
        }
        removePoppedViewAndClear();

    }



    private void removePoppedViewAndClear() {
        if(videoView!=null){
//            videoView.destory();
            videoView.pause();
        }
        if (mWindowManager != null && mWholeView != null) {
            mWindowManager.removeView(mWholeView);
        }

        if (mViewDismissHandler != null) {
            mViewDismissHandler.onViewDismiss();
        }
//        if(currentRing>0){
//            mAudioManager.setStreamVolume(AudioManager.STREAM_RING,currentRing,AudioManager.FLAG_ALLOW_RINGER_MODES);
//        }
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentMusic,AudioManager.FLAG_PLAY_SOUND);
        mWholeView=null;

    }

//    private int rawX;
//    private int rawY;
//    private float mTouchStartX;
//    private float mTouchStartY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        int statusHeight= ScreenUtils.getStatusHeight(mContext);
//        rawX = (int) event.getRawX();
//        rawY = (int) event.getRawY()-statusHeight;
       // Log.e(TAG, "currX"+rawX+"====currY"+rawY);
        //获取相对View的坐标，即以此View左上角为原点
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取相对View的坐标，即以此View左上角为原点
//                mTouchStartX =  event.getX();
//                mTouchStartY =  event.getY();
               // Log.e(TAG, "startX" + mTouchStartX+"====startY"+mTouchStartY);
                break;
            case MotionEvent.ACTION_MOVE:
//                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
//                updateViewPosition();
//                mTouchStartX=mTouchStartY=0;
                break;
        }
        return false;
    }

//    private void updateViewPosition(){
//        //更新浮动窗口位置参数
//        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
//        layoutParams.x=(int)( rawX-mTouchStartX);
//        layoutParams.y=(int) (rawY-mTouchStartY);
//        mWindowManager.updateViewLayout(mWholeView, layoutParams);  //刷新显示
//    }

    @Override
    public void onKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hide();
        }
    }


    public String[] getTips(){
        String[] ret=new String[3];
        Contact contact = null;
        try{
             contact=contactUtils.getContactByNumber(mNumber);
            if(contact==null){
                contact = contactUtils.getContactByNumberJia86(mNumber);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String[] phonelocation= PhoneLocationUtils.getPhoneLocation(mContext, mNumber);
        if(contact==null){
            ret[0]=mNumber;
            ret[1]="陌生号码";
            ret[2]=phonelocation[0]+" "+phonelocation[1];
        }else {
            ret[0]=contact.getDisplay_name();
            ret[1]=mNumber;
            ret[2]=phonelocation[0]+" "+phonelocation[1];
        }
        return ret;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_layout:
                break;
            case R.id.iv_right_close:
                hide();
                break;
            case R.id.iv_phone_center:
                   if(stateCome){
                       answerCall();
                   }else {
                       endCall();
                   }
                break;
            case R.id.tv_mianti:
//                if(openSpeaker){
//                    closeSpeaker();
//                    tv_mianti.setCompoundDrawables(null,mContext.getResources().getDrawable(R.drawable.icon_callshow_open_y),null,null);
//                }else{
//                    openSpeaker();
//                    tv_mianti.setCompoundDrawables(null,mContext.getResources().getDrawable(R.drawable.icon_callshow_close_y),null,null);
//                }

                break;
            case R.id.iv_phone_center_answer:
                answerCall();
                break;
            case R.id.tv_jingyin:
                boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
                Log.e("videoSwitch",""+videoSwitch+"--"+currentMusic);
                if(videoSwitch){
                    this.mp.setVolume(0,0);
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_PLAY_SOUND);
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_callshow_jingyin_h);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    tv_jingyin.setCompoundDrawables(null,drawable,null,null);
                }else{
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentMusic,AudioManager.FLAG_PLAY_SOUND);
                    this.mp.setVolume(currentMusic,currentMusic);
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_callshow_jingyin_n);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    tv_jingyin.setCompoundDrawables(null,drawable,null,null);
                }
                Preferences.saveBoolean(Preferences.VIDEO_SWITCH,!videoSwitch);
        }

    }
    int currPosintion;
    public static final int UPDATE_DURATION_TIP_2 = 1000112;
    private void startDurationTimer() {
        handler.sendEmptyMessageDelayed(UPDATE_DURATION_TIP_2,1000);
    }

    /**
     * 关闭进度检测定时器
     */
    private void stopDurationTimer() {
        handler.removeMessages(UPDATE_DURATION_TIP_2);
    }
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_DURATION_TIP_2:
                    int duration = videoView.getCurrDuration();//CurrentPosition()
                    Log.e("duration",duration+":"+currPosintion);
                    if(duration==currPosintion){
                        video_progressbar.setVisibility(View.GONE);//VISIBLE
                    }else {
                        video_progressbar.setVisibility(View.GONE);
                    }
                    currPosintion = duration;
                    handler.sendEmptyMessageDelayed(UPDATE_DURATION_TIP_2,1000);
                    break;
                default:
                    break;
            }
        }
    };

    public interface ViewDismissHandler {
        void onViewDismiss();
    }
    private void answerCall() {
        TelephonyManager telMag = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method mthEndCall = null;
        try {
            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            mthEndCall.setAccessible(true);
            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
                    (Object[]) null);
            iTel.answerRingingCall();
//            LogOut.out(this, iTel.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LogOut.out(this, "answer call");
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            acceptCall(mContext);
//        } else {
////            acceptCall_4_1(mContext);
//            acceptCall(mContext);
//        }

    }

    /** 接听电话*/
    public void acceptCall(Context context) {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (Exception e) {
            Log.e(TAG, "for version 4.1 or larger");
//            acceptCall_4_1(context);
        }
    }

    /**4.1版本以上接听电话*/
    public void acceptCall_4_1(Context context) {
//        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ;
        //模拟无线耳机的按键来接听电话
        // for HTC devices we need to broadcast a connected headset
//        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
//                && !audioManager.isWiredHeadsetOn();
//        if (broadcastConnected) {
//            broadcastHeadsetConnected(context, false);
//        }
        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                context.sendOrderedBroadcast(btnDown, enforcedPerm);
                context.sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
//            if (broadcastConnected) {
                broadcastHeadsetConnected(context, false);
//            }
        }
    }

    private void broadcastHeadsetConnected(Context context, boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            context.sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }
    private void endCall() {
        TelephonyManager telMag = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method mthEndCall = null;
        try {
            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            mthEndCall.setAccessible(true);
            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
                    (Object[]) null);
            iTel.endCall();
//            LogOut.out(this, iTel.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        LogOut.out(this, "endCall test");
    }


    class HomeKeyBroadcastReceiver extends BroadcastReceiver {
        private final String SYSTEM_REASON = "reason";
        //Home键
        private final String SYSTEM_HOME_KEY = "homekey";
        //最近使用的应用键
        private final String SYSTEM_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String systemReason = intent.getStringExtra(SYSTEM_REASON);
                if (systemReason != null) {
                    if (systemReason.equals(SYSTEM_HOME_KEY)) {
                        Log.e(TAG, "按下HOME键");
                        hide();
                    } else if (systemReason.equals(SYSTEM_RECENT_APPS)) {
                        Log.e(TAG, "按下RECENT_APPS键");
                        hide();
                    }
                }
            }

        }

    }
    Boolean openSpeaker =false;
    private int currVolume = 0;
    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
            openSpeaker = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
            openSpeaker = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
