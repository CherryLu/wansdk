package com.wanappsdk.baen;

import com.google.gson.annotations.SerializedName;

public class BaseSignBean {


    @SerializedName("userData")
    private SignBean signBean;


    public SignBean getSignBean() {
        return signBean;
    }
}
