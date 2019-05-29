package com.wan.callring.utils;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.wan.callring.R;
import com.wan.callring.bean.Contact;
import com.wan.callring.ui.widget.VidewView2;

import java.io.IOException;
import java.lang.reflect.Method;


public class PhoneTipController implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = "PhoneTipController";
    private WindowManager mWindowManager;
    private Context mContext;
    private RelativeLayout mWholeView;
    private ViewDismissHandler mViewDismissHandler;
    private String mNumber;
    VidewView2 videoView;

    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_location;
    private TextView tv_jingyin;
    private ImageView iv_right_close;
    private WindowManager.LayoutParams layoutParams;
    private HomeKeyBroadcastReceiver mHomeKeyBroadcastReceiver;
    private boolean isUnRegister;
    private ContactUtils contactUtils;
    private String videoName;//视频保存在本地的名称
    tv.danmaku.ijk.media.player.IMediaPlayer mp;
    AudioManager mAudioManager;
    ProgressBar video_progressbar;
    ImageView iv_phone_end;
    int currentMusic;
    public PhoneTipController(Context application, String number, String videoName) {
        mContext = application;
        mNumber = number;
        contactUtils=new ContactUtils(application);
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);

        mHomeKeyBroadcastReceiver=new HomeKeyBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mContext.registerReceiver(mHomeKeyBroadcastReceiver, intentFilter);
        this.videoName = videoName;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        currentMusic = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC);

    }

    public void setViewDismissHandler(ViewDismissHandler viewDismissHandler) {
        mViewDismissHandler = viewDismissHandler;
    }

    ImageView imageview_shelldow;
    String myNum;
    ConnManager connManager;
    public void show(String myNum) {
        this.myNum = myNum;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        //媒体音量
//        current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
//        if(current>1){
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,AudioManager.STREAM_MUSIC);
//        }
        mWholeView = (RelativeLayout) View.inflate(mContext, R.layout.callshow_call_view, null);
//        ll_layout = (LinearLayout)mWholeView.findViewById(R.id.ll_layout);
        imageview_shelldow = (ImageView) mWholeView.findViewById(R.id.imageview_shelldow);
        tv_name= (TextView) mWholeView.findViewById(R.id.tv_name);
        tv_phone= (TextView) mWholeView.findViewById(R.id.tv_phone);
        tv_location= (TextView) mWholeView.findViewById(R.id.tv_location);
        tv_jingyin = (TextView)mWholeView.findViewById(R.id.tv_jingyin);
        tv_jingyin.setVisibility(View.GONE);
        video_progressbar = (ProgressBar)mWholeView.findViewById(R.id.video_progressbar);
        iv_right_close = (ImageView)mWholeView.findViewById(R.id.iv_right_close);
        iv_phone_end = (ImageView)mWholeView.findViewById(R.id.iv_phone_end);
        iv_phone_end.setOnClickListener(this);
        iv_right_close.setOnClickListener(this);
        tv_jingyin.setOnClickListener(this);

        videoView = (VidewView2) mWholeView.findViewById(R.id.videoview);
        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.MATCH_PARENT;
//WindowManager.LayoutParams.FLAG_FULLSCREEN WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |

        int flags =  WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

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



        layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);//RGBA_8888
        String url =null;
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        String[] ret=getTips();
        tv_name.setText(ret[0]);
        tv_phone.setText(ret[1]);
        tv_location.setText(ret[2]);
        //开始播放视频
        if(videoName.equals("-1")){
            String urlLocal = "android.resource://" + mContext.getPackageName() + "/" + R.raw._2017_09_15_12_28_53;
            Uri uri = Uri.parse(urlLocal);
//        //设置视频路径
//            videoView.setVideoURI(uri);
            videoView.start(urlLocal);//urlLocal
        }else{
            Uri uri = Uri.parse(videoName);
//        //设置视频路径
//            videoView.setVideoURI(uri);
            videoView.start(videoName);//videoName
        }
//        videoView.start();
        mWindowManager.addView(mWholeView, layoutParams);
        boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
        if(!videoSwitch){
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_callshow_jingyin_h);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            tv_jingyin.setCompoundDrawables(null,drawable,null,null);
        }else{
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_callshow_jingyin_n);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            tv_jingyin.setCompoundDrawables(null,drawable,null,null);
        }
        startDurationTimer();
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                imageview_shelldow.setVisibility(View.GONE);
//                video_progressbar.setVisibility(View.GONE);
////                ll_layout.bringToFront();
//                PhoneTipController.this.mp = mediaPlayer;
////                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,AudioManager.STREAM_MUSIC);
////                iMediaPlayer.setVolume(0,0);//current,current
//                boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
//                Log.e("netutil","iMediaPlayer onprepared isCloseVoice="+isCloseVoice+"  videoSwitch:"+videoSwitch);
//                if(!videoSwitch){
//                    mediaPlayer.setVolume(0,0);
//                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_PLAY_SOUND);
//                }
////                rl_call_layout.requestFocus();
////                rl_answer_layout.requestFocus();
//                if(isCloseVoice){
//                    closeVoice();
//                }
//            }
//        });
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                if(mWholeView!=null){
//                    mediaPlayer.setVolume(0,0);
////                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_PLAY_SOUND);
//                }
//            }
//        });
        videoView.setOnJikPreparedListener(new VidewView2.OnJikPreparedListener() {
            @Override
            public void onprepared(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                imageview_shelldow.setVisibility(View.GONE);
                PhoneTipController.this.mp = iMediaPlayer;
//                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,AudioManager.STREAM_MUSIC);
//                iMediaPlayer.setVolume(0,0);//current,current
                boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH,true);
                if(!videoSwitch){
                    iMediaPlayer.setVolume(0,0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0, AudioManager.FLAG_PLAY_SOUND);
                }
                if(isCloseVoice){
                    closeVoice();
                }
            }

            @Override
            public void onJikCompletion(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                if(mWholeView!=null){
                    Log.e("netutil","onJikCompletion="+2);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0, AudioManager.FLAG_PLAY_SOUND);
                }
            }
        });
        mWholeView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK||event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS){
                    hide();
                }
                return false;
            }
        });
//        handler.sendEmptyMessageDelayed(12345,1000);ConnManager
        connManager = new ConnManager();
        connManager.connect(handler);

    }
    int currPosintion;
    public static final int UPDATE_DURATION_TIP = 1000111;
    private void startDurationTimer() {
        handler.sendEmptyMessageDelayed(UPDATE_DURATION_TIP,1000);
    }

    /**
     * 关闭进度检测定时器
     */
    private void stopDurationTimer() {
        handler.removeMessages(UPDATE_DURATION_TIP);
    }
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ConnManager.STATE_FROM_SERVER_OK:
                    Log.e("netu","STATE_FROM_SERVER_OK"+msg.obj.toString());
                    if(mp!=null){
                        mp.setVolume(0,0);
                    }
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0, AudioManager.FLAG_PLAY_SOUND);
                    hide();
                    break;
                case ConnManager.STATE_Success:
                    Log.e("zx","handler 发送");
                    connManager.putRequest("AM"+myNum);
                    break;
                case UPDATE_DURATION_TIP:
                    int duration = videoView.getCurrDuration();//CurrentPosition();
                    Log.e("duration",duration+":"+currPosintion);
                    if(duration==currPosintion){
                        video_progressbar.setVisibility(View.GONE);//VISIBLE
                    }else {
                        video_progressbar.setVisibility(View.GONE);
                    }
                    currPosintion = duration;
                    handler.sendEmptyMessageDelayed(UPDATE_DURATION_TIP,1000);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * isCall=true拨打否则接听
     */
    public void setMode(Boolean isCall){
//        if(isCall){
//            rl_answer_layout.setVisibility(View.VISIBLE);
//            rl_call_layout.setVisibility(View.GONE);
//        }else{
//            rl_answer_layout.setVisibility(View.GONE);
//            rl_call_layout.setVisibility(View.VISIBLE);
//        }
    }


    boolean stateCome;
    public void setState(boolean state) {
        this.stateCome = state;
        setMode(state);
    }

    public void upDateUrl(String videoName2) {
//        if(videoView.isPlaying())
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
//        if(videoView.isPlaying())
//        videoView.pause();
        Uri uri = Uri.parse(url);
//        videoView.setVideoURI(uri);
        videoView.start(url);//
    }
    boolean isCloseVoice = false;
    public void closeVoice() {
        if(mp!=null){
            mp.setVolume(0,0);
        }
        isCloseVoice = true;
    }

    public void hide(){
        stopDurationTimer();
        connManager.disConnect();
        if(!isUnRegister){
            mContext.unregisterReceiver(mHomeKeyBroadcastReceiver);
            isUnRegister=true;
        }
        removePoppedViewAndClear();

    }



    private void removePoppedViewAndClear() {
        if (mWindowManager != null && mWholeView != null&&mWholeView.getParent()!=null) {
            videoView.pause();
            mWindowManager.removeView(mWholeView);
        }

        if (mViewDismissHandler != null) {
            mViewDismissHandler.onViewDismiss();
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,currentMusic, AudioManager.FLAG_PLAY_SOUND);
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

//    @Override
//    public void onKeyEvent(KeyEvent event) {
//        int keyCode = event.getKeyCode();
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            hide();
//        }
//    }


    public String[] getTips(){
        String[] ret=new String[3];
        Contact contact = null;
        try {
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
        int i = v.getId();
        if (i == R.id.iv_phone_end) {
            endCall();
        } else if (i == R.id.iv_right_close) {
            hide();
        } else if (i == R.id.tv_jingyin) {
            boolean videoSwitch = Preferences.getBoolean(Preferences.VIDEO_SWITCH, true);

            if (videoSwitch) {
                this.mp.setVolume(0, 0);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_callshow_jingyin_h);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                tv_jingyin.setCompoundDrawables(null, drawable, null, null);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMusic, AudioManager.FLAG_PLAY_SOUND);
                this.mp.setVolume(currentMusic, currentMusic);
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_callshow_jingyin_n);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                tv_jingyin.setCompoundDrawables(null, drawable, null, null);
            }
            Preferences.saveBoolean(Preferences.VIDEO_SWITCH, !videoSwitch);
        } /*else if (i == R.id.iv_phone_center) {
            if (stateCome) {
                answerCall();
            } else {
                endCall();
            }
        } else if (i == R.id.tv_mianti) {
            hide();
//                AudioManager mAudioManager1 = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//                mAudioManager1.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                mAudioManager1.setSpeakerphoneOn(true);
////                //通话时设置免提
////                mAudioManager1.setSpeakerphoneOn(!mAudioManager1.isSpeakerphoneOn());
////                System.out.println("isSpeakerphoneOn =" + mAudioManager1.isSpeakerphoneOn());
//                if(mAudioManager1.isSpeakerphoneOn()){//openSpeaker
////                    closeSpeaker();
//                    mAudioManager1.setMode(AudioManager.MODE_NORMAL);
//                    mAudioManager1.setSpeakerphoneOn(false);
//                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_callshow_close_y);
//                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//                    tv_mianti.setCompoundDrawables(null,drawable,null,null);
//                }else{
////                    openSpeaker();
//                    mAudioManager1.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                    try {
//                        Class clazz = Class.forName("android.media.AudioSystem");
//                        Method m = clazz.getMethod("setForceUse", new Class[]{int.class, int.class});
//                        m.setAccessible(true);
//                        m.invoke(null, 1, 1);
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                    mAudioManager1.setSpeakerphoneOn(true);
//                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_callshow_open_y);
//                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//                    tv_mianti.setCompoundDrawables(null,drawable,null,null);
//                }
        } else if (i == R.id.iv_phone_center_answer) {
            answerCall();
        } else if (i == R.id.iv_phone_refuse_2) {
            endCall();
        }*/

    }


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
    private int currVolume = 0;
    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//            audioManager.setMode(AudioManager.MODE_NORMAL);//ROUTE_SPEAKER
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_NORMAL);//MODE_IN_CALL
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
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
                    audioManager.setMode(AudioManager.MODE_NORMAL);
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean getCallLogState(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            ContentResolver cr = mContext.getContentResolver();
            Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION}, CallLog.Calls.NUMBER + "=? and " + CallLog.Calls.TYPE + "= 2", new String[]{phoneNumber}, CallLog.Calls.DEFAULT_SORT_ORDER);

            //while(cursor.moveToNext()){
            Log.e("callLog","phoneNumber"+phoneNumber);
            if (cursor.moveToFirst()){
                int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);
                long durationTime = cursor.getLong(durationIndex);
                Log.e("callLog",durationTime+"");
                if(durationTime > 0){
                    cursor.close();
                    return true;
                } else {
                    cursor.close();
                    return false;
                }
            }

            //  }
        }
        return false;
    }
}
