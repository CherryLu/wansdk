package com.wan.callring.utils;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;

import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.wan.callring.ui.activity.MediaRecorderActivity;

import java.io.File;
import java.io.FileInputStream;

/**拍摄小视频工具类
 * Created by lenovo on 2018/6/6.
 */

public class RecordVieoUtils {



    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/recordvideo";

    public static String gRecordVideo = path  ;


    /**
     * 初始化拍摄工具
     */
    public static void initRecordVideo() {


        // 设置拍摄视频缓存路径
//        File dcim = Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        if (DeviceUtils.isZte()) {
//            if (dcim.exists()) {
//                JianXiCamera.setVideoCachePath(dcim + "/mabeijianxi/");
//            } else {
//                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
//                        "/sdcard-ext/")
//                        + "/mabeijianxi/");
//            }
//        } else {
//            JianXiCamera.setVideoCachePath(dcim + "/mabeijianxi/");
//        }
//        // 初始化拍摄
//        JianXiCamera.initialize(false, null);
        JianXiCamera.setVideoCachePath(gRecordVideo+"/");
        JianXiCamera.initialize(false, null);
    }

    /**
     * 初始化录制配置信息并跳转到录制页面
     * @param activity
     * @param overGOActivityName
     */
    public static void intoRecordVieoActivity(Activity activity,String overGOActivityName){
        if(activity == null){
            return;
        }
        boolean needFull = true;//是否全屏
        int width = 1280;//视频尺寸宽720
        int height = 720;//视频尺寸高960.480
        int maxFramerate = 30;//20;//视频帧率
        int bitrate = 580000;//视频码率<=0表示动态码率580000
        int maxTime = 45*1000;//录制最大时长 毫秒
        int minTime = 7000;//录制最小时长 毫秒

//        maxFramerate = VideoRecordDebugActivity.FRAMERATE;
//        bitrate = VideoRecordDebugActivity.BITRATE;
//        maxTime = VideoRecordDebugActivity.MAX_TIME;
//        minTime = VideoRecordDebugActivity.MIN_TIME;
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .fullScreen(needFull)
                .smallVideoWidth(needFull?0:width)
                .smallVideoHeight(height)
                .recordTimeMax(maxTime)
                .recordTimeMin(minTime)
                .maxFrameRate(maxFramerate)
                .videoBitrate(bitrate)
                .captureThumbnailsTime(1)
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(activity, overGOActivityName, config);
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        boolean isexists = dir.exists();
        boolean isDirectory = dir.isDirectory();
        if (dir == null || !isexists || !isDirectory)
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static String getVideoDir(String videoPath){
        String dir = "";
        if(!TextUtils.isEmpty(videoPath)){
            dir = videoPath.substring(0,videoPath.lastIndexOf("/"));
//            dir.replace(lastStr,"");
        }
        return dir;
    }
    public static int getVideoFileaSize(String path){
        if(!TextUtils.isEmpty(path)){
            try {
                File dF = new File(path);
                FileInputStream fis = new FileInputStream(dF);
                int fileLen = fis.available();
                return fileLen;
            } catch (Exception e) {

            }
        }
        return 0;
    }
}
