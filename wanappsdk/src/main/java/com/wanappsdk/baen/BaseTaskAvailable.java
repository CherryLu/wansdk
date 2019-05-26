package com.wanappsdk.baen;

import com.google.gson.annotations.SerializedName;

public class BaseTaskAvailable extends BaseBean {

    @SerializedName("userData")
    private int status;



    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }
}
