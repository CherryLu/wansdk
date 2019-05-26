package com.wanappsdk.keeper;

import android.os.Handler;

import com.wanappsdk.baen.DeviceMessace;
import com.wanappsdk.utils.FileUtils;
import com.wanappsdk.utils.LogUtils;
import com.wanappsdk.utils.ObjectUtils;


/**
 * Created by 95470 on 2018/4/17.
 */

public class DevicesManager {


    public DeviceMessace messace;



    public void clearUserData() {
        String path = FileUtils.getAppBasePath() + "userKeeper.dat";
        ObjectUtils.saveObjectData(null, path);
    }

    /**
     * 登录的方式
     */

    // 用户是否登录
    private Handler mHandler;
    private Handler tHandler;

    private static DevicesManager userManager;



    public static DevicesManager getInstance(){
        if (userManager==null){
            userManager = new DevicesManager();
        }
        return userManager;
    }








    public void saveUserData() {
        // TODO Auto-generated method stub
        String path = FileUtils.getAppBasePath() + "userKeeper.dat";
        DeviceKeeper keeper = new DeviceKeeper();
        keeper.messace = messace;
        LogUtils.e("IDIDIDID","saveUserData : "+messace.getDeviceId());
        if (keeper.messace != null) {
           /* CommUtils.setUserName(keeper.userData.getSmail());
            CommUtils.setUserPassword(keeper.userData.getSuserno());
            CommUtils.setThirdName(keeper.userData.getUserName());
            CommUtils.setThirdheader(keeper.userData.getHeaderuerl());*/

        }
        ObjectUtils.saveObjectData(keeper, path);
    }

    public String getDeviceId(){
        if (messace!=null){
            return messace.getDeviceId();
        }

        return "";
    }

    public boolean readUserData() {
        String path = FileUtils.getAppBasePath() + "userKeeper.dat";
        Object obj = FileUtils.loadObjectData(path);
        if (obj != null) {
            DeviceKeeper keeper = (DeviceKeeper) obj;
            this.messace = keeper.messace;
            if (messace != null) {
                if (messace.getDevice().length()>13&&messace.getDevice().length()<16){
                    return true;
                }
               return false;

            }
            return false;
        } else {
            return false;
        }
    }




    private String userName;
    private String headerurl;

    private DevicesManager() {

    }












}
