package com.mabeijianxi.smallvideorecord2;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by zgb on 2018/8/14.
 */

public class RecordUtil {
    //获取最接近的数字
    public static int getCloselyMun(int nearNum,List<Camera.Size> list){
        // 接近的数字
        nearNum = 6;
// 差值实始化
        int diffNum = Math.abs(list.get(0).height - nearNum);
// 最终结果
        int result = list.get(0).height;
        for (Camera.Size size : list) {
            int diffNumTemp = Math.abs(size.height - nearNum);
            if (diffNumTemp < diffNum)
            {
                diffNum = diffNumTemp;
                result = size.height;
            }
        }
        return result;
    }
}
