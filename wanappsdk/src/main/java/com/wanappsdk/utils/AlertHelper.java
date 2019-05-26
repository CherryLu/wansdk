package com.wanappsdk.utils;

import android.content.Context;
import android.graphics.Color;

import com.wanappsdk.R;
import com.wanappsdk.ui.view.LoadingDialog;
import com.wanappsdk.ui.view.TipsDailog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 954703125 on 2017/4/18.
 */

public class AlertHelper {
    private SweetAlertDialog pDialog;
    private Context context;
    private LoadingDialog loadingDialog;

    private SweetAlertDialog waitDialog;

    private TipsDailog tipsDailog;

    public AlertHelper(Context context){
        this.context = context;
        waitDialog = new SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE);
        waitDialog.setTitleText("加载中...");
        waitDialog.setCancelable(false);
        waitDialog.setCanceledOnTouchOutside(false);
        tipsDailog = new TipsDailog(context);

        loadingDialog = new LoadingDialog(this.context, LoadingDialog.CUSTOM_IMAGE_TYPE);
    }


    /**
     * 显示tips
     */
    public void showTips(IAlertListener iAlertListener){
        if (tipsDailog!=null){
            tipsDailog.setiAlertListener(iAlertListener);
            tipsDailog.show();
        }
    }

    /**
     * 取消tips
     */
    public void cancleTips(){
        if (tipsDailog!=null&&tipsDailog.isShowing()){
            tipsDailog.dismiss();
        }
    }



    public void showWaiting(){
        loadingDialog.setTitleText("加载中...");
        loadingDialog.setCustomImage(R.drawable.loading);
        loadingDialog.setButtonGone();
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        /*if (waitDialog!=null){
            waitDialog.show();
        }*/
    }


    public void cancleWaiting(){
        loadingDialog.dismiss();
        /*if (waitDialog!=null&&waitDialog.isShowing()){
            waitDialog.dismiss();
        }*/
    }

    public void showLoading(){
        loadingDialog = new LoadingDialog(this.context, LoadingDialog.CUSTOM_IMAGE_TYPE);
        loadingDialog.setTitleText("加载中...");
        loadingDialog.setCustomImage(R.drawable.loading);
        loadingDialog.setButtonGone();
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    public boolean isShowing(){
        if(loadingDialog!=null){
            return loadingDialog.isShowing();
        }
        return false;
    }
    public void dissmiss(){
        if(loadingDialog!=null){
            loadingDialog.dismiss();
        }
    }

    /**
     * 显示签到成功
     */
    public void showSuccess(){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText("签到成功")
                .setConfirmText("确定")
                .show();

    }


    public void showUninstall(final SweetAlertDialog.OnSweetClickListener listener){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText("您已经安装此APP,是否卸载?")
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        listener.onClick(sweetAlertDialog);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }


    /**
     * 重新下载提示框
     * @param listener
     */
    public void showreDownLoad(final SweetAlertDialog.OnSweetClickListener listener){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText("您尚未安装该APP,是否下载?")
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        listener.onClick(sweetAlertDialog);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 显示上传成功
     */
    public void showWaitDialog(){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText("上传成功")
                .setConfirmText("确定")
                .show();


    }



    public void showUploadSuccess(){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText("上传成功")
                .setConfirmText("确定")
                .show();
    }

    /**
     * 显示提示
     */
    public void showSTips(String msg){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText(msg)
                .setConfirmText("确定")
                .show();

    }


    /**
     * 错误提示
     */
    public void showError(String msg){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText(msg)
                .setConfirmText("确定")
                .show();

    }


    /**
     * 任务数量超限
     * @param iAlertListener
     */
    public static void showLimitOut(Context context,final IAlertListener iAlertListener ){
        final SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText("任务已经被接完")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        mDialog.dismiss();
                        iAlertListener.sure();
                    }
                })
                .show();
    }

    public static void showFinish(Context context,String msg,SweetAlertDialog.OnSweetClickListener listener ){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("提示")
                .setContentText(msg)
                .setConfirmText("确定")
                .setConfirmClickListener(listener)
                .show();
    }




    public void alert(String title, String msg, String sureMsg, final IAlertListener iAlertListener){
        SweetAlertDialog mDialog =new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText(title)
                .setContentText(msg)
                .setConfirmText(sureMsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        if(iAlertListener!=null){
                            iAlertListener.sure();
                        }
                    }
                })
                .show();
    }

    public static  void confirm(String msg, Context context, final IAlertListener iAlertListener ){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("系统消息")
                .setContentText(msg)
                .setCancelText("取消")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        if(iAlertListener!=null){
                            iAlertListener.sure();
                        }
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                        if(iAlertListener!=null){
                            iAlertListener.cancel();
                        }
                    }
                })
                .show();
    }


    public static  void confirm(String title, String msg, String cancel, String sureMsg, Context context, final IAlertListener iAlertListener ){
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .setCancelText(cancel)
                .setConfirmText(sureMsg)
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        if(iAlertListener!=null){
                            iAlertListener.sure();
                        }
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                if(iAlertListener!=null){
                    iAlertListener.cancel();
                }
            }
        }).show();
    }


    public void initProgress(){
        initProgress("加载中...");
    }

    public void initProgress(String title){
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void progress(float progress){
        pDialog.setTitleText("下载进度："+ String.format("%.2f",progress*100)+"%");
    }

    public void progress(String progressMsg){
        pDialog.setTitleText(progressMsg);
    }

    public void changeProgressError(String title, String msg, String surMsg, final IAlertListener iAlertListener ){
        pDialog.setCancelable(false);
        pDialog.setTitleText(title)
                .setContentText(msg)
                .setConfirmText(surMsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        if(iAlertListener!=null){
                            iAlertListener.sure();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    public void changeProgressErrorCancelable(String title, String msg, String surMsg, final IAlertListener iAlertListener ){
        pDialog.setCancelable(true);
        pDialog.setTitleText(title)
                .setContentText(msg)
                .setConfirmText(surMsg)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        if(iAlertListener!=null){
                            iAlertListener.sure();
                        }
                    }
                })
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }


    public void destoryProgress(){
        pDialog.dismiss();
    }

    public interface IAlertListener{
        public void sure();
        public void cancel();
    }

}
