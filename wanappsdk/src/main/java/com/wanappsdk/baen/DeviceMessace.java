package com.wanappsdk.baen;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wanappsdk.WanSdkManager;
import com.wanappsdk.utils.GetSystemInfoUtil;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.MD5Util;

import java.io.Serializable;
import java.util.Map;

public class DeviceMessace implements Serializable{

    private  String deviceId;

    private  String andrlid_id;

    private String meid;

    private  String imei1;

    private  String imei2;

    private  String brand;//品牌

    private  String model;//型号

    private  String id;//设备版本号

    private  String product;//产品名

    private  String  device;//设备名

    private  String hardward;//硬件

    private  String serial;//串口序列号

    private  String board;//主板名称

   // private  List<String> messages;



    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    public   void getAllData(Context context,TelephonyManager manager){

       // messages = new ArrayList<>();
        deviceId = WanSdkManager.getUserId();
        LogUtils.e("IDIDIDID","deviceId1 : "+deviceId);
     //   messages.add(deviceId);

        andrlid_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      //  messages.add(andrlid_id);
        imei1 = "000000000001";
        if (manager!=null){
            imei1 = manager.getDeviceId(0);
        }

        imei2 = "000000000000";


        if (manager!=null&&manager.getPhoneCount()>1){
            imei2 = manager.getDeviceId(1);
        }


       Map<String,String> map =  GetSystemInfoUtil.getImeiAndMeid(context);

        if (map!=null){

            imei1 = map.get("imei1");
            imei2 = map.get("imei2");
            meid = map.get("meid");
        }

        if (TextUtils.isEmpty(deviceId)){
            String str = "brand"+Build.BRAND+"meid"+meid+"imei1"+imei1+"imei2"+ imei2;
            LogUtils.e("IDIDIDID","getAllData : "+str);
            deviceId = MD5Util.lowerMD5(str);
        }/*else if (TextUtils.isEmpty(deviceId)&&manager!=null){
            deviceId = MD5Util.lowerMD5(""+"mac"+ ApiServiceManager.getMacAddr()+"inid"+ GetDeviceid.getid(context)+"bid"+ApiServiceManager.getMacid(context));
        }*/
        LogUtils.e("IDIDIDID","deviceId2 : "+deviceId);

        LogUtils.e("GetSystemInfoUtil","imei1 : "+imei1);
        LogUtils.e("GetSystemInfoUtil","imei2 : "+imei2);
        LogUtils.e("GetSystemInfoUtil","meid : "+meid);



        brand = Build.BRAND;


        model = Build.MODEL;


        id = Build.ID;


        product = Build.PRODUCT;

     //   messages.add(product);

        device = Build.DEVICE;

     //   messages.add(device);

        hardward = Build.HARDWARE;

    //    messages.add(hardward);

        serial = Build.SERIAL;

     //   messages.add(serial);

        board = Build.BOARD;

     //   messages.add(board);


    }


    public String getDeviceId() {
        return deviceId;
    }

    public String getAndrlid_id() {
        return andrlid_id;
    }

    public String getImei1() {
        return imei1;
    }

    public String getImei2() {
        return imei2;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public String getDevice() {
        return device;
    }

    public String getHardward() {
        return hardward;
    }

    public String getSerial() {
        return serial;
    }

    public String getBoard() {
        return board;
    }




    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAndrlid_id(String andrlid_id) {
        this.andrlid_id = andrlid_id;
    }

    public void setImei1(String imei1) {
        this.imei1 = imei1;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setHardward(String hardward) {
        this.hardward = hardward;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getMeid() {
        return meid;
    }

    public void setMeid(String meid) {
        this.meid = meid;
    }
}
