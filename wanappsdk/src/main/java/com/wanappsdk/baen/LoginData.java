package com.wanappsdk.baen;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginData{


    private String memo;
    private String asToTime;


    public String getMemo() {
        return memo;
    }

    public String getAsToTime() {
        return asToTime;
    }


    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setAsToTime(String asToTime) {
        this.asToTime = asToTime;
    }
}
