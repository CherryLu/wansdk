package com.wanappsdk.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wanappsdk.R;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ProgressListener;
import com.wanappsdk.http.ProgressResponseBody;
import com.wanappsdk.http.RetrofitService;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SignActivity extends BaseActivity {


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

    TextView downloadtitle;


    private TaskData taskData;

    private boolean isDownloadComplete;
    private List<TaskData> data = new ArrayList<>();

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
                        downloadtitle.setVisibility(View.VISIBLE);
                        download_area.setVisibility(View.GONE);
                        //downloadtitle.setText("下载完成");//跳转安装页面
                        //download_install.setVisibility(View.VISIBLE);
                        isDownloadComplete = true;

                        //getDownLoadCompletion();
                    }

                    break;
                case 201:
                    if (downloadtitle!=null){
                        downloadtitle.setVisibility(View.VISIBLE);
                        download_area.setVisibility(View.GONE);
                        downloadtitle.setText("下载错误");
                    }

                    break;

            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_download);
        initView();
        //setdatas();
        setData();

    }

/*
    private void setdatas(){
        for (int i = 0;i<4;i++){
            TaskData taskData = new TaskData();
            switch (i){
                case 0:
                    taskData.setTitle("下载安装测试APP");
                    taskData.setSubtitle("应用不要通过其他渠道下载安装，否则无法获得奖励");
                    taskData.setJzGain(300d);
                    taskData.setCanDo(true);
                    break;
                case 1:
                    taskData.setTitle("注册账号，邀请好友，完成新手任务");
                    taskData.setSubtitle("当前可进行");
                    taskData.setJzGain(200d);

                    break;
                case 2:
                    taskData.setTitle("第二天打开体验三分钟");
                    taskData.setSubtitle("任务未解锁");
                    taskData.setJzGain(100d);

                    break;

                case 3:
                    taskData.setTitle("第二天打开体验三分钟");
                    taskData.setSubtitle("任务未解锁");
                    taskData.setJzGain(100d);

                    break;
            }

            data.add(taskData);

        }
    }*/


    private void setData(){

        GlideUtil.loadImageView(this,"https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1539281487934&di=d5e3155b1d297891831e4f2b215cf728&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3D083790ed0323dd54357eaf2bb960d9ab%2F574e9258d109b3de5fa608e1c6bf6c81800a4c1a.jpg",appPic);
        appName.setText("测试APP");
        appLable1.setText("免审");
        appLable2.setText("测试");
        getmoney.setText("+0.8元");

        stepsLayouy.removeAllViews();
        for (int i = 0;i<data.size();i++){

            stepsLayouy.addView(getStepView(data.get(i)));
        }

    }


    private View getStepView(final TaskData taskData) {

        if (taskData == null) {
            return null;
        }
        View view = View.inflate(this, R.layout.step_item, null);
        TextView step_number = view.findViewById(R.id.step_number);
        View line = view.findViewById(R.id.line);
        TextView step_descrip = view.findViewById(R.id.step_descrip);
        TextView step_descripe = view.findViewById(R.id.step_descripe);
        TextView step_money = view.findViewById(R.id.step_money);



        return view;
    }


    private void startDownload(){

        download_area.setVisibility(View.VISIBLE);


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
}
