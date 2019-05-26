package com.wanappsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class ArtificalUtils {

    public static void storeData(Context context,int count){

        SharedPreferences preferences = context.getSharedPreferences("ARTIFICAL",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Date date = new Date();
        editor.putInt("count",count);
        editor.putLong("data",date.getTime());
        editor.commit();

    }


    /**
     * 获取文章次数
     * @param context
     * @return
     */
    public static int getStoreId(Context context){

        SharedPreferences preferences = context.getSharedPreferences("ARTIFICAL",Context.MODE_PRIVATE);

        return preferences.getInt("count",0);

    }


    /**
     * 获取上次存储时间
     * @param context
     * @return
     */
    public static Long getStoreTime(Context context){

        SharedPreferences preferences = context.getSharedPreferences("ARTIFICAL",Context.MODE_PRIVATE);

        return preferences.getLong("data",0L);

    }




}
