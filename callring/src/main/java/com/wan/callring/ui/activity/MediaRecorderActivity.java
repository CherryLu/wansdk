package com.wan.callring.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mabeijianxi.smallvideorecord2.CustromUtils;
import com.mabeijianxi.smallvideorecord2.DeviceUtils;
import com.mabeijianxi.smallvideorecord2.FileUtils;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress;
import com.mabeijianxi.smallvideorecord2.Log;
import com.mabeijianxi.smallvideorecord2.MediaRecorderBase;
import com.mabeijianxi.smallvideorecord2.MediaRecorderNative;
import com.mabeijianxi.smallvideorecord2.ProgressView;
import com.mabeijianxi.smallvideorecord2.StringUtils;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.BaseMediaBitrateConfig;
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig;
import com.mabeijianxi.smallvideorecord2.model.MediaObject;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.mabeijianxi.smallvideorecord2.model.OnlyCompressOverBean;
import com.wan.callring.R;
import com.wan.callring.utils.RecordVieoUtils;
import com.wan.callring.utils.UriUtils;

import java.io.File;


/**
 * 视频录制
 */
public class MediaRecorderActivity extends BaseActivity implements
        MediaRecorderBase.OnErrorListener, OnClickListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener {

    private int RECORD_TIME_MIN = (int) (1.5f * 1000);
    /**
     * 录制最长时间
     */
    private int RECORD_TIME_MAX = 6 * 1000;
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;

    /**
     * 下一步
     */
    private View mTitleNext;
    /**
     * 前后摄像头切换
     */
    private CheckBox mCameraSwitch;
    /**
//     * 回删按钮、延时按钮、滤镜按钮
//     */
//    private CheckedTextView mRecordDelete;
    /**
     * 闪光灯
     */
    private CheckBox mRecordLed;
    /**
     * 拍摄按钮
     */
    private TextView mRecordController;

    /**
     * 底部条
     */
    private RelativeLayout mBottomLayout;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * 录制进度
     */
    private ProgressView mProgressView;

    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;

    /**
     * 是否是点击状态
     */
    private volatile boolean mPressedStatus;
    /**
     * 是否已经释放
     */
    private volatile boolean mReleased;
    /**
     * 视屏地址
     */
    public final static String VIDEO_URI = "video_uri";
    /**
     * 本次视频保存的文件夹地址
     */
    public final static String OUTPUT_DIRECTORY = "output_directory";
    /**
     * 视屏截图地址
     */
    public final static String VIDEO_SCREENSHOT = "video_screenshot";
    /**
     * 录制完成后需要跳转的activity
     */
    public final static String OVER_ACTIVITY_NAME = "over_activity_name";
    /**
     * 最大录制时间的key
     */
    public final static String MEDIA_RECORDER_MAX_TIME_KEY = "media_recorder_max_time_key";
    /**
     * 最小录制时间的key
     */
    public final static String MEDIA_RECORDER_MIN_TIME_KEY = "media_recorder_min_time_key";
    /**
     * 录制配置key
     */
    public final static String MEDIA_RECORDER_CONFIG_KEY = "media_recorder_config_key";

    private boolean GO_HOME;
    private boolean startState;
    private boolean isRecord;//是否录制中
    private boolean NEED_FULL_SCREEN = false;
    private RelativeLayout title_layout;
    private View record_preview_shade;

    private ImageView local_btn;
    private final int CHOOSE_CODE = 0x000520;
    private ImageView delete_btn;
    private TextView tv_bottom;
    private boolean isChooseBack = true;
    /**
     * @param context
     * @param overGOActivityName 录制结束后需要跳转的Activity全类名
     */
    public static void goSmallVideoRecorder(Activity context, String overGOActivityName, MediaRecorderConfig mediaRecorderConfig) {
        context.startActivity(new Intent(context, MediaRecorderActivity.class).putExtra(OVER_ACTIVITY_NAME, overGOActivityName).putExtra(MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        initData();
        loadViews();
        permissionCheck();
    }

    private void initData() {
        Intent intent = getIntent();
        MediaRecorderConfig mediaRecorderConfig = intent.getParcelableExtra(MEDIA_RECORDER_CONFIG_KEY);
        if (mediaRecorderConfig == null) {
            return;
        }
        NEED_FULL_SCREEN = mediaRecorderConfig.getFullScreen();
        RECORD_TIME_MAX = mediaRecorderConfig.getRecordTimeMax();
        RECORD_TIME_MIN = mediaRecorderConfig.getRecordTimeMin();
        MediaRecorderBase.MAX_FRAME_RATE = mediaRecorderConfig.getMaxFrameRate();
        MediaRecorderBase.NEED_FULL_SCREEN = NEED_FULL_SCREEN;
        MediaRecorderBase.MIN_FRAME_RATE = mediaRecorderConfig.getMinFrameRate();
        MediaRecorderBase.SMALL_VIDEO_HEIGHT = mediaRecorderConfig.getSmallVideoHeight();
        MediaRecorderBase.SMALL_VIDEO_WIDTH = mediaRecorderConfig.getSmallVideoWidth();
        MediaRecorderBase.mVideoBitrate = mediaRecorderConfig.getVideoBitrate();
        MediaRecorderBase.CAPTURE_THUMBNAILS_TIME = mediaRecorderConfig.getCaptureThumbnailsTime();
        GO_HOME = mediaRecorderConfig.isGO_HOME();
    }

    /**
     * 加载视图
     */
    private void loadViews() {
        setContentView(R.layout.activity_video_recorder);
        // ~~~ 绑定控件
        record_preview_shade = findViewById(R.id.record_preview_shade);
        record_preview_shade.setVisibility(View.GONE);
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        title_layout = (RelativeLayout) findViewById(R.id.title_layout);
        mCameraSwitch = (CheckBox) findViewById(R.id.record_camera_switcher);
        mTitleNext = findViewById(R.id.title_next);
        mProgressView = (ProgressView) findViewById(R.id.record_progress);
//        mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
        mRecordController = (TextView) findViewById(R.id.record_controller);
        mRecordController.setBackgroundResource(R.mipmap.record_normal);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRecordLed = (CheckBox) findViewById(R.id.record_camera_led);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        // ~~~ 绑定事件
        /*if (DeviceUtils.hasICS())
            mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);*/

        mTitleNext.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
//        mRecordDelete.setOnClickListener(this);
        mRecordController.setOnTouchListener(mOnVideoControllerTouchListener);

        // ~~~ 设置数据

        // 是否支持前置摄像头
        if (MediaRecorderBase.isSupportFrontCamera()) {
            mCameraSwitch.setOnClickListener(this);
        } else {
            mCameraSwitch.setVisibility(View.GONE);
        }
        // 是否支持闪光灯
        if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
            mRecordLed.setOnClickListener(this);
        } else {
            mRecordLed.setVisibility(View.GONE);
        }


        mProgressView.setMaxDuration(RECORD_TIME_MAX);
        mProgressView.setMinTime(RECORD_TIME_MIN);
        mProgressView.setVisibility(View.INVISIBLE);
        local_btn = (ImageView) findViewById(R.id.local_btn);
        local_btn.setOnClickListener(this);
        delete_btn=(ImageView) findViewById(R.id.delete_btn);
        delete_btn.setOnClickListener(this);
    }

    /**
     * 初始化画布
     */
    private void initSurfaceView() {
        if (NEED_FULL_SCREEN) {
            mBottomLayout.setBackgroundColor(0);
            title_layout.setBackgroundColor(getResources().getColor(R.color.full_title_color));
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView
                    .getLayoutParams();
            lp.setMargins(0,0,0,0);
            mSurfaceView.setLayoutParams(lp);
            mProgressView.setBackgroundColor(getResources().getColor(R.color.full_progress_color));
        } else {
            final int w = DeviceUtils.getScreenWidth(this);
            //((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = (int) (w / (MediaRecorderBase.SMALL_VIDEO_HEIGHT / (MediaRecorderBase.SMALL_VIDEO_WIDTH * 1.0f)));
            int width = w;
            int height = (int) (w * ((MediaRecorderBase.mSupportedPreviewWidth * 1.0f) / MediaRecorderBase.SMALL_VIDEO_HEIGHT));
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView
                    .getLayoutParams();
            lp.width = width;
            lp.height = height;//width*3/4;//height;
            lp.setMargins(0,60,0,0);
            mSurfaceView.setLayoutParams(lp);
            record_preview_shade.setVisibility(View.GONE);
//            if(MediaRecorderBase.SMALL_VIDEO_OUT_16and9 > 0) {
                record_preview_shade.setVisibility(View.VISIBLE);
                int heights = CustromUtils.get4And3Height(width);
                ((RelativeLayout.LayoutParams) record_preview_shade.getLayoutParams()).topMargin
                        = heights;
//            }
            Toast.makeText(MediaRecorderActivity.this,"输入视频分辨率="+MediaRecorderBase.SMALL_VIDEO_HEIGHT+"*"+MediaRecorderBase.mSupportedPreviewWidth+
              ":输出视频分辨率"+MediaRecorderBase.SMALL_VIDEO_HEIGHT+"*"+MediaRecorderBase.SMALL_VIDEO_WIDTH,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();
        mMediaRecorder.setAppContext(this);
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        mMediaRecorder.setOnPreparedListener(this);

        File f = new File(JianXiCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                JianXiCamera.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }


    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:


                    break;

                case MotionEvent.ACTION_UP:
                    if(isRecord){
                        mMediaRecorder.setRecordState(false);
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            mTitleNext.performClick();
                        } else {

                            mMediaRecorder.setStopDate();
                            setStopUI();
                        }
                    }
                   else{
                        // 检测是否手动对焦
                        // 判断是否已经超时
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            return true;
                        }

                        // 取消回删
                        if (cancelDelete())
                            return true;
                        if (!startState) {
                            startState = true;
                            startRecord();
                        } else {
                            mMediaObject.buildMediaPart(mMediaRecorder.mCameraId);
                            mProgressView.setData(mMediaObject);
                            setStartUI();
                            mMediaRecorder.setRecordState(true);
                        }
                    }
                    isRecord = !isRecord;
                    if(isRecord){

                    }
                    else{

                    }

                    // 暂停
/*                    if (mPressedStatus) {

                        // 检测是否已经完成
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            mTitleNext.performClick();
                        }
                    }*/
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume() {
        super.onResume();

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
        }
//        mMediaRecorder.stopPreview();
//        mMediaRecorder.startPreview();
//        if(local_btn.getVisibility() == View.GONE){
//            finish();
//        }
        if(!isChooseBack) {
           finish();
            RecordVieoUtils.intoRecordVieoActivity(this,PreviewActivity.class.getName());
        }
        isChooseBack = false;

    }

    /*@Override
    public void onPause() {
        super.onPause();
        stopRecord();
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
    }*/


    /**
     * 开始录制
     */
    private void startRecord() {
        if (mMediaRecorder != null) {

            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }

            mProgressView.setData(mMediaObject);
        }

        setStartUI();
    }

    private void setStartUI() {
        mPressedStatus = true;
//		TODO 开始录制的图标
        mRecordController.setBackgroundResource(R.mipmap.record_ing);
        mRecordController.animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start();
       // mRecordController.setText("拍摄中");
        mProgressView.setVisibility(View.VISIBLE);
        mTitleNext.setVisibility(View.INVISIBLE);
        title_layout.setVisibility(View.INVISIBLE);
        tv_bottom.setVisibility(View.INVISIBLE);
        delete_btn.setVisibility(View.GONE);
        local_btn.setVisibility(View.GONE);
        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
                    RECORD_TIME_MAX - mMediaObject.getDuration());
        }
//        mRecordDelete.setVisibility(View.GONE);
        mCameraSwitch.setEnabled(false);
        mRecordLed.setEnabled(false);
    }

    private boolean onBackPressed1(){
        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
//                                    if (!VideoRecordDebugActivity.isBaoliu) {
//                                        RecordVieoUtils.deleteDir(AnyRadioApplication.gRecordVideo);
//                                    }
                                    if(mMediaObject != null) {
                                        mMediaObject.delete();
                                    }
                                    finish();
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return true;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        finish();
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(onBackPressed1()){
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode,event);
    }
    @Override
    public void onBackPressed() {
        /*if (mRecordDelete != null && mRecordDelete.isChecked()) {
            cancelDelete();
            return;
        }*/

        onBackPressed1();
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }
        setStopUI();
    }

    private void setStopUI() {
        mPressedStatus = false;
        mRecordController.setBackgroundResource(R.mipmap.record_normal);
        mRecordController.animate().scaleX(1).scaleY(1).setDuration(500).start();
       // mRecordController.setText("暂停拍摄");
//        mRecordDelete.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        title_layout.setVisibility(View.INVISIBLE);
        tv_bottom.setVisibility(View.INVISIBLE);
        mCameraSwitch.setEnabled(true);
        mRecordLed.setEnabled(true);

        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
            mHandler.removeMessages(HANDLE_STOP_RECORD);
        }

        // 处理开启回删后其他点击操作
//        if (id != R.id.record_delete) {
//            if (mMediaObject != null) {
//                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
//                if (part != null) {
//                    if (part.remove) {
//                        part.remove = false;
////                        mRecordDelete.setChecked(false);
//                        if (mProgressView != null)
//                            mProgressView.invalidate();
//                    }
//                }
//            }
//        }

        if (id == R.id.title_back) {
            onBackPressed();
        } else if (id == R.id.record_camera_switcher) {// 前后摄像头切换
            if (mRecordLed.isChecked()) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                mRecordLed.setChecked(false);
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.switchCamera();
            }

            if (mMediaRecorder.isFrontCamera()) {
                mRecordLed.setEnabled(false);
            } else {
                mRecordLed.setEnabled(true);
            }
        } else if (id == R.id.record_camera_led) {// 闪光灯
            // 开启前置摄像头以后不支持开启闪光灯
            if (mMediaRecorder != null) {
                if (mMediaRecorder.isFrontCamera()) {
                    return;
                }
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.toggleFlashMode();
            }
        } else if (id == R.id.title_next) {// 停止录制

            stopRecord();
            /*finish();
            overridePendingTransition(R.anim.push_bottom_in,
					R.anim.push_bottom_out);*/
        }
//        else if (id == R.id.record_delete) {
//            // 取消回删
//            if (mMediaObject != null) {
//                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
//                if (part != null) {
//                    if (part.remove) {
//                        part.remove = false;
//                        mMediaObject.removePart(part, true);
////                        mRecordDelete.setChecked(false);
//                    } else {
//                        part.remove = true;
////                        mRecordDelete.setChecked(true);
//                    }
//                }
//                if (mProgressView != null){
//                    mProgressView.invalidate();
//                }
//
//                // 检测按钮状态
//                checkStatus();
//                mProgressView.setVisibility(View.INVISIBLE);
//                title_layout.setVisibility(View.VISIBLE);
//                tv_bottom.setVisibility(View.VISIBLE);
//                local_btn.setVisibility(View.VISIBLE);
//                delete_btn.setVisibility(View.GONE);
//                mTitleNext.setVisibility(View.INVISIBLE);
//            }
//        }
        else if(id == R.id.local_btn){
            choose();
        }
         else if(id==R.id.delete_btn){
            onBackPressed();
        }
    }

    public void choose() {

        Intent it = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        it.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(it, CHOOSE_CODE);

    }
    /**
     * 取消回删
     */
    private boolean cancelDelete() {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
//                mRecordDelete.setChecked(false);

                if (mProgressView != null)
                    mProgressView.invalidate();

                return true;
            }
        }
        return false;
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus() {
        int duration = 0;
        if (!isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {
                    mCameraSwitch.setVisibility(View.VISIBLE);
//                    mRecordDelete.setVisibility(View.GONE);
                } else {
                    mCameraSwitch.setVisibility(View.GONE);
                }
                // 视频必须大于3秒
                if (mTitleNext.getVisibility() != View.INVISIBLE)
                    mTitleNext.setVisibility(View.INVISIBLE);
                showToast("注意：视频时间必须在7~45s以内");
            } else {
                // 下一步
                if (mTitleNext.getVisibility() != View.VISIBLE) {
                    mTitleNext.setVisibility(View.VISIBLE);
                    delete_btn.setVisibility(View.VISIBLE);
                    local_btn.setVisibility(View.GONE);
                }
            }
        }
        return 0;
    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
                        if (mMediaObject != null && mMediaObject.getMedaParts() != null && mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            mTitleNext.performClick();
                            return;
                        }
                        if (mProgressView != null)
                            mProgressView.invalidate();
                        // if (mPressedStatus)
                        // titleText.setText(String.format("%.1f",
                        // mMediaRecorder.getDuration() / 1000F));
                        if (mPressedStatus)
                            sendEmptyMessageDelayed(0, 30);
                    }
                    break;
            }
        }
    };

    @Override
    public void onEncodeStart() {
        showProgress("", "压缩中...");
    }

    @Override
    public void onEncodeProgress(int progress) {
        Log.e("onEncodeProgress",progress+"");
        showProgress("","压缩中"+progress+"%");
    }

    /**
     * 转码完成
     */
    @Override
    public void onEncodeComplete() {
        hideProgress();
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(getIntent().getStringExtra(OVER_ACTIVITY_NAME)));
            intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, mMediaObject.getOutputDirectory());
            intent.putExtra(MediaRecorderActivity.VIDEO_URI, mMediaObject.getOutputTempTranscodingVideoPath());
            intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, mMediaObject.getOutputVideoThumbPath());
            intent.putExtra("go_home", GO_HOME);
            intent.putExtra(PreviewActivity.URL,mMediaObject.getOutputTempTranscodingVideoPath());
            intent.putExtra(PreviewActivity.comeRecod,true);
            intent.putExtra(MediaRecorderActivity.VIDEO_URI,mMediaObject.getOutputTempTranscodingVideoPath());
            intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT,mMediaObject.getOutputVideoThumbPath());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("录制错误");//需要传入录制完成后跳转的Activity的全类名
        }

        finish();
    }

    /**
     * 转码失败 检查sdcard是否可用，检查分块是否存在
     */
    @Override
    public void onEncodeError() {
        hideProgress();
        Toast.makeText(this, R.string.record_video_transcoding_faild,
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {
        initSurfaceView();
    }

    public void onFinished() {
        finish();
    }

    protected ProgressDialog mProgressDialog;

    public ProgressDialog showProgress(String title, String message) {
        return showProgress(title, message, -1);
    }

    public ProgressDialog showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaRecorder.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaRecorder instanceof MediaRecorderNative) {
            ((MediaRecorderNative) mMediaRecorder).activityStop();
        }
        hideProgress();
        mProgressDialog = null;
    }


    private static final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final int PERMISSION_REQUEST_CODE = 0x001;
    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean permissionState = true;
            for (String permission : permissionManifest) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionState = false;
                }
            }
            if (!permissionState) {
                ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
            } else {
               // setSupportCameraSize();
            }
        } else {
            //setSupportCameraSize();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (Manifest.permission.CAMERA.equals(permissions[i])) {
                        //setSupportCameraSize();
                    } else if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {

                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CODE) {
            //
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.MIME_TYPE};

                Cursor cursor = getContentResolver().query(uri, proj, null,
                        null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int _data_num = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int dur_num = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int mime_type_num = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

                    String _data = cursor.getString(_data_num);
                    String dur = "";
                    if(dur_num > 0) {
                         dur = cursor.getString(dur_num);
                    }
                    if(TextUtils.isEmpty(_data)){
                        _data = UriUtils.getPath(this,uri);
                    }
//                    if(!TextUtils.isEmpty(_data)){
//                        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(_data));
//                        int duration = mp.getDuration();
//                        mp.release();
//                    }
                    String mime_type = cursor.getString(mime_type_num);
                    if (!TextUtils.isEmpty(mime_type) && mime_type.contains("video") && !TextUtils.isEmpty(_data)) {

                        BaseMediaBitrateConfig compressMode = new AutoVBRMode(30);
                        compressMode.setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST);
                        int iRate = 20;
                        float fScale=1.0f;

                        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                        final LocalMediaConfig config = buidler
                                .setVideoPath(_data)
                                .captureThumbnailsTime(1)
                                .doH264Compress(compressMode)
                                .setFramerate(iRate)
                                .setScale(fScale)
                                .build();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgress("", "压缩中...", -1);
                                    }
                                });
                                OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgress();
                                    }
                                });
                                Intent intent = new Intent(MediaRecorderActivity.this, PreviewActivity.class);
                                intent.putExtra(PreviewActivity.URL, onlyCompressOverBean.getVideoPath());
                                intent.putExtra(PreviewActivity.comeRecod,true);
                                intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, onlyCompressOverBean.getPicPath());
                                startActivity(intent);
                            }
                        }).start();
                        isChooseBack = true;
                    } else {
                        Toast.makeText(this, "选择的不是视频或者地址错误,也可能是这种方式定制神机取不到！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
