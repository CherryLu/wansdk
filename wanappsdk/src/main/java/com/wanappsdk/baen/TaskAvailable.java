package com.wanappsdk.baen;

import com.google.gson.annotations.SerializedName;

public class TaskAvailable extends BaseBean {

    @SerializedName("userData")
    private CompleteMsg status;


    @SerializedName("jobID")
    private String jobID;



    public CompleteMsg getStatus() {
        return status;
    }


    public void setStatus(CompleteMsg status) {
        this.status = status;
    }


    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
}
