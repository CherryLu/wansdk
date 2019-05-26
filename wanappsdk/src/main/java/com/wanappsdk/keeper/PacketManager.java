package com.wanappsdk.keeper;

import android.os.Handler;

import com.wanappsdk.baen.PacketMessage;
import com.wanappsdk.utils.FileUtils;
import com.wanappsdk.utils.ObjectUtils;


/**
 * Created by 95470 on 2018/4/17.
 */

public class PacketManager {


    public PacketMessage messace;



    public void clearUserData() {
        String path = FileUtils.getAppBasePath() + "packet.dat";
        ObjectUtils.saveObjectData(null, path);
    }

    /**
     * 登录的方式
     */

    // 用户是否登录
    private Handler mHandler;
    private Handler tHandler;

    private static PacketManager userManager;



    public static PacketManager getInstance(){
        if (userManager==null){
            userManager = new PacketManager();
        }
        return userManager;
    }








    public void saveUserData() {
        // TODO Auto-generated method stub
        String path = FileUtils.getAppBasePath() + "packet.dat";
        PacketKeeper keeper = new PacketKeeper();
        keeper.packetMessage = messace;
        if (keeper.packetMessage != null) {
           /* CommUtils.setUserName(keeper.userData.getSmail());
            CommUtils.setUserPassword(keeper.userData.getSuserno());
            CommUtils.setThirdName(keeper.userData.getUserName());
            CommUtils.setThirdheader(keeper.userData.getHeaderuerl());*/

        }
        ObjectUtils.saveObjectData(keeper, path);
    }


    public boolean readUserData() {
        String path = FileUtils.getAppBasePath() + "packet.dat";
        Object obj = FileUtils.loadObjectData(path);
        if (obj != null) {
            PacketKeeper keeper = (PacketKeeper) obj;
            this.messace = keeper.packetMessage;
            if (messace != null) {
               return true;

            }
            return false;
        } else {
            return false;
        }
    }















}
