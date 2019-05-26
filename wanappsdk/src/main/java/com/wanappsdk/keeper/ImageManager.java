package com.wanappsdk.keeper;

import android.os.Handler;

import com.wanappsdk.baen.ImageMessage;
import com.wanappsdk.utils.FileUtils;
import com.wanappsdk.utils.ObjectUtils;


/**
 * Created by 95470 on 2018/4/17.
 */

public class ImageManager {


    public ImageMessage messace;



    public void clearUserData() {
        String path = FileUtils.getAppBasePath() + "image.dat";
        ObjectUtils.saveObjectData(null, path);
    }

    /**
     * 登录的方式
     */

    // 用户是否登录
    private Handler mHandler;
    private Handler tHandler;

    private static ImageManager userManager;



    public static ImageManager getInstance(){
        if (userManager==null){
            userManager = new ImageManager();
        }
        return userManager;
    }








    public void saveUserData() {
        // TODO Auto-generated method stub
        String path = FileUtils.getAppBasePath() + "image.dat";
        ImageKeeper keeper = new ImageKeeper();
        keeper.imageMessage = messace;
        if (keeper.imageMessage != null) {
           /* CommUtils.setUserName(keeper.userData.getSmail());
            CommUtils.setUserPassword(keeper.userData.getSuserno());
            CommUtils.setThirdName(keeper.userData.getUserName());
            CommUtils.setThirdheader(keeper.userData.getHeaderuerl());*/

        }
        ObjectUtils.saveObjectData(keeper, path);
    }


    public boolean readUserData() {
        String path = FileUtils.getAppBasePath() + "image.dat";
        Object obj = FileUtils.loadObjectData(path);
        if (obj != null) {
            ImageKeeper keeper = (ImageKeeper) obj;
            this.messace = keeper.imageMessage;
            if (messace != null) {
               /* CommUtils.setUserName(userData.getSmail());
                CommUtils.setUserPassword(userData.getSuserno());
                CommUtils.setThirdName(keeper.userData.getUserName());
                CommUtils.setThirdheader(keeper.userData.getHeaderuerl());*/
               return true;

            }
            return false;
        } else {
            return false;
        }
    }



}
