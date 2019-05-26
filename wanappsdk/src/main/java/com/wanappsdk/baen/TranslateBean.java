package com.wanappsdk.baen;

import android.text.TextUtils;

public class TranslateBean {

    private String available;
    private String bApk_name;
    private String bJob_logo;
    private String bJob_market;
    private String bJob_name;
    private String bTags;
    private String cCheck_type;
    private String cMinstay_sec;
    private String cStep_points;
    private String cStep_title;
    private String cStep_type;
    private String id;
    private String jobId;
    private String keyWord;
    private String stepId;
    private String cStep_notice;
    private String cStep_guide;



    private String cExample_img_url;


    public String getcStep_guide() {
        return cStep_guide;
    }

    public void setcStep_guide(String cStep_guide) {
        this.cStep_guide = cStep_guide;
    }

    public String getcStep_notice() {
        return cStep_notice;
    }

    public String getcExample_img_url() {
        return cExample_img_url;
    }

    public void setcStep_notice(String cStep_notice) {
        this.cStep_notice = cStep_notice;
    }

    public void setcExample_img_url(String cExample_img_url) {
        this.cExample_img_url = cExample_img_url;
    }

    public String getAvailable() {
        return available;
    }

    public String getbApk_name() {
        return bApk_name;
    }

    public String getbJob_logo() {
        return bJob_logo;
    }

    public String getbJob_market() {
        return bJob_market;
    }

    public String getbJob_name() {
        return bJob_name;
    }

    public String getbTags() {
        return bTags;
    }

    public int getcCheck_type() {
        if (TextUtils.isEmpty(cCheck_type)){
            return 0;
        }
        return Integer.parseInt(cCheck_type);
    }

    public long getcMinstay_sec() {
        if (TextUtils.isEmpty(cMinstay_sec)){
            return 0L;
        }
        return Long.parseLong(cMinstay_sec);
    }

    public long getcStep_points() {
        if (TextUtils.isEmpty(cStep_points)){
            return 0L;
        }
        return Long.parseLong(cStep_points);
    }

    public String getcStep_title() {
        return cStep_title;
    }

    public String getcStep_type() {
        return cStep_type;
    }

    public String getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getStepId() {
        return stepId;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public void setbApk_name(String bApk_name) {
        this.bApk_name = bApk_name;
    }

    public void setbJob_logo(String bJob_logo) {
        this.bJob_logo = bJob_logo;
    }

    public void setbJob_market(String bJob_market) {
        this.bJob_market = bJob_market;
    }

    public void setbJob_name(String bJob_name) {
        this.bJob_name = bJob_name;
    }

    public void setbTags(String bTags) {
        this.bTags = bTags;
    }

    public void setcCheck_type(int cCheck_type) {
        this.cCheck_type = cCheck_type+"";
    }

    public void setcMinstay_sec(long cMinstay_sec) {
        this.cMinstay_sec = cMinstay_sec+"";
    }

    public void setcStep_points(long cStep_points) {
        this.cStep_points = cStep_points+"";
    }

    public void setcStep_title(String cStep_title) {
        this.cStep_title = cStep_title;
    }

    public void setcStep_type(String cStep_type) {
        this.cStep_type = cStep_type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }
}
