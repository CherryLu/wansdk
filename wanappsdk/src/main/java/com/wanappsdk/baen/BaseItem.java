package com.wanappsdk.baen;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/7/25.
 */

public class BaseItem implements Serializable{


    public static int ITEM_SMALL_PIC = 1;//推荐列表
    public static int ITEM_SMALL_PIC_WEB = 2;//推荐列表
    public static int ITEM_XIAOCHENGXU = 3;//小程序列表
    private int type; //视图类型 0

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    private TaskData topMessage;//用户信息  公告信息

    public TaskData getTopMessage() {
        return topMessage;
    }

    public void setTopMessage(TaskData topMessage) {
        this.topMessage = topMessage;
    }

    //列表数据
    private List<TaskData> dataList;


    public List<TaskData> getDataList() {
        return dataList;
    }


    public void setDataList(List<TaskData> dataList) {
        this.dataList = dataList;
    }


    private List<TaskData> firstData;

    public List<TaskData> getFirstData() {
        return firstData;
    }

    public void setFirstData(List<TaskData> firstData) {
        this.firstData = firstData;
    }
}
