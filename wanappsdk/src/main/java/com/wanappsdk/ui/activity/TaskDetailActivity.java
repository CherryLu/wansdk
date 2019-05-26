package com.wanappsdk.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wanappsdk.R;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.BaseTaskAvailable;
import com.wanappsdk.baen.BaseTaskList;
import com.wanappsdk.baen.CompleteMsg;
import com.wanappsdk.baen.TaskAvailable;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.baen.TranslateBean;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.http.ProgressListener;
import com.wanappsdk.http.ProgressResponseBody;
import com.wanappsdk.http.RetrofitService;
import com.wanappsdk.ui.view.UpLoadCallBack;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.ImageUtil;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.QcCallBack;
import com.wanappsdk.utils.ScreenUtil;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class TaskDetailActivity extends TakePhotoActivity implements View.OnClickListener,UpLoadCallBack {


    ImageView backImage;
    TextView mainTitle;
    TextView rightTxt;
    ImageView rightImage;
    ImageView appPic;
    TextView appName;
    TextView appSize;
    TextView appLable1;
    TextView appLable2;
    LinearLayout stepsLayouy;
    LinearLayout imageLayout;
    Button download;
    TextView getmoney;
    LinearLayout download_area;
    TextView progress_txt;
    ProgressBar progressBar;
    TextView re_select_pic;

    TextView downloadtitle;
    ImageView qc_images;
    LinearLayout view_all;


    private TaskData taskData;

    private List<TaskData> mDatas;

    private boolean isDownloadComplete;


    private boolean isTimer;

    private long  leftTime;

    private TaskData currentTask;



    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 200:
                    float progress  = (float) msg.obj;

                    if (progressBar!=null){
                        progressBar.setProgress((int) (progress*100));
                    }

                    if (progress_txt!=null){
                        progress_txt.setText((int) (progress*100)+"");
                    }

                    if (progress==1){//下载完成
                        downloadtitle.setVisibility(View.GONE);
                        //downloadtitle.setVisibility(View.VISIBLE);
                        //download_area.setVisibility(View.GONE);
                        downloadtitle.setText("下载完成");//跳转安装页面
                        //download_install.setVisibility(View.VISIBLE);
                        isDownloadComplete = true;
                        String filePath = (String) msg.getData().getString("FILE");
                        File file = new File(filePath);
                        ApkUtils.installApk(TaskDetailActivity.this,file);
                        //getDownLoadCompletion();
                    }

                    break;
                case 201:
                    if (downloadtitle!=null){
                        downloadtitle.setVisibility(View.GONE);
                        //downloadtitle.setVisibility(View.VISIBLE);
                        //download_area.setVisibility(View.GONE);
                        downloadtitle.setText("下载错误");
                    }

                    break;

            }
            super.handleMessage(msg);
        }
    };

    private AlertHelper alertHelper;
    private String bid;
    private TaskData fromtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_download);

        alertHelper = new AlertHelper(this);

        fromtask = (TaskData) getIntent().getBundleExtra("BID").getSerializable("TASK");
        bid = fromtask.getTid();
        initView();

        getData();




    }

    List<String> pics;

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        if (pics==null){
            pics = new ArrayList<>();
        }else {
             pics.clear();
        }
        ArrayList<TImage> tImages =  result.getImages();
        for (int i =0;i<tImages.size();i++){
            pics.add(tImages.get(i).getOriginalPath());
        }
        initLayout(pics);
    }



    private void getData(){
        alertHelper.showWaiting();
        ApiServiceManager.getTaskDetails(bid, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                view_all.setVisibility(View.VISIBLE);
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskList baseTaskList = gson.fromJson(json, BaseTaskList.class);
                    mDatas = new ArrayList<>();
                    mDatas.addAll(baseTaskList.getTaskDatas());
                    setData();
                    alertHelper.cancleWaiting();
                    if (mDatas.size()==0){
                        Toast.makeText(TaskDetailActivity.this,"您已经完成"+fromtask.getJobName()+"任务",Toast.LENGTH_SHORT).show();
                        Nagivator.finishActivity(TaskDetailActivity.this);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    alertHelper.cancleWaiting();

                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(TaskDetailActivity.this,"数据获取失败",Toast.LENGTH_SHORT).show();
                alertHelper.cancleWaiting();
            }

            @Override
            public void onDecode(BaseString baseString,String json) {
                view_all.setVisibility(View.VISIBLE);
                Gson gson = new Gson();

                List<TaskData> dataList = new ArrayList<>();
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(json).getAsJsonArray();

                for (JsonElement taskData:jsonArray){
                    TaskData data =  gson.fromJson(taskData,TaskData.class);
                    dataList.add(data);
                }

                mDatas = new ArrayList<>();
                mDatas.addAll(dataList);

                setData();
                alertHelper.cancleWaiting();
                if (mDatas.size()==0){
                    Toast.makeText(TaskDetailActivity.this,"您已经完成"+fromtask.getJobName()+"任务",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(TaskDetailActivity.this);
                }
            }
        });

    }


    private boolean hasInstanllTask;

    private void setData(){


        hasInstanllTask = false;
        GlideUtil.loadImageView(this,fromtask.getJobLogo(),appPic);
        appName.setText(fromtask.getJobName());
        mainTitle.setText(fromtask.getJobName());
        if (fromtask.getTags()!=null){
            String[] strings = fromtask.getTags().split(";");
            if (strings.length>0){
                appLable1.setVisibility(View.VISIBLE);
                appLable1.setText(strings[0]);
            }else {
                appLable1.setVisibility(View.GONE);
            }
            if (strings.length>1){
                appLable2.setVisibility(View.VISIBLE);
                appLable2.setText(strings[1]);
            }else {
                appLable2.setVisibility(View.GONE);
            }
        }

        appSize.setText(fromtask.getJobSubtitle());




        stepsLayouy.removeAllViews();

        long totalPoint = 0;
        for (int i = 0;i<mDatas.size();i++){
            if ("1".equals(mDatas.get(i).getStepType())){
                hasInstanllTask = true;
                break;
            }
        }

        for (int i = 0;i<mDatas.size();i++){
            totalPoint = totalPoint + mDatas.get(i).getStepPoints();
            if (i == mDatas.size()-1){
                stepsLayouy.addView(getStepView(mDatas.get(i),true));
            }else {
                stepsLayouy.addView(getStepView(mDatas.get(i),false));
            }

        }

        getmoney.setText(ApkUtils.getMuchMoney(totalPoint));
    }


    private View getStepView(final TaskData taskData,boolean isLast) {

        if (taskData == null) {
            return null;
        }
        View view = View.inflate(this, R.layout.step_item, null);
        View line = view.findViewById(R.id.lines);
        TextView step_descrip = view.findViewById(R.id.step_descrip);
        TextView step_descripe = view.findViewById(R.id.step_descripe);
        TextView step_money = view.findViewById(R.id.step_money);
        TextView limitCount = view.findViewById(R.id.limitCount);
        TextView app_lable1 = view.findViewById(R.id.app_lable1);
        TextView app_lable2 = view.findViewById(R.id.app_lable2);

        TextView step_number = view.findViewById(R.id.step_number);

        if (isLast){
            line.setVisibility(View.GONE);
        }
        step_descrip.setText(taskData.getStepName());
        step_descripe.setText(taskData.getStepTitle());
        step_money.setText(ApkUtils.getMuchMoney(taskData.getStepPoints()));
        limitCount.setText("剩余"+(int)(Float.parseFloat(taskData.getAvailable()))+"份");
        app_lable1.setVisibility(View.GONE);
        if (TextUtils.isEmpty(taskData.getKeyWord())){
            app_lable1.setVisibility(View.GONE);
        }else {
            app_lable1.setText(taskData.getKeyWord());
        }
        app_lable2.setVisibility(View.VISIBLE);
        if (taskData.getChecktype()==0||taskData.getChecktype()==2){
            app_lable2.setText("免审");
            app_lable2.setBackgroundResource(R.drawable.light_blue_bac);
        }else if (taskData.getChecktype()==1){
            app_lable2.setText("审核");
            app_lable2.setBackgroundResource(R.drawable.grey_bac_step);
        }

        if (hasInstanllTask&&!(taskData.getStepType().equals("1"))){
            step_number.setBackgroundResource(R.drawable.round_gry);
        }else {
            step_number.setBackgroundResource(R.drawable.round_ori);
        }

        //taskData.setCredit_card("0");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAvailable(taskData);

            }
        });

        return view;
    }


    private void getAvailable(final TaskData taskData){
        //增加是否安装判断

        if (!taskData.getStepType().equals("1")&&!fromtask.getJobMarket().equals("跳转")&&!fromtask.getJobMarket().equals("二维码")){
                if (hasInstanllTask){
                    Toast.makeText(this,"请先完成安装任务",Toast.LENGTH_SHORT).show();
                    return;
                }
        }

        alertHelper.showWaiting();

        ApiServiceManager.getTaskAvailable(taskData.getId(), taskData.getTid(), new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                alertHelper.cancleWaiting();
                try {
                    String json = new String(body.bytes());
                    final Gson gson = new Gson();
                    final TaskAvailable baseTaskList = gson.fromJson(json, TaskAvailable.class);
                    if (baseTaskList==null){
                        finish();
                        return;
                    }
                    switch (baseTaskList.getStatus().getCode()){
                        case -2://有其他任务
                            alertHelper.showTips(new AlertHelper.IAlertListener() {
                                @Override
                                public void sure() {
                                    //TODO  需要完整数据

                                    String json = baseTaskList.getStatus().getMsg();
                                    json = json.replace("\\","");
                                    JsonParser parser = new JsonParser();
                                    JsonArray array = parser.parse(json).getAsJsonArray();
                                    Gson gson1 = new Gson();
                                    JsonElement element = array.get(0);
                                    TranslateBean bean = gson1.fromJson(element,TranslateBean.class);

                                    if (bean!=null){
                                        TaskData stepTask = new TaskData();
                                        stepTask.setChecktype(bean.getcCheck_type());
                                        stepTask.setAvailable(bean.getAvailable());
                                        stepTask.setId(bean.getId());
                                        stepTask.setMinstaySec(bean.getcMinstay_sec());
                                        stepTask.setSimpleUrl(bean.getcExample_img_url());
                                        stepTask.setStepType(bean.getcStep_type());
                                        stepTask.setStepPoints(bean.getcStep_points());
                                        stepTask.setStepTitle(bean.getcStep_title());
                                        stepTask.setKeyWord(bean.getKeyWord());
                                        stepTask.setStepNotice(bean.getcStep_notice());
                                        stepTask.setStepGuide(bean.getcStep_guide());


                                        TaskData mainstepId = new TaskData();
                                        mainstepId.setApkName(bean.getbApk_name());
                                        mainstepId.setAppName(bean.getbJob_name());
                                        mainstepId.setTid(bean.getJobId());
                                        mainstepId.setJobLogo(bean.getbJob_logo());
                                        mainstepId.setTags(bean.getbTags());
                                        mainstepId.setJobMarket(bean.getbJob_market());
                                        //startTask(taskData,fromtask);
                                        startTask(stepTask,mainstepId);
                                        //startJob(stepTask,mainstepId);

                                    }

                                }

                                @Override
                                public void cancel() {
                                    startJob(taskData,fromtask);
                                }
                            });
                            break;
                        case -1://任务已经抢光了

                            AlertHelper.showLimitOut(TaskDetailActivity.this, new AlertHelper.IAlertListener() {
                                @Override
                                public void sure() {
                                    finish();
                                }

                                @Override
                                public void cancel() {

                                }
                            });


                            break;
                        case 1://已领取
                        case 2://领取成功
                            //开始任务
                            startTask(taskData,fromtask);
                            //startJob(taskData,fromtask);

                            break;

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable e) {
                alertHelper.cancleWaiting();
                LogUtils.e("ZX",e.getMessage());
            }

            @Override
            public void onDecode(BaseString baseString,String json) {
                alertHelper.cancleWaiting();
                final Gson gson = new Gson();
                final CompleteMsg baseTaskList = gson.fromJson(json, CompleteMsg.class);
                if (baseTaskList==null){
                    finish();
                    return;
                }

                switch (baseTaskList.getCode()){
                    case -2://有其他任务
                        alertHelper.showTips(new AlertHelper.IAlertListener() {
                            @Override
                            public void sure() {
                                //TODO  需要完整数据

                                String json = baseTaskList.getMsg();
                                json = json.replace("\\","");
                                JsonParser parser = new JsonParser();
                                JsonArray array = parser.parse(json).getAsJsonArray();
                                Gson gson1 = new Gson();
                                JsonElement element = array.get(0);
                                TranslateBean bean = gson1.fromJson(element,TranslateBean.class);

                                if (bean!=null){
                                    TaskData stepTask = new TaskData();
                                    stepTask.setChecktype(bean.getcCheck_type());
                                    stepTask.setAvailable(bean.getAvailable());
                                    stepTask.setId(bean.getId());
                                    stepTask.setMinstaySec(bean.getcMinstay_sec());
                                    stepTask.setSimpleUrl(bean.getcExample_img_url());
                                    stepTask.setStepType(bean.getcStep_type());
                                    stepTask.setStepPoints(bean.getcStep_points());
                                    stepTask.setStepTitle(bean.getcStep_title());
                                    stepTask.setKeyWord(bean.getKeyWord());
                                    stepTask.setStepNotice(bean.getcStep_notice());
                                    stepTask.setStepGuide(bean.getcStep_guide());


                                    TaskData mainstepId = new TaskData();
                                    mainstepId.setApkName(bean.getbApk_name());
                                    mainstepId.setAppName(bean.getbJob_name());
                                    mainstepId.setTid(bean.getJobId());
                                    mainstepId.setJobLogo(bean.getbJob_logo());
                                    mainstepId.setTags(bean.getbTags());
                                    mainstepId.setJobMarket(bean.getbJob_market());
                                    //startTask(taskData,fromtask);
                                    startTask(stepTask,mainstepId);
                                    //startJob(stepTask,mainstepId);

                                }

                            }

                            @Override
                            public void cancel() {
                                startJob(taskData,fromtask);
                            }
                        });
                        break;
                    case -1://任务已经抢光了

                        AlertHelper.showLimitOut(TaskDetailActivity.this, new AlertHelper.IAlertListener() {
                            @Override
                            public void sure() {
                                getData();
                            }

                            @Override
                            public void cancel() {

                            }
                        });

                        break;
                    case 1://已领取
                    case 2://领取成功
                        //开始任务
                        startTask(taskData,fromtask);
                        //startJob(taskData,fromtask);

                        break;

                }

            }
        });
    }



    private void startJob(final TaskData taskData, final TaskData fromtask){
        alertHelper.showWaiting();
        ApiServiceManager.getTaskStart(taskData.getId(), taskData.getId(), new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                alertHelper.cancleWaiting();
                try {
                    String str = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskAvailable baseTaskList = gson.fromJson(str, BaseTaskAvailable.class);
                    if (baseTaskList==null){
                        return;
                    }
                    //TODO  测试用 记得删掉
                    //baseTaskList.setCode(100);
                    if (baseTaskList.getCode()==109){//成功
                        startTask(taskData,fromtask);
                    }else {
                        AlertHelper.showFinish(TaskDetailActivity.this,baseTaskList.getMsg(), new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                alertHelper.cancleWaiting();
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                alertHelper.cancleWaiting();

                //TODO  测试用 记得删掉
                //baseTaskList.setCode(100);
                if (baseString.getCode()==109){//成功
                    startTask(taskData,fromtask);
                }else {
                    AlertHelper.showFinish(TaskDetailActivity.this,baseString.getMsg(), new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            finish();
                        }
                    });
                }

            }
        });
    }


    private void startTask(TaskData taskData, final TaskData fromtask){
        if (taskData==null){
            return;
        }
        currentTask = taskData;

        switch (taskData.getStepType()){
            case "1"://下载

                qc_images.setVisibility(View.GONE);
                if (!fromtask.getJobMarket().equals("二维码")){
                    if (ApkUtils.hasInstall(TaskDetailActivity.this,fromtask.getApkName())){
                        Uri uri = Uri.parse("package:" + fromtask.getApkName());
                        Intent intent=new Intent(Intent.ACTION_DELETE,uri);
                        startActivity(intent);

                        return;
                      /* int i = (int) SharedPreferencesUtils.getParam(TaskDetailActivity.this,fromtask.getApkName(),0);
                       String mkt = (String) SharedPreferencesUtils.getParam(TaskDetailActivity.this,fromtask.getApkName()+"market","0");
                       String id = (String) SharedPreferencesUtils.getParam(TaskDetailActivity.this,fromtask.getApkName()+"id","0");
                        if (i==0||!(mkt.equals(fromtask.getJobMarket()))||!id.equals(taskData.getTid())){   //没有记录 不是在我们平台安装
                            alertHelper.showUninstall(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Uri uri = Uri.parse("package:" + fromtask.getApkName());
                                    Intent intent=new Intent(Intent.ACTION_DELETE,uri);
                                    startActivity(intent);
                                }
                            });
                            return;
                        }*/
                    }/*else {
                        SharedPreferencesUtils.setParam(TaskDetailActivity.this,fromtask.getApkName(),1);
                        SharedPreferencesUtils.setParam(TaskDetailActivity.this,fromtask.getApkName()+"market",fromtask.getJobMarket());
                        SharedPreferencesUtils.setParam(TaskDetailActivity.this,fromtask.getApkName()+"id",taskData.getTid());

                    }*/
                }else if (fromtask.getJobMarket().equals("二维码")){
                    Toast.makeText(TaskDetailActivity.this,"请长按二维码图片识别",Toast.LENGTH_SHORT).show();
                    GlideUtil.loadBitMapImageView(TaskDetailActivity.this,fromtask.getDownUrl(),qc_images);
                    qc_images.setVisibility(View.VISIBLE);
                    return;
                }
                if (taskData.getChecktype()==0){
                    Nagivator.startDownloadNoPicActivity(this,currentTask,fromtask);
                }else {
                    Nagivator.startDownloadActivity(this,currentTask,fromtask);
                }

                //downlaodTask(taskData);
                break;
            case "2"://评价
                if ("跳转".equals(fromtask.getJobMarket())||"二维码".equals(fromtask.getJobMarket())){
                  //  if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                        Nagivator.startUpLoadActivity(this,currentTask,fromtask,2);
                 //   }else {
                 //       reDownload();
                 //   }
                }else {
                    if (hasInstanllTask){
                        Toast.makeText(TaskDetailActivity.this,"请先完成安装任务",Toast.LENGTH_SHORT).show();
                    }else {
                        if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                            Nagivator.startUpLoadActivity(this,currentTask,fromtask,2);
                        }else {
                            reDownload();
                        }
                    }

                }
                //ApkUtils.startAPP(this,taskData.getApkName());
                break;
            case "3"://注册   上传截图
                if ("跳转".equals(fromtask.getJobMarket())||"二维码".equals(fromtask.getJobMarket())){
                   // if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                        Nagivator.startUpLoadActivity(this,currentTask,fromtask,3);
                   // }else {
                   //     reDownload();
                   // }
                }else {
                    if (hasInstanllTask){
                        Toast.makeText(TaskDetailActivity.this,"请先完成安装任务",Toast.LENGTH_SHORT).show();
                    }else {
                        if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                            Nagivator.startUpLoadActivity(this,currentTask,fromtask,3);
                        }else {
                            reDownload();
                        }
                    }
                }
                break;
            case "4"://试用   打开app 或者上传截图
                if ("跳转".equals(fromtask.getJobMarket())||"二维码".equals(fromtask.getJobMarket())){

                //    if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                        if (taskData.getChecktype()==0){
                            Nagivator.startStayActivity(this,currentTask,fromtask,1);
                        }else {
                            Nagivator.startUpLoadActivity(this,currentTask,fromtask,4);

                        }
               //     }else {
               //         reDownload();
               //     }

                }else {
                    if (hasInstanllTask){
                        Toast.makeText(TaskDetailActivity.this,"请先完成安装任务",Toast.LENGTH_SHORT).show();
                    }else {
                        if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                            if (taskData.getChecktype()==0){
                                Nagivator.startStayActivity(this,currentTask,fromtask,1);
                            }else {
                                Nagivator.startUpLoadActivity(this,currentTask,fromtask,4);

                            }
                        }else {
                            reDownload();
                        }
                    }
                }




                break;
            case "5"://留存    打开APP或者上传截图

                if ("跳转".equals(fromtask.getJobMarket())||"二维码".equals(fromtask.getJobMarket())){
              //      if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                        if (taskData.getChecktype()==0){
                            Nagivator.startStayActivity(this,currentTask,fromtask,0);
                    /*ApkUtils.startAPP(this,fromtask.getApkName());
                    leftTime = System.currentTimeMillis();*/
                        }else {
                            Nagivator.startUpLoadActivity(this,currentTask,fromtask,5);
                        }
             //       }else {
             //           reDownload();
             //       }
                }else {
                    if (hasInstanllTask){
                        Toast.makeText(TaskDetailActivity.this,"请先完成安装任务",Toast.LENGTH_SHORT).show();
                    }else {
                        if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                            if (taskData.getChecktype()==0){
                                Nagivator.startStayActivity(this,currentTask,fromtask,0);
                    /*ApkUtils.startAPP(this,fromtask.getApkName());
                    leftTime = System.currentTimeMillis();*/
                            }else {
                                Nagivator.startUpLoadActivity(this,currentTask,fromtask,5);
                            }
                        }else {
                            reDownload();
                        }
                    }
                }

                break;
            case "6":

                if ("跳转".equals(fromtask.getJobMarket())||"二维码".equals(fromtask.getJobMarket())){
              //      if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                        Nagivator.startUpLoadActivity(this,currentTask,fromtask,3 );
             //       }else {
             //           reDownload();
             //       }
                }else {
                    if (hasInstanllTask){
                        Toast.makeText(TaskDetailActivity.this,"请先完成安装任务",Toast.LENGTH_SHORT).show();
                    }else {

                        if (ApkUtils.hasInstall(this,fromtask.getApkName())){
                            Nagivator.startUpLoadActivity(this,currentTask,fromtask,3 );
                        }else {
                            reDownload();
                        }
                    }

                }


                break;

        }


    }


    private void reDownload(){
        if (fromtask==null){
            return;
        }
        alertHelper.showreDownLoad(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (fromtask.getJobMarket().equals("平台")){
                    startDownload(fromtask.getAppName(),fromtask.getDownUrl());
                }else if (fromtask.getJobMarket().equals("跳转")){
                    ApkUtils.startUrl(TaskDetailActivity.this,fromtask.getDownUrl());
                }else {
                    String marketPacketName = "";
                    switch (fromtask.getJobMarket()){
                        case "华为市场":
                            marketPacketName = "com.huawei.appmarket";
                            break;
                        case "vivo市场":
                            marketPacketName = "com.bbk.appstore";
                            break;
                        case "oppo市场":
                            marketPacketName = "com.oppo.market";
                            break;
                        case "360市场":
                            marketPacketName = "com.qihoo.appstore";
                            break;
                        case "搜狗市场":
                            marketPacketName = "com.sogou.androidtool";
                            break;
                        case "金立市场":
                            marketPacketName = "com.gionee.aora.market";
                            break;
                        case "豌豆荚市场":
                            marketPacketName = "com.wandoujia.phoenix2";
                            break;
                        case "百度市场":
                            marketPacketName = "com.baidu.appsearch";
                            break;
                        case "应用宝":
                            marketPacketName = "com.tencent.android.qqdownloader";
                            break;
                        case "小米市场":
                        case "小米":
                            marketPacketName = "com.xiaomi.market";
                            break;
                        case "安智市场":
                            marketPacketName = "cn.goapk.market";
                            break;
                        case "pp助手":
                            marketPacketName = "com.pp.assistant";
                            break;
                        case "小米游戏中心":
                            marketPacketName = "com.xiaomi.gamecenter";
                            break;
                        case "华为游戏中心":
                            marketPacketName = "com.huawei.gamebox";
                            break;
                        case "应用汇市场":
                            marketPacketName = "com.yingyonghui.market";
                            break;
                        case "qq浏览器":
                            marketPacketName = "com.tencent.mtt";
                            break;
                    }

                    if (!TextUtils.isEmpty(marketPacketName)){
                        ApkUtils.startAPP(TaskDetailActivity.this,marketPacketName);
                    }
                }
            }
        });


    }








    private void initLayout(List<String> images) {
        imageLayout.removeAllViews();
        for (int i=0;i<images.size();i++ ){
            ImageView imageView = (ImageView) View.inflate(this,R.layout.moddle_image,null);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.dip2px(this,200),ScreenUtil.dip2px(this,300),1));
            if (i==0){
                imageView.setPadding(0,0,ScreenUtil.dip2px(this,5),0);
            }else {
                imageView.setPadding(ScreenUtil.dip2px(this,5),0,0,0);
            }

            GlideUtil.loadImageViewFromSD(images.get(i),imageView);

            imageLayout.addView(imageView);
        }
    }











    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            getData();
        }
    }

    private void initView(){
        backImage = findViewById(R.id.back_image);
        backImage.setOnClickListener(this);
        mainTitle = findViewById(R.id.main_title);
        rightTxt = findViewById(R.id.right_txt);
        rightImage = findViewById(R.id.right_image);
        appPic = findViewById(R.id.app_pic);

        appName = findViewById(R.id.app_name);
        appSize = findViewById(R.id.app_size);
        appLable1 = findViewById(R.id.app_lable1);
        appLable2 = findViewById(R.id.app_lable2);
        stepsLayouy = findViewById(R.id.steps_layouy);
        imageLayout = findViewById(R.id.image_layout);
        download = findViewById(R.id.download);
        getmoney = findViewById(R.id.getmoney);

        download_area = findViewById(R.id.download_area);
        download_area.setVisibility(View.GONE);
        progress_txt = findViewById(R.id.progress_txt);
        progressBar = findViewById(R.id.progress);
        downloadtitle = findViewById(R.id.title);
        downloadtitle.setVisibility(View.GONE);

        re_select_pic = findViewById(R.id.re_select_pic);
        re_select_pic.setOnClickListener(this);

        qc_images = findViewById(R.id.qc_images);

        ImageUtil.distinguishQRcode(qc_images, new QcCallBack() {
            @Override
            public void success(String url) {
                if (TextUtils.isEmpty(url)){
                    Toast.makeText(TaskDetailActivity.this,"二维码链接为空",Toast.LENGTH_SHORT).show();
                }else {
                    fromtask.setDownUrl(url);
                    ApkUtils.startUrl(TaskDetailActivity.this,url);
                }
            }
        });

        view_all = findViewById(R.id.view_all);
        view_all.setVisibility(View.INVISIBLE);
    }


    private void startDownload(String fileName, String url){

        download_area.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if (!getPackageManager().canRequestPackageInstalls()){
                ApkUtils.startInstallPermissionSettingActivity(this);
                return;
            }
        }

        fileDownLoad(url,fileName,handler);


    }


    /**
     *
     * @param url   完整的下载链接
     * @param fileName   带后缀的文件名
     */
    public static void fileDownLoad(final String url, String fileName, final Handler handler) {
       /* int i = url.lastIndexOf("/");
        final String title = url.substring(0, i + 1);*/
        final String name = fileName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://codeload.github.com/");//传入服务器BaseUrl
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder().retryOnConnectionFailure(true).addNetworkInterceptor(new okhttp3.Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        return response.newBuilder().body(new ProgressResponseBody(response.body(), new ProgressListener() {
                            @Override
                            public void onProgress(long progress, long total, boolean done) {
                                LogUtils.e("Download","progress : "+progress+"         total : "+total);
                                if (handler!=null){
                                    Message message = Message.obtain();
                                    message.what = 200;
                                    String path = Environment.getExternalStorageDirectory()+"/wandownload/"+name;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("FILE",path);

                                    message.setData(bundle);

                                    double to = total;
                                    double pr = progress;
                                    float pro = (float) (pr/to);
                                    LogUtils.e("Download","pro : "+pro);
                                    message.obj = pro;
                                    handler.sendMessage(message);
                                }

                            }
                        })).build();
                    }
                }).build();
                RetrofitService apiService = builder.client(client).build().create(RetrofitService.class);
                Call<ResponseBody> call = apiService.getFile(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, final retrofit2.Response<ResponseBody> response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (response == null || response.body() == null) {//空地址
                                        LogUtils.e("Download","空地址");
                                        if (handler!=null){
                                            Message message = Message.obtain();
                                            message.what = 201;//
                                            handler.sendMessage(message);
                                        }
                                        return;
                                    }
                                    InputStream is = response.body().byteStream();
                                    String files = Environment.getExternalStorageDirectory()+"/wandownload";
                                    File pfile = new File(files);
                                    if (!pfile.exists()){
                                        pfile.mkdir();
                                    }
                                    File loadFile = new File(pfile, name);//保存路径与文件名
                                    FileOutputStream fos = new FileOutputStream(loadFile);
                                    BufferedInputStream bis = new BufferedInputStream(is);
                                    byte[] bytes = new byte[1024];
                                    int len;
                                    while ((len = bis.read(bytes)) != -1) {
                                        fos.write(bytes, 0, len);
                                        fos.flush();
                                    }
                                    fos.close();
                                    bis.close();
                                    is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();//下载失败
                                    LogUtils.e("Download","下载失败 ："+e.getMessage());
                                    if (handler!=null){
                                        Message message = Message.obtain();
                                        message.what = 201;
                                        handler.sendMessage(message);
                                    }
                                }

                            }
                        }).start();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();//下载失败
                        LogUtils.e("Download","onFailure ："+t.getMessage());
                        if (handler!=null){
                            Message message = Message.obtain();
                            message.what = 201;
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back_image) {
            Nagivator.finishActivity(this);
        }

    }

    @Override
    public void onSuccessed(String name) {



    }

    @Override
    public void onFail() {

    }
}
