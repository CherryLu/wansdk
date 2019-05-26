package com.wanappsdk.baen;

public class CompleteMsg {

    private int code;

    private String jobID;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return jobID;
    }


    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.jobID = msg;
    }
}
