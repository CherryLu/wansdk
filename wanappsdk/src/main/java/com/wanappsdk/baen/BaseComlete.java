package com.wanappsdk.baen;

import com.google.gson.annotations.SerializedName;

public class BaseComlete extends BaseBean {



    @SerializedName("userData")
    private int completeMsg;


    public int getCompleteMsg() {
        return completeMsg;
    }
}
