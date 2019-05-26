package com.wanappsdk.http;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wanappsdk.BuildConfig;
import com.wanappsdk.WanSdkManager;
import com.wanappsdk.keeper.DevicesManager;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.MD5Util;
import com.wanappsdk.utils.UserManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/8/6.
 */

public class ApiServiceManager {



    private static final String GET_TASKLIST = "User/rec";

    private static final String GET_SIGN_TASKLIST = "User/sig";

    private static final String GET_TASK_DETAIL = "User/recJob";

    private static final String GET_TASK_AVAILABLE = "User/taskRec";

    private static final String GET_TASK_STAR = "User/taskRecStart";

    private static final String GET_TASK_COMPLETE = "User/taskRecSub";

    private static final String GET_LOGIN_IN = "User/login";

    private static final String POST_USERINFO = "User/userInfo";

    private static final String GET_ARTIFICAL = "User/read";

    private static final String UP_READ_TASK = "User/readSub";


    private static final String  GET_XIAOCHENGXU = "User/wchartInt";


    /**
     * 获取小程序列表
     */
    public static void getXiaochengxu(HttpResponse response){

        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        doGet(GET_XIAOCHENGXU,map, new HttpCallBack(response));

    }



    /**
     *获取列表
     * @param mid 列表标志
     * @param response
     */
    public static void getDataList(String mid, HttpResponse response){
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("mid", mid);
        stringMap.put("uid", WanSdkManager.getUserId());
        stringMap.put("count", "20");
        stringMap.put("count_page", "0");
        doGet(GET_TASKLIST,stringMap, new HttpCallBack(response));
    }




    /**
     *获取推荐列表
     * @param response
     */
    public static void getDataList(int page, HttpResponse response){
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("deviceId", WanSdkManager.getUserId());
        stringMap.put("count_page", page+"");
        stringMap.put("count", "20");
        stringMap.put("channelId",WanSdkManager.getChannelD());

        doGet(GET_TASKLIST,stringMap, new HttpCallBack(response));
    }

    /**
     *获取推荐列表
     * @param mid 列表标志
     * @param response
     */
    public static void getSignDataList(String mid, int page, HttpResponse response){
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("deviceId", WanSdkManager.getUserId());
        stringMap.put("count_page", page+"");
        stringMap.put("count", "20");
        doGet(GET_SIGN_TASKLIST,stringMap, new HttpCallBack(response));
    }


    /**
     * 任务详情
     * @param bid
     * @param httpResponse
     */
    public static void  getTaskDetails(String bid,HttpResponse httpResponse){

        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        map.put("bid",bid);

        doGet(GET_TASK_DETAIL,map, new HttpCallBack(httpResponse));

    }


    /**
     * 获取任务是否可用
     * @param response
     */
    public static void getTaskAvailable(String policyId, String taskID,HttpResponse response){
        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        map.put("policyId",policyId);
        map.put("taskID","1");
        doGet(GET_TASK_AVAILABLE,map,new HttpCallBack(response));
    }


    /**
     * 任务开始
     * @param response
     */
    public static void getTaskStart(String policyId, String taskID,HttpResponse response){
        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        map.put("policyId",policyId);
        map.put("taskID","1");
        doGet(GET_TASK_STAR,map,new HttpCallBack(response));
    }

    /**
     * 任务完成
     * @param policyId
     * @param uploadUrl
     * @param response
     */
    public static void getTaskComplete(String apkName,String policyId,String uploadUrl,HttpResponse response){

        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        map.put("policyId",policyId);
        map.put("uploadUrl",uploadUrl);
        map.put("channelUid",WanSdkManager.getChanneluid());
        map.put("apkName",apkName);
        map.put("channelId",WanSdkManager.getChannelD());

        doPost(GET_TASK_COMPLETE,map,new HttpCallBack(response));
    }



    /**
     * 阅读任务完成
     * @param response
     */
    public static void getReadTaskComplete(String readId,HttpResponse response){

        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        map.put("readId",readId);
        map.put("channelUid",WanSdkManager.getChanneluid());
        map.put("channelId",WanSdkManager.getChannelD());
        map.put("uploadUrl","");
        map.put("apkName","");

        doPost(UP_READ_TASK,map,new HttpCallBack(response));
    }

    /**
     * 用户信息上报
     */
    /*public static void getUserInfoUpload(String policyId,String ){

        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());
        map.put("channelId","10001");
        map.put("channeluid",WanSdkManager.getChanneluid());
        map.put("policyId",policyId);

        //map.put("Apkname",apkName);

    }*/

    /**
     * 登录接口
     * @param deviceId
     * @param brand
     * @param imei1
     * @param imei2
     * @param response
     */
    public static void loginSDK(String deviceId,String brand,String imei1,String imei2,HttpResponse response){

        Map<String,String> map = new HashMap<>();

        map.put("deviceId",WanSdkManager.getUserId());

        map.put("brand",brand);

        map.put("imei1",imei1);

        map.put("imei2",imei2);
        LogUtils.e("MEID",DevicesManager.getInstance().messace.getMeid()+"");
        if (!TextUtils.isEmpty(DevicesManager.getInstance().messace.getMeid())){
            map.put("meid",DevicesManager.getInstance().messace.getMeid());
        }


        doPost(GET_LOGIN_IN,map,new HttpCallBack(response));


    }


    /**
     * 上报内容
     * @param response
     */
    public static void uploadInfo(String jobId,String isuserphone,String isusername,String isuseridcard,String isuseraccount,String isuserpasswd,String isuserincode,String isuserorder,String isuserweizhi,HttpResponse response){
        Map<String,String> map = new HashMap<>();
        map.put("deviceid",WanSdkManager.getUserId());
        map.put("jobId",jobId);
        if (!TextUtils.isEmpty(isuserphone)){
            map.put("isuserphone",isuserphone);
        }

        if (!TextUtils.isEmpty(isusername)){
            map.put("isusername",isusername);
        }

        if (!TextUtils.isEmpty(isuseridcard)){
            map.put("isuseridcard",isuseridcard);
        }

        if (!TextUtils.isEmpty(isuseraccount)){
            map.put("isuseraccount",isuseraccount);
        }

        if (!TextUtils.isEmpty(isuserpasswd)){
            map.put("isuserpasswd",isuserpasswd);
        }

        if (!TextUtils.isEmpty(isuserincode)){
            map.put("isuserincode",isuserincode);
        }


        if (!TextUtils.isEmpty(isuserorder)){
            map.put("isuserorder",isuserorder);
        }

        if (!TextUtils.isEmpty(isuserweizhi)){
            map.put("isuserweizhi",isuserweizhi);
        }

        doPost(POST_USERINFO,map,new HttpCallBack(response));

    }


    /**
     * 获取阅读文章详情
     */
    public static void getArtificalDetail(HttpResponse response){
        Map<String,String> map = new HashMap<>();
        map.put("deviceId",WanSdkManager.getUserId());

        doGet(GET_ARTIFICAL,map,new HttpCallBack(response));

    }


    /**
     * 获取IP
     */
    public static String getIP(Context context){
        String ip = "";
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getActiveNetworkInfo();
        if (network!=null){
            if (network.isConnected()){//已经连接
                if (network.getType()==ConnectivityManager.TYPE_WIFI){
                     ip = getWIFILocalIpAdress(context);
                }else {
                     ip = getGPRSLocalIpAddress();
                }

            }else {//没网

            }
        }
        LogUtils.e("ZXZXZXZX",ip);
        return ip;

    }




    /**
     * 获取非WIFI时的IP
     * @return
     */
    private static String getGPRSLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }


    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF ) + "." +
                ((ipAdress >> 8 ) & 0xFF) + "." +
                ((ipAdress >> 16 ) & 0xFF) + "." +
                ( ipAdress >> 24 & 0xFF) ;
    }


    /**
     * 获取WIFI是的IP
     * @param mContext
     * @return
     */
    private static String getWIFILocalIpAdress(Context mContext) {

        //获取wifi服务
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
       /* //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }*/
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }



    private static String wifiTag = "wlan0";//有线标志
    private static String localTag = "eth0";//无线标志


    /**
     * 蓝牙
     * @param context
     * @return
     */
    public synchronized static String getMacid(Context context) {
        BluetoothAdapter mBlueth= BluetoothAdapter.getDefaultAdapter();
        String mBluethId= mBlueth.getAddress();
        return mBluethId;
    }



    /**
     * 获取MAC地址
     * @return
     */
    public static String getMacAddr() {
      /*  try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase(localTag)) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }*/
        return "02:00:00:00:00:00";
    }


    private static void doGet(String action, Map<String, String> stringMap, HttpCallBack consumer) {
        if (TextUtils.isEmpty(WanSdkManager.packetMd5)){
            WanSdkManager.packetMd5 = "abc";
        }
        String data = getData();
        String code = getCode(data);
        stringMap.put("timestamp", data);
        stringMap.put("code",code);
        stringMap.put("packetmd5",WanSdkManager.packetMd5);
        stringMap.put("ip",getIP(WanSdkManager.getContext()));
        stringMap.put("mac",getMacAddr());
        stringMap.put("isroot",WanSdkManager.isRoot+"");
        stringMap.put("isxopse",WanSdkManager.isXposed+"");

        consumer.setCode(code);


        Observable<ResponseBody> observable = getHttpService().create(RetrofitService.class).getGetRequest(action,stringMap);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }







    /*private static void doGet(String action, Map<String, String> stringMap, Observer<ResponseBody> consumer) {
        if (TextUtils.isEmpty(WanSdkManager.packetMd5)){
            WanSdkManager.packetMd5 = "abc";
        }
        String data = getData();
        String code = getCode(data);
        stringMap.put("timestamp", data);
        stringMap.put("code",code);
        stringMap.put("packetmd5",WanSdkManager.packetMd5);
        stringMap.put("ip",getIP(WanSdkManager.getContext()));
        stringMap.put("mac",getMacAddr());
        stringMap.put("isroot",WanSdkManager.isRoot+"");
        stringMap.put("isxopse",WanSdkManager.isXposed+"");


        Observable<ResponseBody> observable = getHttpService().create(RetrofitService.class).getGetRequest(action,stringMap);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }*/




    private static void doPost(String action, Map<String,String> stringMap, HttpCallBack consumer){
        String data = getData();
        if (TextUtils.isEmpty(WanSdkManager.packetMd5)){
            WanSdkManager.packetMd5 = "abc";
        }

        LogUtils.e("USERID","Id ; "+WanSdkManager.getUserId());
        stringMap.put("timestamp", data);
        stringMap.put("code", getCode(data));
        stringMap.put("packetmd5",WanSdkManager.packetMd5);
        stringMap.put("ip",getIP(WanSdkManager.getContext()));
        stringMap.put("mac",getMacAddr());
        stringMap.put("isroot",WanSdkManager.isRoot+"");
        stringMap.put("isxopse",WanSdkManager.isXposed+"");

        if (action.equals(GET_LOGIN_IN)){
            consumer.setCode(getCode(data));
        }
        Observable<ResponseBody> observable = getHttpService().create(RetrofitService.class).getPostRequest(action,stringMap);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }

    private static void doPostTest(String action, String eid, String jid, String jz_gain, String remark, String snap_url, String status, Observer<ResponseBody> consumer){
        String data = getData();
       // stringMap.put("timestamp", data);
        //stringMap.put("code", getCode(data));

        Observable<ResponseBody> observable = getHttpService().create(RetrofitService.class).getPostTestRequest(action,UserManager.getInstance().getId(),eid,data,getCode(data),jid,jz_gain,remark,snap_url,status,data,data);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }

    private static String getData() {
        Date date = new Date();
        return date.getTime() + "";
    }

    private static long[] getToday(){
        long current=new Date().getTime();//当前时间毫秒数
        long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        long twelve=zero+24*60*60*1000-1;//今天23点59分59秒的毫秒数

        long[] longs = new long[]{zero,twelve};

        return  longs;
    }

    /**
     * 获取一周 起始时间
     * @return
     */
    private static String getThisWeekStart(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMMdd", Locale. getDefault());
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar. DAY_OF_WEEK) - 1;
        if (day_of_week == 0 ) {
            day_of_week = 7 ;
        }
        cal.add(Calendar.DATE , -day_of_week + 1 );


        return  simpleDateFormat.format(cal.getTime()) + "000000";
    }

    /**
     * 获取一周结束时间
     * @return
     */

    private static String getThisWeekEnd(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMMdd", Locale. getDefault());
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar. DAY_OF_WEEK) - 1;
        if (day_of_week == 0 ) {
            day_of_week = 7 ;
        }
        cal.add(Calendar.DATE , -day_of_week + 7 );
        return simpleDateFormat.format(cal.getTime()) + "235959";

    }



    private static String getCode(String data) {
        String key = "yioye@sina.cn";
        String before = key + data;
        return MD5Util.lowerMD5(before);
    }

    private static String getTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);

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
                OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new okhttp3.Interceptor() {
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
                                    File loadFile = new File(Environment.getExternalStorageDirectory(), name);//保存路径与文件名
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


    private static Retrofit getHttpService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }


    private static OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.e("Http",message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        builder.retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        return builder.build();
    }


    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;

    }

}
