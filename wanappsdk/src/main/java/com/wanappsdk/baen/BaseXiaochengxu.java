package com.wanappsdk.baen;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseXiaochengxu {

    @SerializedName("list1")
    private List<TaskData> xiaochengxuList;


    public List<TaskData> getXiaochengxuList() {
        return xiaochengxuList;
    }

    public void setXiaochengxuList(List<TaskData> xiaochengxuList) {
        this.xiaochengxuList = xiaochengxuList;
    }
}
