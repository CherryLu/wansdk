package com.wanappsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wanappsdk.R;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.ui.activity.NoTimeWebActivity;
import com.wanappsdk.ui.activity.NoTimeWebFirstUnUseActivity;
import com.wanappsdk.ui.activity.TaskDetailActivity;
import com.wanappsdk.ui.activity.TaskDownActivity;
import com.wanappsdk.ui.activity.TaskDownNoPicActivity;
import com.wanappsdk.ui.activity.TaskTimeActivity;
import com.wanappsdk.ui.activity.TaskUploadActivity;
import com.wanappsdk.ui.activity.WebActivity;
import com.wanappsdk.ui.activity.WebSlideActivity;


public class Nagivator {



    public static void finishActivity(Activity activity){
        if (activity != null) {
            activity.finish();
            activity.overridePendingTransition(R.anim.no_anim, R.anim.push_right_out);
        }
    }

    //跳转上传图片
    public static void startUpLoadActivity(Activity context, TaskData stepTask,TaskData mainTask,int type){

        Intent intent = new Intent(context, TaskUploadActivity.class);
        intent.putExtra("TASK",stepTask);
        intent.putExtra("MAIN",mainTask);
        intent.putExtra("TYPE",type);
        context.startActivityForResult(intent,100);
    }


    /**
     * 跳转下载页面
     * @param context
     * @param stepTask
     * @param mainTask
     */
    public static void startDownloadActivity(Activity context, TaskData stepTask,TaskData mainTask){

        Intent intent = new Intent(context, TaskDownActivity.class);
        intent.putExtra("TASK",stepTask);
        intent.putExtra("MAIN",mainTask);
        context.startActivityForResult(intent,100);
    }

    /**
     * 跳转无截图下载页面
     * @param context
     * @param stepTask
     * @param mainTask
     */
    public static void startDownloadNoPicActivity(Activity context, TaskData stepTask,TaskData mainTask){
        Intent intent = new Intent(context, TaskDownNoPicActivity.class);
        intent.putExtra("TASK",stepTask);
        intent.putExtra("MAIN",mainTask);
        context.startActivityForResult(intent,100);
    }


    /**
     * 跳转留存页面
     * @param context
     * @param stepTask
     * @param mainTask
     */
    public static void startStayActivity(Activity context, TaskData stepTask,TaskData mainTask,int type){

        Intent intent = new Intent(context, TaskTimeActivity.class);
        intent.putExtra("TASK",stepTask);
        intent.putExtra("MAIN",mainTask);
        intent.putExtra("TYPE",type);
        context.startActivityForResult(intent,100);
    }


    /**
     * 跳转留存页面
     * @param stepTask
     * @param mainTask
     */
    public static void startStayActivity(Fragment fragment, TaskData stepTask,TaskData mainTask){

        Intent intent = new Intent(fragment.getContext(), TaskTimeActivity.class);
        intent.putExtra("TASK",stepTask);
        intent.putExtra("MAIN",mainTask);
        intent.putExtra("TYPE",0);
        fragment.startActivityForResult(intent,100);
    }

    /**
     * 跳转留存页面
     * @param stepTask
     * @param mainTask
     */
    public static void startStayActivity(Context context, TaskData stepTask,TaskData mainTask){

        Intent intent = new Intent(context, TaskTimeActivity.class);
        intent.putExtra("TASK",stepTask);
        intent.putExtra("MAIN",mainTask);
        intent.putExtra("TYPE",0);
        context.startActivity(intent);
    }


    /**
     * 跳转
     * @param taskData
     */
    public static void startTaskDetailActivity(Fragment fragment, TaskData taskData){
        Intent intent = new Intent(fragment.getContext(), TaskDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TASK",taskData);
        intent.putExtra("BID",bundle);
        fragment.startActivityForResult(intent,100);
    }


    public static void startTaskDetailActivity(Context context, TaskData taskData){
        Intent intent = new Intent(context, TaskDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TASK",taskData);
        intent.putExtra("BID",bundle);
        context.startActivity(intent);
    }


    /**
     * 跳转H5页面
     * @param context
     */
    public static void startWebActivity(Context context,TaskData data){
        Intent intent = null;

        switch (data.getUrlFormat()){
            case "0":
                intent = new Intent(context, WebSlideActivity.class);
                break;
            case "1":
                intent = new Intent(context, NoTimeWebActivity.class);
                break;
            case "2":
                intent = new Intent(context, NoTimeWebFirstUnUseActivity.class);
                break;
            default:
                intent = new Intent(context, WebActivity.class);
                break;

        }

        if (intent!=null){
            intent.putExtra("TASK",data);
            context.startActivity(intent);
        }

    }

    /**
     * 跳转任务详情页
     * @param context
     *//*
    public static void startTaskDetailActivity(Context context, TaskData taskData){
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra("TASK",taskData);
        context.startActivity(intent);
    }


    *//**
     * 跳转分享任务详情页
     * @param context
     *//*
    public static void startTaskDetailShareActivity(Context context){
        Intent intent = new Intent(context, TaskDetailsShareActivity.class);
        context.startActivity(intent);
    }


    *//**
     * 跳转主页
     * @param context
     *//*
    public static void startMainActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    *//**
     * 跳转引导页
     * @param context
     *//*
    public static void startGuideActivity(Context context){
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    *//**
     * 每日任务
     * @param context
     *//*
    public static void startRewardActivity(Context context){
        Intent intent = new Intent(context, RewardActivity.class);
        context.startActivity(intent);
    }

    *//**
     * 每日任务
     * @param context
     *//*
    public static void startEveryRewardActivity(Context context){
        Intent intent = new Intent(context, RewardActivity.class);
        intent.putExtra("TYPE",1);
        context.startActivity(intent);
    }


    *//**
     * 微商任务
     * @param context
     *//*
    public static void startWeSaleActivityActivity(Context context){
        Intent intent = new Intent(context, WeSaleActivity.class);
        intent.putExtra("TYPE",1);
        context.startActivity(intent);
    }


    *//**
     * 收益排行
     * @param context
     *//*
    public static void startRewardZhoujihua(Context context){
        Intent intent = new Intent(context, PersonSortActivity.class);
        context.startActivity(intent);
    }


    *//**
     * 跳转下载任务详情
     * @param context
     *//*
    public static void startTaskDownloadActivity(Context context){
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        context.startActivity(intent);
    }


    *//**
     * 跳转分享任务详情
     * @param context
     *//*
    public static void startH5TaskShareActivity(Context context){
        Intent intent = new Intent(context, H5ShareActivity.class);
        context.startActivity(intent);
    }

    *//**
     * 跳转分享任务详情
     * @param context
     *//*
    public static void startTaskShareActivity(Context context){
        Intent intent = new Intent(context, H5ShareActivity.class);
        context.startActivity(intent);
    }

    *//**
     * 跳转签到页面
     * @param context
     *//*
    public static void startSignActivity(Context context){
        Intent intent = new Intent(context, SignActivity.class);
        context.startActivity(intent);
    }

    *//**
     * 跳转周计划
     * @param context
     *//*
    public static void startWeeksPlan(Context context){
        Intent intent = new Intent(context, WeeksPlanActivity.class);
        context.startActivity(intent);

    }

    *//**
     * 跳转上传页面
     * @param activity
     * @param taskData
     *//*
    public static void startUploadActivity(Activity activity, TaskData taskData){
        Intent intent = new Intent(activity, UploadActivity.class);
        intent.putExtra("FT",taskData);
        activity.startActivityForResult(intent,100);
    }


    *//**
     * 跳转上传页面
     * @param taskData
     *//*
    public static void startUploadActivity(Context context, TaskData taskData){
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra("FT",taskData);
        context.startActivity(intent);
    }

    *//**
     *
     * @param context
     *//*
    public static void startInvitationActivity(Context context){

        Intent intent = new Intent(context, InvitationActivity.class);
        context.startActivity(intent);
    }


    *//**
     * 跳转分类页面
     * @param context
     *//*
    public static void startSortActivity(Context context){
        Intent intent = new Intent(context, SortActivity.class);
        context.startActivity(intent);
    }


    *//**
     * 跳转分类页面
     * @param context
     *//*
    public static void startSortActivity(Context context, int position){
        Intent intent = new Intent(context, SortActivity.class);
        intent.putExtra("POS",position);
        context.startActivity(intent);
    }


    *//**
     * 任务点击类型
     * @param context
     * @param taskData
     *//*
    public static void startTaskOClick(Context context, TaskData taskData){
        AlertHelper helper = new AlertHelper(context);
        if (context==null||taskData==null){
            helper.showError("数据异常，无法解析");
            return;
        }


        if (taskData.getAction().equals(1)){//原生
            Intent intent = new Intent();
            intent.setData(Uri.parse("www.baidu.com"));//Url 就是你要打开的网址
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent); //启动浏览器
            return;
        }
      String[] strs =  taskData.getParam().split("=");
        if (strs.length>1){
            String page = strs[1];
            switch (page){
                case "100"://详情页面
                    Nagivator.startTaskDetailActivity(context,taskData);
                    break;
                case "101"://上传页面
                    Nagivator.startUploadActivity(context,taskData);
                    break;
                case "102"://先检测是否安装，然后下载
                    String packetName ="";
                    if ("0".equals(taskData.getJobStr().getMarket())){
                        packetName = taskData.getJobStr().getMarketApk();
                    }else {
                        packetName = taskData.getJobStr().getAppApk();
                    }
                    if (TextUtils.isEmpty(packetName)){
                        Toast.makeText(context,"包名为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (SystemUtil.hasInstanceApp(context,taskData.getJobStr().getAppApk())){//已安装
                        SystemUtil.openOtherAPP(context,taskData.getJobStr().getAppApk());
                    }else {
                        DownloadDialog downloadDialog = new DownloadDialog(context,taskData);
                        downloadDialog.show();
                    }
                    break;
                case "103"://打开APP
                    SystemUtil.openOtherAPP(context,taskData.getJobStr().getAppApk());
                    break;
                case "104"://分享
                    ShareDialog dialog = new ShareDialog(context,page);
                    dialog.setTaskData(taskData);
                    dialog.show();
                    break;

                case "105"://微信购买页面
                    Nagivator.startH5TaskShareActivity(context);

                    break;
                default:
                    helper.showError("数据异常，无法解析");
                    break;
            }
        }else {
            helper.showError("数据异常，无法解析");

        }

    }*/




}
