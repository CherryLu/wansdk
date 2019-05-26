package com.wanappsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.wanappsdk.baen.ArtificalData;
import com.wanappsdk.baen.BaseAllTaskList;
import com.wanappsdk.baen.BaseBean;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.BaseTaskList;
import com.wanappsdk.baen.BaseXiaochengxu;
import com.wanappsdk.baen.DeviceMessace;
import com.wanappsdk.baen.ImageMessage;
import com.wanappsdk.baen.LoginData;
import com.wanappsdk.baen.PacketMessage;
import com.wanappsdk.baen.SortBean;
import com.wanappsdk.baen.SortData;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.baen.TitleMessage;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.keeper.ArtificalManager;
import com.wanappsdk.keeper.DevicesManager;
import com.wanappsdk.keeper.ImageManager;
import com.wanappsdk.keeper.PacketManager;
import com.wanappsdk.ui.activity.ReadActivity;
import com.wanappsdk.ui.activity.SortActivity;
import com.wanappsdk.ui.activity.XiaochengxuActivity;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.Constant;
import com.wanappsdk.utils.CrashHandler;
import com.wanappsdk.utils.DateCallBack;
import com.wanappsdk.utils.GetSystemInfoUtil;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.LoginCallBack;
import com.wanappsdk.utils.MD5Util;
import com.wanappsdk.utils.Nagivator;
import com.wanappsdk.utils.TimeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

import static android.content.Context.TELEPHONY_SERVICE;

public class WanSdkManager {


    private static OSS oss;
    private static Context context;
    public static String  packetMd5;
    public static TitleMessage currentTitle;
    public static List<TitleMessage> titleMessages;

    public static String  userId;

    private static WanSdkManager sdkManager;

    private XiaoChengXuClick xiaoChengXuClick;

    public static boolean isRoot;

    public static boolean isXposed;

    private static String channeluid;


    private static String channelD = "10001";


    public void setXiaoChengXuClick(XiaoChengXuClick xiaoChengXuClick) {
        this.xiaoChengXuClick = xiaoChengXuClick;
    }

    public XiaoChengXuClick getXiaoChengXuClick() {
        return xiaoChengXuClick;
    }

    public static String getChannelD() {
        return channelD;
    }

    public static String getChanneluid() {
        return channeluid;
    }

    private WanSdkManager(Context mContext) {
        context = mContext;
        initOSSClient();
        getAllApkPackName(context);
        initImageLoader();
       // ApiServiceManager.getIP(mContext);
        isRoot = isRoot();
        isXposed = isXpose();
        CrashHandler.getInstance().init(mContext);
        boolean hasData = ArtificalManager.getInstance().readUserData();
        if (!hasData){
            ArtificalData data = new ArtificalData();
            ArtificalManager.getInstance().artificalData = data;
            }

        ArtificalManager.getInstance().refresh();



        boolean hasImage = ImageManager.getInstance().readUserData();

        if (!hasImage){
            ImageManager.getInstance().messace = new ImageMessage();
        }
    }


    public static DisplayImageOptions options;

    public static ImageLoaderConfiguration config;



    private void initImageLoader(){

        options = new DisplayImageOptions.Builder()// 开始构建, 显示的图片的各种格式
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                .cacheInMemory(true)// 开启内存缓存
                .cacheOnDisk(true) // 开启硬盘缓存
                .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少；避免使用RoundedBitmapDisplayer.他会创建新的ARGB_8888格式的Bitmap对象；
                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .displayer(new SimpleBitmapDisplayer())// 正常显示一张图片　
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型;使用.bitmapConfig(Bitmap.config.RGB_565)代替ARGB_8888;
                .considerExifParams(true)// 是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY)// 缩放级别
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//这两种配置缩放都推荐
                .build();// 构建完成（参数可以不用设置全，根据需要来配置）


        config = new ImageLoaderConfiguration.Builder(context)// 开始构建 ,图片加载配置
                .threadPriority(Thread.NORM_PRIORITY - 2)// 设置线程优先级
                .threadPoolSize(3)// 线程池内加载的数量 ;减少配置之中线程池的大小，(.threadPoolSize).推荐1-5；
                .denyCacheImageMultipleSizesInMemory()// 设置加载的图片有多样的
                .tasksProcessingOrder(QueueProcessingType.LIFO)// 图片加载任务顺序
                .memoryCache(new WeakMemoryCache())//使用.memoryCache(new WeakMemoryCache())，不要使用.cacheInMemory();
                .memoryCacheExtraOptions(480, 800) // 即保存的每个缓存文件的最大长宽
                .memoryCacheSizePercentage(60)// 图片内存占应用的60%；
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())//使用HASHCODE对UIL进行加密命名
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5 加密
                .diskCacheSize(50 * 1024 * 1024) // 缓存设置大小为50 Mb
                .diskCacheFileCount(100) // 缓存的文件数量
                .denyCacheImageMultipleSizesInMemory()// 自动缩放
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                 .memoryCacheExtraOptions(480, 800)//设置缓存图片时候的宽高最大值，默认为屏幕宽高;保存的每个缓存文件的最大长宽
                .defaultDisplayImageOptions(options)// 如果需要打开缓存机制，需要自己builde一个option,可以是DisplayImageOptions.createSimple()
                .writeDebugLogs() // Remove for release app
                .build();//构建完成（参数可以不用设置全，根据需要来配置）


                ImageLoader.getInstance().init(config);



    }


    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        WanSdkManager.userId = userId;



    }

    /**
     * 获取包名MD5
     */
    public void getAllApkPackName(Context context){

        StringBuffer sb = new StringBuffer();

        PackageManager manager = context.getPackageManager();

        List<PackageInfo> infos = manager.getInstalledPackages(0);

        for (int i =0;i<infos.size();i++){
            PackageInfo info = infos.get(i);
            sb.append(info.packageName);
            if (i!=infos.size()-1){
                sb.append(",");
            }
        }

        packetMd5 = MD5Util.lowerMD5(sb.toString());
    }





    /**
     * 单例模式
     * @return
     */
    public static WanSdkManager getInstance(Context context){
        synchronized (WanSdkManager.class){
            if (sdkManager==null){
                sdkManager = new WanSdkManager(context);
            }
        }

        return sdkManager;
    }

    public void init(String channeluid){
        this.channeluid = channeluid;
        List<String> list = ApkUtils.getAllPacket(context);

        if (PacketManager.getInstance().readUserData()){
            PacketManager.getInstance().messace.addList(list);
            PacketManager.getInstance().saveUserData();

        }else {

            PacketMessage packetMessage = new PacketMessage();
            packetMessage.addList(list);
            PacketManager.getInstance().messace = packetMessage;
            PacketManager.getInstance().saveUserData();

        }


    }


    public void initPacketData(){

        List<String> list = ApkUtils.getAllPacket(context);

        if (PacketManager.getInstance().readUserData()){
            PacketManager.getInstance().messace.addList(list);
            PacketManager.getInstance().saveUserData();

        }else {

            PacketMessage packetMessage = new PacketMessage();
            packetMessage.addList(list);
            PacketManager.getInstance().messace = packetMessage;
            PacketManager.getInstance().saveUserData();

        }

    }




    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }




    public static OSS getOss() {
        return oss;
    }

   private static String code;

    public static String getCode(){

        return code;
    }


    public static void setCode(String code) {
        WanSdkManager.code = code;
    }

    private void initOSSClient() {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(Constant.accessKeyId, Constant.accessKeySecret);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(8); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        // oss为全局变量，endpoint是一个OSS区域地址
        oss = new OSSClient(context,Constant.ENDPOINT, credentialProvider, conf);
        LogUtils.e("ZXZXZX",(oss==null)+"");

    }



    public static Context getContext() {
        return context;
    }

    /**
     * 跳转首页
     * @param activity
     */
    public static void startMainActivity(Activity activity){
        Intent intent = new Intent(activity, SortActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转
     */
    public static void startReadActivity(Activity activity){

        Intent intent = new Intent(activity, ReadActivity.class);
        activity.startActivity(intent);

    }

    /**
     * 跳转
     */
    public static void startXiaochengxuActivity(Activity activity){

        Intent intent = new Intent(activity, XiaochengxuActivity.class);
        activity.startActivity(intent);

    }

    private static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
       if (new File(binPath).exists() && isCanExecute(binPath)) {
         return true;
       }
       if (new File(xBinPath).exists() && isCanExecute(xBinPath)) {
         return true;
       }
       return false;
    }


    private static boolean isCanExecute(String filePath) {
        java.lang.Process process = null;
        try {
                process = Runtime.getRuntime().exec("ls -l " + filePath);
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String str = in.readLine();
                if (str != null && str.length() >= 4) {
                        char flag = str.charAt(3);
                        if (flag == 's' || flag == 'x')
                         return true;
            }
        } catch (IOException e) {
        e.printStackTrace();
     } finally {
         if (process != null) {
             process.destroy();
        }
     }
     return false;
    }


    /**
     * 根据包名检测是否Xpose
     * @param context
     */
    private static boolean checkXposeByPackManager(Context context){
        PackageManager packageManager=context.getPackageManager();
        List<ApplicationInfo> appliacationInfoList=packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo item:appliacationInfoList ){
            if(item.packageName.equals("de.robv.android.xposed.installer")){
                return true;
            }
            if(item.packageName.equals("com.saurik.substrate")){
                return true;
            }
        }

        return false;
    }



    private static boolean checkXposeBystacktrace(){
        try {

            throw new Exception("Deteck hook");

        } catch (Exception e) {

            int zygoteInitCallCount = 0;
            for (StackTraceElement item : e.getStackTrace()) {
                // 检测"com.android.internal.os.ZygoteInit"是否出现两次，如果出现两次，则表明Substrate框架已经安装
                if (item.getClassName().equals("com.android.internal.os.ZygoteInit")) {
                    zygoteInitCallCount++;
                    if (zygoteInitCallCount == 2) {
                        Log.wtf("HookDetection", "Substrate is active on the device.");

                        return true;
                    }

                }

                if (item.getClassName().equals("com.saurik.substrate.MS$2") && item.getMethodName().equals("invoke")) {
                    Log.wtf("HookDetection", "A method on the stack trace has been hooked using Substrate.");

                    return true;
                }

                if (item.getClassName().equals("de.robv.android.xposed.XposedBridge") && item.getMethodName().equals("main")) {
                    Log.wtf("HookDetection", "Xposed is active on the device.");

                    return true;
                }
                if (item.getClassName().equals("de.robv.android.xposed.XposedBridge") && item.getMethodName().equals("handleHookedMethod")) {
                    Log.wtf("HookDetection", "A method on the stack trace has been hooked using Xposed.");

                    return true;
                }

            }

        }


        return false;
    }


    /**
     * 是否Xpose框架
     * @return
     */
    private static boolean isXpose(){
        if (checkXposeByPackManager(context)){
            return true;
        }else {
            if (checkXposeBystacktrace()){
                return true;
            }

            return false;
        }
    }


    /**
     *获取推荐列表
     */
    public void getRecommandData(final DateCallBack callBack){

        ApiServiceManager.getDataList(0, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseAllTaskList baseTaskList = gson.fromJson(json, BaseAllTaskList.class);
                    List<TaskData> mDatas = new ArrayList<>();
                    mDatas.addAll(baseTaskList.getData().gettJob_list0());
                    initData(mDatas,baseTaskList.getData());
                    if (callBack!=null){
                        callBack.getSucceed(mDatas);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    if (callBack!=null){
                        callBack.getFail(e.getMessage());
                    }
                }


            }

            @Override
            public void onError(Throwable e) {
                if (callBack!=null){
                    callBack.getFail(e.getMessage());
                }
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                Gson gson = new Gson();
                SortData baseTaskList = gson.fromJson(json, SortData.class);
                List<TaskData> mDatas = new ArrayList<>();
                mDatas.addAll(baseTaskList.gettJob_list0());
                initData(mDatas,baseTaskList);
                if (callBack!=null){
                    callBack.getSucceed(mDatas);
                }

            }
        });


    }

    private List<TaskData> initData(List<TaskData> mDatas, SortData sortData) {
        if (mDatas == null) {
            return null;
        }

        List<TaskData> taskDatas = new ArrayList<>();

        for (int i = 0;i<mDatas.size();i++){
            TaskData taskData = mDatas.get(i);
            //屏蔽黑白名单
            for (int m = 0;m<sortData.getTii_JidAname().size();m++){
                SortBean bean = sortData.getTii_JidAname().get(m);
                if (taskData.getApkName().equals(bean.getApkName())){
                    if (taskData.getTid().equals(bean.getJobId())){//必须显示
                        taskData.setWhite(true);
                        break;
                    }/*else {//必须不显示
                            taskData.setWhite(false);
                        }*/
                }
            }

            //屏蔽渠道
            for (int n = 0;n<sortData.getTcbm_jid().size();n++){
                SortBean bean = sortData.getTcbm_jid().get(n);
                if (bean.getJobId().equals(taskData.getTid())){
                    taskData.setBlack(true);//屏蔽
                }

            }



            if (taskData.getJobMarket().equals("平台")||taskData.getJobMarket().equals("跳转")){
                if (taskData.isWhite()){
                    taskDatas.add(taskData);
                } else if ((!ApkUtils.isInList(taskData.getApkName()))&&(!taskData.isBlack())){
                    taskDatas.add(taskData);
                }
            }else {
                taskDatas.add(taskData);
            }
        }

        if (mDatas!=null){
            mDatas.clear();
            mDatas.addAll(taskDatas);
        }

        sortList(mDatas);


        return mDatas;

    }



    private void sortList(List<TaskData> mDatas){
        Collections.shuffle(mDatas);
        Collections.sort(mDatas, new Comparator<TaskData>() {
            @Override
            public int compare(TaskData o1, TaskData o2) {
                if (o1.getSort()>=o2.getSort()){
                    return -1;
                }else {
                    return 1;
                }

            }
        });



    }


    /**
     * 跳转详情页面
     * @param context
     * @param taskData
     */
    public static void startTaskDetail(Context context,TaskData taskData){
        if (taskData==null){
            return;
        }

        Nagivator.startTaskDetailActivity(context,taskData);

    }


    /**
     * 跳转签到详情
     * @param context
     * @param taskData
     */
    public static void startSignTaskDetail(Context context,TaskData taskData){
        if (taskData==null){
            return;
        }

        TaskData stepTask = new TaskData();
        stepTask.setChecktype(taskData.getcCheckType());
        stepTask.setAvailable(taskData.getAvailable());
        stepTask.setId(taskData.getPolicyId());
        stepTask.setMinstaySec(taskData.getcMinstaySec());
        stepTask.setSimpleUrl(taskData.getcExampleImgUrl());
        stepTask.setStepTitle(taskData.getStepTitle());
        stepTask.setStepNotice(taskData.getcStepNotice());
        stepTask.setStepGuide(taskData.getcStepGuide());
        //stepTask.setStepPoints(taskData.getStepPoints());

        stepTask.setStepPoints(taskData.getcStepPoints());

        Nagivator.startStayActivity(context,stepTask,taskData);

    }


    /**
     * 获取签到数据
     * @param callBack
     */
    public static void getSignData(final DateCallBack callBack){
        ApiServiceManager.getSignDataList("0", 0, new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseTaskList baseTaskList = gson.fromJson(json, BaseTaskList.class);
                    ArrayList<TaskData> mDatas = new ArrayList<>();
                    if(baseTaskList==null){
                        if (callBack!=null){
                            callBack.getFail("list==null");
                        }
                        return;
                    }
                    mDatas.addAll(baseTaskList.getTaskDatas());
                    if (callBack!=null){
                        callBack.getSucceed(mDatas);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callBack!=null){
                        callBack.getFail(e.getMessage());
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (callBack!=null){
                    callBack.getFail(e.getMessage());
                }
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                Gson gson = new Gson();
                List<TaskData> dataList = new ArrayList<>();
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(json).getAsJsonArray();

                for (JsonElement taskData:jsonArray){
                    TaskData data =  gson.fromJson(taskData,TaskData.class);
                    dataList.add(data);
                }

                ArrayList<TaskData> mDatas = new ArrayList<>();
                if(dataList==null){
                    if (callBack!=null){
                        callBack.getFail("list==null");
                    }
                    return;
                }
                mDatas.addAll(dataList);

                if (callBack!=null){
                    callBack.getSucceed(mDatas);
                }



            }
        });

    }


    /**
     * 获取小程序列表
     */
    public static void getXIaochengxuList(final DateCallBack callBack){
        ApiServiceManager.getXiaochengxu(new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {

            }

            @Override
            public void onError(Throwable e) {
                if (callBack!=null){
                    callBack.getFail(e.getMessage());
                }

            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                LogUtils.e("XCX","onDecode : "+json);
                Gson gson = new Gson();
                BaseXiaochengxu xiaochengxu =gson.fromJson(json, BaseXiaochengxu.class);

                if (xiaochengxu==null){
                    if (callBack!=null){
                        callBack.getFail("list==null");
                    }
                    return;
                }

                ArrayList<TaskData>   mDatas = new ArrayList<>();

                mDatas.addAll(xiaochengxu.getXiaochengxuList());

                if (callBack!=null){
                    callBack.getSucceed(mDatas);
                }


            }
        });
    }


    /**
     * 登录
     * @param context
     * @return
     */
    public static boolean getDeviceId(final Context context, final LoginCallBack callBack){
        boolean canUse = true;
        Map<Integer,String> map = new HashMap<>();
        String imei = "";
        if (DevicesManager.getInstance().readUserData()){
            imei = DevicesManager.getInstance().getDeviceId();
            map.put(0,DevicesManager.getInstance().messace.getImei1());
            map.put(1,DevicesManager.getInstance().messace.getImei2());
            WanSdkManager.setUserId(imei);
        }else {
            Map<String,String> stringMap = GetSystemInfoUtil.getImeiAndMeid(context);
            if (map!=null){
                map.clear();
            }
            map.put(0,stringMap.get("imei1"));
            map.put(1,stringMap.get("imei2"));

            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);

            String str = null;

            if ((stringMap.get("meid")!=null&&stringMap.get("meid").length()>13&&stringMap.get("meid").length()<16)||(map.get(0)!=null&&map.get(0).length()>13&&map.get(0).length()<16)||(map.get(1)!=null&&map.get(1).length()>13&&map.get(1).length()<16)){
                if (stringMap.get("meid")!=null){
                    if (stringMap.get("meid").length()==14){
                        str = stringMap.get("meid");
                    }else {

                        int  imei1 =  map.get(0).charAt(0);
                        int imei2 = map.get(1).charAt(0);

                        if (imei1>imei2){
                            str = map.get(1);
                        }else {
                            str = map.get(0);
                        }

                    }
                }else {
                    int  imei1 =  map.get(0).charAt(0);
                    int imei2 = map.get(1).charAt(0);
                    if (imei1>imei2){
                        str = map.get(1);
                    }else {
                        str = map.get(0);
                    }

                }
            }else {
                Toast.makeText(context,"非法设备",Toast.LENGTH_SHORT).show();


            }


            imei = str;
            LogUtils.e("Permission","false   :  "+imei);
            WanSdkManager.setUserId(imei);


            DeviceMessace messace = new DeviceMessace();
            messace.getAllData(context,telephonyManager);
            DevicesManager.getInstance().messace = messace;
            DevicesManager.getInstance().saveUserData();
        }

        ApiServiceManager.loginSDK(imei, android.os.Build.BRAND, map.get(0), map.get(1), new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                LogUtils.e("Decode","LoginSDK : onNext");
                try {
                    String json = new String(body.bytes());
                    Gson gson = new Gson();
                    BaseBean bean = gson.fromJson(json,BaseBean.class);
                    if (bean.getCode()==100||bean.getCode()==119){//成功


                    }else {//失败


                    }
                } catch (IOException e) {
                    e.printStackTrace();


                }
            }

            @Override
            public void onError(Throwable e) {
                if (callBack!=null){
                    callBack.getFail("登录失败，请检查您的网络");
                }

            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                Gson gson = new Gson();
                LoginData bean = gson.fromJson(json,LoginData.class);
                LogUtils.e("Decode","LoginSDK : "+json);
                if (baseString.getCode()==100||baseString.getCode()==119){//成功
                    if (TextUtils.isEmpty(bean.getMemo())){
                       if (callBack!=null){
                           callBack.getSucceed();
                       }
                    }else {
                        String str = bean.getAsToTime();
                        String timeFormat = TimeUtils.formatDate1(str);
                        if (callBack!=null){
                            callBack.getFail("由于您未按照要求提交图片或存在作弊行为，任务区将关停至"+timeFormat+",如有问题请联系客服！");
                        }
                    }

                }else if (baseString.getCode()==102||baseString.getCode()==103){
                    if (callBack!=null){
                        callBack.getFail("非法用户，如有问题请联系客服！");
                    }

                }else {//失败

                    if (callBack!=null){
                        callBack.getFail("登录失败");
                    }
                }
            }
        });



       return canUse;
    }

}
