package com.mabeijianxi.smallvideorecord2;

/**
 * Created by lenovo on 2018/6/15.
 */

public class CustromUtils {
    public static int get16And9Height(int h){
        int mh = (int) (h * ((9 * 1.0f) / 16));
        while(mh%2 == 1){
            mh = mh - 1;
        }
        return mh;
    }

    public static int get4And3Height(int h){
        int mh = (int) (h * ((3 * 1.0f) / 4));
        while(mh%2 == 1){
            mh = mh - 1;
        }
        return mh;
    }
}
