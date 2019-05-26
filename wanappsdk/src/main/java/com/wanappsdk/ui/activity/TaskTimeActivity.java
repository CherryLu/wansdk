package com.wanappsdk.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wanappsdk.R;
import com.wanappsdk.animotion.ShowGold;
import com.wanappsdk.baen.BaseBean;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.ui.view.MeCollectionDialog;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.ImageUtil;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.QcCallBack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;

public class TaskTimeActivity extends BaseActivity {

    ImageView backImage;
    TextView mainTitle;
    TextView rightTxt;
    ImageView rightImage;
    ImageView appPic;
    TextView appName;
    TextView appSize;
    TextView appLable1;
    TextView appLable2;
    TextView getmoney;

    TextView shuoming;
    TextView notices;

    Button task_start;
    Button startapp;
    ImageView images;



    TaskData mainTask;
    TaskData stepTask;
    ImageView qc_image;

    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasktimer);
        mainTask = (TaskData) getIntent().getSerializableExtra("MAIN");
        stepTask = (TaskData) getIntent().getSerializableExtra("TASK");
        initView();
        initData();
        setTitleBar(mainTask.getJobName());

    }

    private void initData(){
        shuoming.setText(stepTask.getStepGuide());

        if (!TextUtils.isEmpty(stepTask.getStepNotice())){
            String[] strs = stepTask.getStepNotice().split("&");

            notices.setText(strs[0]);
            if (strs.length>1){
                images.setVisibility(View.VISIBLE);
                GlideUtil.loadImageView(this,strs[1],images);
            }else {
                images.setVisibility(View.GONE);
            }
        }



        GlideUtil.loadImageView(this,mainTask.getJobLogo(),appPic);
        appName.setText(mainTask.getAppName());
        if (mainTask.getTags()!=null){
            String[] strings = mainTask.getTags().split(";");
            if (strings.length>0){
                appLable1.setText(strings[0]);
            }
            if (strings.length>1){
                appLable2.setText(strings[1]);
            }
        }

        appSize.setText(stepTask.getStepTitle());
        LogUtils.e("ZXZXZXZXZXZXZXZX",stepTask.getStepPoints()+"");
        getmoney.setText("+" + ApkUtils.getMuchMoney(stepTask.getStepPoints()));
        String total = stepTask.getMinstaySec()+"";
        task_start.setText("开始体验"+total+"秒");

        if ("二维码".equals(mainTask.getJobMarket())){
            qc_image.setVisibility(View.VISIBLE);
            GlideUtil.loadBitMapImageView(this,mainTask.getDownUrl(),qc_image);
        }else {
            qc_image.setVisibility(View.GONE);
        }
    }

    private void showCollectionDialog(final int  which){
        if (!TextUtils.isEmpty(stepTask.getCredit_card())){
            if (stepTask.getCredit_card().contains("1")&&stepTask.getCredit_card().length()==8){
                List<String> showIt = new ArrayList<>();
                for (int i = 0;i<stepTask.getCredit_card().length();i++){
                    showIt.add(stepTask.getCredit_card().charAt(i)+"");
                }
                MeCollectionDialog dialog = new MeCollectionDialog(TaskTimeActivity.this, mainTask, new AlertHelper.IAlertListener() {
                    @Override
                    public void sure() {
                        completeTask(stepTask,"aaa");
                        Toast.makeText(TaskTimeActivity.this,"提交成功",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void cancel() {
                        Toast.makeText(TaskTimeActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }
                },showIt);
                dialog.show();
            }else {
                completeTask(stepTask,"aaa");

            }

        }else {
            completeTask(stepTask,"aaa");
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (requestCode==ApkUtils.START_APP){

            totalTime = System.currentTimeMillis() - leftTime + totalTime;

            if (totalTime>=stepTask.getMinstaySec()*1000){
                task_start.setText("开始体验");
                showCollectionDialog(0);
                //completeTask(stepTask,"aaa");
            }else {

                String total = totalTime/1000+"";
                String left = (stepTask.getMinstaySec()*1000-totalTime)/1000+1+"";
                task_start.setText("继续体验"+left+"秒即可获得奖励,点击继续体验");

                Toast.makeText(this,"您的留存时间不足",Toast.LENGTH_SHORT).show();
            }

            requestCode = 0;

        }
    }

    /**
     * 任务完成
     * @param taskData
     * @param urls
     */
    private void completeTask(final TaskData taskData, String urls){

        final SweetAlertDialog mDialog =new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.setCancelable(false);
        mDialog.setTitleText("同步中...").show();


        ApiServiceManager.getTaskComplete(mainTask.getApkName(), taskData.getId(), urls, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                mDialog.dismiss();
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseBean baseComlete = gson.fromJson(json,BaseBean.class);
                    if (baseComlete.getCode()==110){

                        ShowGold showGold = new ShowGold(TaskTimeActivity.this);
                        showGold.show((LinearLayout) findViewById(R.id.container), TaskTimeActivity.this, new ShowGold.AnimotionFinish() {
                            @Override
                            public void onFinish() {
                                if (taskData.getChecktype()==0||taskData.getChecktype()==2){//免审
                                    Toast.makeText(TaskTimeActivity.this,"提交成功，任务奖励已经到账",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(TaskTimeActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                                }
                                Nagivator.finishActivity(TaskTimeActivity.this);
                            }
                        });



                    }else if (baseComlete.getCode()==112){
                        Toast.makeText(TaskTimeActivity.this,"您已经完成该任务",Toast.LENGTH_SHORT).show();
                        Nagivator.finishActivity(TaskTimeActivity.this);
                    }else {
                        Toast.makeText(TaskTimeActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TaskTimeActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e("ZXZXZX",e.getMessage());
                e.printStackTrace();
                mDialog.dismiss();
                Toast.makeText(TaskTimeActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                mDialog.dismiss();
                Gson gson = new Gson();
                BaseBean baseComlete = gson.fromJson(json,BaseBean.class);
                if (baseComlete.getCode()==110){
                    Toast.makeText(TaskTimeActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(TaskTimeActivity.this);
                }else if (baseComlete.getCode()==112){
                    Toast.makeText(TaskTimeActivity.this,"您已经完成该任务",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(TaskTimeActivity.this);
                }else {
                    Toast.makeText(TaskTimeActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    String urls;
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
        getmoney = findViewById(R.id.getmoney);

        task_start = findViewById(R.id.task_start);
        task_start.setOnClickListener(this);

         shuoming = findViewById(R.id.shuoming);
         notices = findViewById(R.id.notices);
        startapp = findViewById(R.id.startapp);
        startapp.setVisibility(View.GONE);
        images = findViewById(R.id.images);
        qc_image = findViewById(R.id.qc_image);

        ImageUtil.distinguishQRcode(qc_image, new QcCallBack() {
            @Override
            public void success(String url) {
                if (TextUtils.isEmpty(url)){
                    Toast.makeText(TaskTimeActivity.this,"链接为空",Toast.LENGTH_SHORT).show();
                }else {
                    ApkUtils.startUrl(TaskTimeActivity.this,url);
                }
            }
        });

    }

    private long  leftTime;
    private long  totalTime;
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.back_image) {
            Nagivator.finishActivity(this);
        }else if (i==R.id.task_start){
            requestCode = ApkUtils.START_APP;
            leftTime = System.currentTimeMillis();
            ApkUtils.startAPP(this,mainTask.getApkName());
        }
        super.onClick(view);
    }
}
