package com.wanappsdk.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wanappsdk.keeper.ImageManager;
import com.wanappsdk.ui.view.MeCollectionDialog;
import com.wanappsdk.ui.view.ProgressDialog;
import com.wanappsdk.ui.view.UpLoadCallBack;
import com.wanappsdk.utils.AlertHelper;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.ImageUtil;
import com.wanappsdk.utils.MD5Util;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.QcCallBack;
import com.wanappsdk.utils.ScreenUtil;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;

public class TaskUploadActivity extends TakePhotoActivity implements View.OnClickListener,UpLoadCallBack {

    ImageView app_pic;
    TextView app_name,app_size,app_lable1,app_lable2,getmoney,re_select_pic;
    LinearLayout image_layout,image_simple_layout,copy_layout;

    ImageView backImage;
    TextView mainTitle;
    TextView rightTxt;
    ImageView rightImage;
    Button upload,startapp;
    TextView shuoming;
    TextView notices;
    TextView copy_name;
    ImageView images;

    LinearLayout comment_layout;
    TextView comment_name;

    private boolean canOpen;




    private TaskData currentTask;
    private TaskData mainTask;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        currentTask = (TaskData) getIntent().getSerializableExtra("TASK");
        mainTask = (TaskData) getIntent().getSerializableExtra("MAIN");
        type = getIntent().getIntExtra("TYPE",0);
        if (currentTask==null||mainTask==null){
            Toast.makeText(this,"数据异常",Toast.LENGTH_SHORT).show();
            Nagivator.finishActivity(TaskUploadActivity.this);
            return;
        }
        initView();
        initCreateLayout();
        initData();

        mainTitle.setText(mainTask.getJobName());

        comment_layout = findViewById(R.id.comment_layout);
        comment_name = findViewById(R.id.comment_name);
        comment_name.setOnClickListener(this);
        if ("1".equals(currentTask.getCommentMode())){
            comment_layout.setVisibility(View.VISIBLE);
            comment_name.setText(currentTask.getComment());
        }else {
            comment_layout.setVisibility(View.GONE);
        }
    }


    private void initData(){
        shuoming.setText(currentTask.getStepGuide());

        String[] strs = currentTask.getStepNotice().split("&");

        notices.setText(strs[0]);
        if (strs.length>1){
            images.setVisibility(View.VISIBLE);
            GlideUtil.loadImageView(this,strs[1],images);
        }else {
            images.setVisibility(View.GONE);
        }

        GlideUtil.loadImageView(this,mainTask.getJobLogo(),app_pic);
        app_name.setText(mainTask.getAppName());
        if (mainTask.getTags()!=null){
            String[] strings = mainTask.getTags().split(";");
            if (strings.length>0){
                app_lable1.setText(strings[0]);
            }
            if (strings.length>1){
                app_lable2.setText(strings[1]);
            }
        }

        app_size.setText(currentTask.getStepTitle());


        getmoney.setText("+" + ApkUtils.getMuchMoney(currentTask.getStepPoints()));

        if (TextUtils.isEmpty(currentTask.getKeyWord())){
            copy_layout.setVisibility(View.GONE);
        }else {
            copy_layout.setVisibility(View.VISIBLE);
            copy_name.setText(currentTask.getKeyWord());
        }


        String urls = currentTask.getSimpleUrl();
        String[] strings = urls.split(";");

        simplePic = new ArrayList<>();

        for (int i = 0;i<strings.length;i++){

            simplePic.add(strings[i]);

        }
        initSimpleLayout(simplePic);


    }

    List<String> simplePic = new ArrayList<>();
    ImageView qc_image;
    private void initView(){
        images = findViewById(R.id.images);
        app_pic  = findViewById(R.id.app_pic);
        app_name = findViewById(R.id.app_name);
        app_size = findViewById(R.id.app_size);
        app_lable1 = findViewById(R.id.app_lable1);
        app_lable2 = findViewById(R.id.app_lable2);
        getmoney = findViewById(R.id.getmoney);
        re_select_pic = findViewById(R.id.re_select_pic);
        re_select_pic.setOnClickListener(this);
        image_layout = findViewById(R.id.image_layout);

        image_simple_layout = findViewById(R.id.image_simple_layout);

        backImage = findViewById(R.id.back_image);
        backImage.setOnClickListener(this);
        mainTitle = findViewById(R.id.main_title);
        rightTxt = findViewById(R.id.right_txt);
        rightImage = findViewById(R.id.right_image);
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(this);
        startapp = findViewById(R.id.startapp);
        startapp.setOnClickListener(this);
        copy_layout = findViewById(R.id.copy_layout);
        copy_name = findViewById(R.id.copy_name);
        copy_name.setOnClickListener(this);
        qc_image = findViewById(R.id.qc_image);
        qc_image.setVisibility(View.GONE);
        startapp.setVisibility(View.VISIBLE);
        if ("平台".equals(mainTask.getJobMarket())){
            startapp.setText("打开APP");
        }else if ("二维码".equals(mainTask.getJobMarket())) {
            qc_image.setVisibility(View.VISIBLE);
            GlideUtil.loadBitMapImageView(this,mainTask.getDownUrl(),qc_image);
            startapp.setVisibility(View.GONE);
            //startapp.setText("打开链接");
        }else if ("跳转".equals(mainTask.getJobMarket())){
            startapp.setText("打开链接");
        }else {
            if (type==2){
                startapp.setText("打开市场");
            }else if (type==3){
                startapp.setText("打开APP");
            }else if (type==4){
                startapp.setText("打开APP");
            }else if (type==5){
                startapp.setText("打开APP");
            }

        }

        shuoming = findViewById(R.id.shuoming);
        notices = findViewById(R.id.notices);

        qc_image = findViewById(R.id.qc_image);
        ImageUtil.distinguishQRcode(qc_image, new QcCallBack() {
            @Override
            public void success(String url) {
                if (TextUtils.isEmpty(url)){
                    Toast.makeText(TaskUploadActivity.this,"链接为空",Toast.LENGTH_SHORT).show();
                }else {
                    canOpen = true;
                    ApkUtils.startUrl(TaskUploadActivity.this,url);
                }
            }
        });
    }

    private CompressConfig getCompressConfig(){

        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(1024*1024*80)
                .setMaxPixel(400)
                .enableReserveRaw(true)
                .create();

        return config;
    }




    private void showCollectionDialog(final int  which){
        if (!TextUtils.isEmpty(currentTask.getCredit_card())){
            if (currentTask.getCredit_card().contains("1")&&currentTask.getCredit_card().length()==8){
                List<String> showIt = new ArrayList<>();
                for (int i = 0;i<currentTask.getCredit_card().length();i++){
                    showIt.add(currentTask.getCredit_card().charAt(i)+"");
                }
                MeCollectionDialog dialog = new MeCollectionDialog(TaskUploadActivity.this, mainTask, new AlertHelper.IAlertListener() {
                    @Override
                    public void sure() {
                        if (which==0){
                            uploadPics();
                        }else {
                            completeTask(currentTask,"aaa");
                        }
                        Toast.makeText(TaskUploadActivity.this,"提交成功",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void cancel() {
                        Toast.makeText(TaskUploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }
                },showIt);
                dialog.show();
            }else {
                if (which==0){
                    uploadPics();
                }else {
                    completeTask(currentTask,"aaa");
                }
            }

        }else {
            if (which==0){
                uploadPics();
            }else {
                completeTask(currentTask,"aaa");
            }
        }
    }

    /**
     * 初始化上传
     */
    private void initCreateLayout() {
        image_layout.removeAllViews();
       /* View view = View.inflate(this, R.layout.moddle_upload_image, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTakePhoto().onEnableCompress(getCompressConfig(),true);
                getTakePhoto().onPickMultiple(3);
            }
        });

        image_layout.addView(view);*/

        adduploadImage();


    }




    private void adduploadImage(){
        View view = View.inflate(this, R.layout.moddle_upload_image, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canOpen){
                    isStartPic = true;
                    getTakePhoto().onEnableCompress(getCompressConfig(),true);
                    getTakePhoto().onPickMultiple(3);
                    //getTakePhoto().onPickFromGallery();
                } else {
                    Toast.makeText(TaskUploadActivity.this,"请按照说明提示步骤进行任务",Toast.LENGTH_SHORT).show();
                }

            }
        });

        image_layout.addView(view);
        checkChange();
    }



    private void initLayout(final List<TImage> images) {
        image_layout.removeAllViews();
        for (int i=0;i<images.size();i++ ){
            final RelativeLayout layout = (RelativeLayout) View.inflate(this,R.layout.up_pic_del,null);
            ImageView imageView = layout.findViewById(R.id.pic);
            ImageView delete = layout.findViewById(R.id.del);

            if (i==0){
                imageView.setPadding(0,0,ScreenUtil.dip2px(this,5),0);
            }else {
                imageView.setPadding(ScreenUtil.dip2px(this,5),0,0,0);
            }

            GlideUtil.loadImageViewFromSD(images.get(i).getCompressPath(),imageView);
            final int finalI = i;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pics.remove(finalI);
                    initLayout(pics);
                    //image_layout.removeView(layout);
                   /* if (image_layout.getChildCount()==0){
                        View view = View.inflate(TaskUploadActivity.this, R.layout.moddle_upload_image, null);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(canOpen){
                                    isStartPic = true;
                                    getTakePhoto().onEnableCompress(getCompressConfig(),true);
                                    getTakePhoto().onPickMultiple(3);
                                } else {
                                    Toast.makeText(TaskUploadActivity.this,"请先去打开",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        image_layout.addView(view);
                    }*/

                }
            });
            image_layout.addView(layout);
        }

        adduploadImage();




    }


    private void initSimpleLayout(final List<String> images) {
        image_simple_layout.removeAllViews();
        for (int i=0;i<images.size();i++ ){
            final String url = images.get(i);
            ImageView imageView = (ImageView) View.inflate(this,R.layout.moddle_image,null);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ScreenUtil.dip2px(this,200),ScreenUtil.dip2px(this,300),1));
            if (i==0){
                imageView.setPadding(0,0,ScreenUtil.dip2px(this,5),0);
            }else {
                imageView.setPadding(ScreenUtil.dip2px(this,5),0,0,0);
            }
            GlideUtil.loadImageView(this,images.get(i),imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskUploadActivity.this,BigPicActivity.class);
                    intent.putExtra("URL",url);
                    startActivity(intent);
                }
            });
            image_simple_layout.addView(imageView);
        }
    }


    List<TImage> pics = new ArrayList<>();
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
            String md5 = MD5Util.getFileMD5(tImages.get(i).getOriginalPath());
            if (ImageManager.getInstance().messace.getImageMd5().contains(md5)){
                Toast.makeText(this,"请按照要求进行任务",Toast.LENGTH_SHORT).show();
            }else {
                pics.add(tImages.get(i));
            }
        }
        initLayout(pics);
    }


    public void addMd5List(){
        for (int i = 0;i<pics.size();i++){
            String md5 = MD5Util.getFileMD5(pics.get(i).getOriginalPath());
            if (!ImageManager.getInstance().messace.getImageMd5().contains(md5)){
                ImageManager.getInstance().messace.getImageMd5().add(md5);
            }
        }
        ImageManager.getInstance().saveUserData();

    }


    private int currentPosition = 0;
    private void uploadPics(){
        if (pics!=null&&pics.size()>0&&currentTask!=null){
            currentPosition = 0;

            List<String> list = new ArrayList<>();
            for (int i = 0;i<pics.size();i++){
                list.add(pics.get(i).getCompressPath());
            }

            ProgressDialog progressDialog = new ProgressDialog(this, list,currentTask.getId(),mainTask.getPolicyId());
            progressDialog.setUpLoadCallBack(this);
            progressDialog.show();
        }else {
            Toast.makeText(this,"请先选择图片",Toast.LENGTH_SHORT).show();
        }
    }


    private void checkChange(){
        if ("二维码".equals(mainTask.getJobMarket())||"跳转".equals(mainTask.getJobMarket())){
            return;
        }
        if (isStartPic&&canOpen&&pics.size()==simplePic.size()&&ApkUtils.hasInstall(this,mainTask.getApkName())){
            upload.setText("提交任务");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkChange();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        checkChange();
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

                        ShowGold showGold = new ShowGold(TaskUploadActivity.this);
                        showGold.show((LinearLayout) findViewById(R.id.container), TaskUploadActivity.this, new ShowGold.AnimotionFinish() {
                            @Override
                            public void onFinish() {

                                if (taskData.getChecktype()==0||taskData.getChecktype()==2){//免审
                                    Toast.makeText(TaskUploadActivity.this,"提交成功，任务奖励已经到账",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(TaskUploadActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                                }

                                Nagivator.finishActivity(TaskUploadActivity.this);
                                addMd5List();
                            }
                        });


                    }else {
                        Toast.makeText(TaskUploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TaskUploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                mDialog.dismiss();
                Toast.makeText(TaskUploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                mDialog.dismiss();
                Gson gson = new Gson();
                BaseBean baseComlete = gson.fromJson(json,BaseBean.class);
                if (baseComlete.getCode()==110){
                    Toast.makeText(TaskUploadActivity.this,"提交成功",Toast.LENGTH_SHORT).show();
                    Nagivator.finishActivity(TaskUploadActivity.this);
                    addMd5List();
                }else {
                    Toast.makeText(TaskUploadActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }





    private boolean isStartPic;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.re_select_pic) {
            initCreateLayout();
        }else if (i == R.id.back_image){
            Nagivator.finishActivity(this);
        }else if (i ==R.id.upload){
            if (!canOpen||!isStartPic){
                Toast.makeText(this,"请按照提示操作后再来提交",Toast.LENGTH_SHORT).show();
                return;
            }
            if (simplePic.size()==pics.size()){

                showCollectionDialog(0);
                //uploadPics();
            }else {
                Toast.makeText(this,"上传图片数量不符合示例图片数量",Toast.LENGTH_SHORT).show();
            }

        }else if (i==R.id.startapp){
                String marketPacketName ="";
                String apkName = mainTask.getApkName();
                String download  = "";
                switch (mainTask.getJobMarket()){
                    case "跳转":
                    case "二维码":
                        download =mainTask.getDownUrl();
                        break;
                    case "平台":
                        marketPacketName = mainTask.getApkName();
                        break;
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
                        canOpen = true;
                    if (!TextUtils.isEmpty(marketPacketName)){
                        if (startapp.getText().toString().equals("打开市场")){
                            ApkUtils.startAPP(this,marketPacketName);
                        }else if (startapp.getText().toString().equals("打开APP")){
                            ApkUtils.startAPP(this,apkName);
                        }
                    }else {
                        ApkUtils.startUrl(this,download);
                    }
        }else if (i==R.id.copy_name){
            ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label",copy_name.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            Toast.makeText(this,"已复制至剪切板",Toast.LENGTH_SHORT).show();

        }else if (i==R.id.comment_name){

            ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label",comment_name.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            Toast.makeText(this,"已复制至剪切板",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onSuccessed(String name) {
        completeTask(currentTask,name);
    }

    @Override
    public void onFail() {
        Toast.makeText(this,"上传失败",Toast.LENGTH_SHORT).show();
    }
}
