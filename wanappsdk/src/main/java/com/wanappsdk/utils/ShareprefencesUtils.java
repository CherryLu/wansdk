package com.wanappsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareprefencesUtils {

    public static void storeData(Context context,String id,String pics){

        SharedPreferences preferences = context.getSharedPreferences("TASK",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ID",id);
        editor.putString("PIC",pics);
        editor.commit();

    }


    /**
     * 获取ID
     * @param context
     * @return
     */
    public static String getStoreId(Context context){

        SharedPreferences preferences = context.getSharedPreferences("TASK",Context.MODE_PRIVATE);

        return preferences.getString("ID","0");

    }


    /**
     * 获取图片
     * @param context
     * @return
     */
    public static String getStorePics(Context context){

        SharedPreferences preferences = context.getSharedPreferences("TASK",Context.MODE_PRIVATE);

        return preferences.getString("PIC","0");

    }




}
