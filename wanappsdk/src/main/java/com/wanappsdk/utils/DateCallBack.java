package com.wanappsdk.utils;

import com.wanappsdk.baen.TaskData;

import java.util.List;

public interface DateCallBack {

    void getSucceed(List<TaskData> taskData);
    void getFail(String errorMsg);
}
