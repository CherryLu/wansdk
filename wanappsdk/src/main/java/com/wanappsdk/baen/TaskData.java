package com.wanappsdk.baen;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskData implements Serializable {

    //视图类型
    private int type;

    //包名
   private String apkName;
    //名字
   private String appName;
    //剩余任务数字
   private String available;
    //
   private String contract;

   private String aId;

   private String id;

   private String  jobDesc;
    //来源
   private String jobFrom;
    //来源
   private String jobLogo;
    //市场
   private String jobMarket;
    //任务名称
   private String jobName;
    //任务标题
   private String jobSubtitle;
    //赚钱
   private String keyWord;
    //操作者
   private String operator;
    //排序
   private String sort;
    //状态
   private String status;
    //步骤
   private String stepId;
    //分步分数
   private String stepPoints ;
    //tag
   private String tags;

   private String jobPlay;

   private String policyId;

   private String channelId;

   private String stepType;

   private int checkType;

   private String stepTitle;

   private String stepName;

   private String minstaySec;

   private String  exampleImgUrl;

   private String stepGuide;

   private String stepNotice;

   private String downUrl;

   private String jobShare;




   private String billStep;
   private String hwBrand;
   private String jobBusiness;
   private int cCheckType;
   private String cExampleImgUrl;
   private String cMinstaySec;
   private String cStepGuide;
   private String cStepNotice;
   private String cStepPoints;

   private String comment;
   private String commentMode;


   private String points;

    public Double getPoints() {
        if (TextUtils.isEmpty(points)){
            return 0d;
        }
        return Double.parseDouble(points);
    }

    public void setPoints(String points) {
        this.points = points;
    }


    //阅读相关


    private String readName;
    private String readSubtitle;
    private String readFrom;
    private String readBusiness;
    private String billType;
    private String createTime;
    private String readDesc;
    private String readLogo;
    private String readUrl;
    private String urlFormat;
    private String timesaday;
    private String readInterval;
    private String onlineTime;
    private String offlineTime;
    private String billPrice;
    private String read_interval;

    private String jobSign;

    private String playPoints;

    private String sharePoints;

    private String signPoints;

    private String startTool;

    private String updateTime;

    private String wechatapp;

    public void setWechatapp(String wechatapp) {
        this.wechatapp = wechatapp;
    }

    public String getWechatapp() {
        return wechatapp;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setStartTool(String startTool) {
        this.startTool = startTool;
    }

    public String getStartTool() {
        return startTool;
    }

    public String getSignPoints() {
        return signPoints;
    }

    public void setSignPoints(String signPoints) {
        this.signPoints = signPoints;
    }

    public String getSharePoints() {
        return sharePoints;
    }

    public void setSharePoints(String sharePoints) {
        this.sharePoints = sharePoints;
    }

    public Long getPlayPoints() {
        if (TextUtils.isEmpty(playPoints)){
            return 0L;
        }
        return Long.parseLong(playPoints);
    }

    public void setPlayPoints(String playPoints) {
        this.playPoints = playPoints;
    }



    public String getJobSign() {
        return jobSign;
    }

    public void setJobSign(String jobSign) {
        this.jobSign = jobSign;
    }

    public int getRead_interval() {
        if (TextUtils.isEmpty(read_interval)){
            return 0;
        }
        return Integer.parseInt(read_interval);
    }

    public void setRead_interval(int read_interval) {
        this.read_interval = read_interval+"";
    }

    public void setStepPoints(String stepPoints) {
        this.stepPoints = stepPoints;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
    }

    public void setMinstaySec(String minstaySec) {
        this.minstaySec = minstaySec;
    }

    public void setExampleImgUrl(String exampleImgUrl) {
        this.exampleImgUrl = exampleImgUrl;
    }

    public void setcMinstaySec(String cMinstaySec) {
        this.cMinstaySec = cMinstaySec;
    }

    public void setcStepPoints(String cStepPoints) {
        this.cStepPoints = cStepPoints;
    }

    public void setReadName(String readName) {
        this.readName = readName;
    }

    public void setReadSubtitle(String readSubtitle) {
        this.readSubtitle = readSubtitle;
    }

    public void setReadFrom(String readFrom) {
        this.readFrom = readFrom;
    }

    public void setReadBusiness(String readBusiness) {
        this.readBusiness = readBusiness;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setReadDesc(String readDesc) {
        this.readDesc = readDesc;
    }

    public void setReadLogo(String readLogo) {
        this.readLogo = readLogo;
    }

    public void setReadUrl(String readUrl) {
        this.readUrl = readUrl;
    }

    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
    }

    public void setTimesaday(String timesaday) {
        this.timesaday = timesaday;
    }

    public void setReadInterval(int readInterval) {
        this.readInterval = readInterval+"";
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public void setOfflineTime(String offlineTime) {
        this.offlineTime = offlineTime;
    }

    public void setBillPrice(String billPrice) {
        this.billPrice = billPrice;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public String getReadName() {
        return readName;
    }

    public String getReadSubtitle() {
        return readSubtitle;
    }

    public String getReadFrom() {
        return readFrom;
    }

    public String getReadBusiness() {
        return readBusiness;
    }

    public String getBillType() {
        return billType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getReadDesc() {
        return readDesc;
    }

    public String getReadLogo() {
        return readLogo;
    }

    public String getReadUrl() {
        return readUrl;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    public int getTimesaday() {
        if (TextUtils.isEmpty(timesaday)){
            return 1;
        }
        return Integer.parseInt(timesaday);
    }

    public int getReadInterval() {
        if (!TextUtils.isEmpty(readInterval)){
          return  Integer.parseInt(readInterval);
        }
        return 0;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public String getOfflineTime() {
        return offlineTime;
    }

    public Double getBillPrice() {
        if (TextUtils.isEmpty(billPrice)){
            return 0d;
        }
        return Double.parseDouble(billPrice);
    }

    public String getCreditCard() {
        return creditCard;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentMode() {
        return commentMode;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentMode(String commentMode) {
        this.commentMode = commentMode;
    }

    public long getcStepPoints() {
        if (TextUtils.isEmpty(cStepPoints)){
            return 0L;
        }
        return Long.parseLong(cStepPoints);
    }

    public void setcStepPoints(long cStepPoints) {
        this.cStepPoints = cStepPoints+"";
    }

    public String getcStepNotice() {
        return cStepNotice;
    }

    public void setcStepNotice(String cStepNotice) {
        this.cStepNotice = cStepNotice;
    }

    public String getcStepGuide() {
        return cStepGuide;
    }

    public void setcStepGuide(String cStepGuide) {
        this.cStepGuide = cStepGuide;
    }


    public long getcMinstaySec() {
        if (!TextUtils.isEmpty(cMinstaySec)){
            return Long.parseLong(cMinstaySec);
        }
        return 0L;
    }

    public void setcMinstaySec(long cMinstaySec) {
        this.cMinstaySec = cMinstaySec+"";
    }

    public String getcExampleImgUrl() {
        return cExampleImgUrl;
    }

    public void setcExampleImgUrl(String cExampleImgUrl) {
        this.cExampleImgUrl = cExampleImgUrl;
    }

    public int getcCheckType() {
        return cCheckType;
    }

    public void setcCheckType(int cCheckType) {
        this.cCheckType = cCheckType;
    }

    public void setBillStep(String billStep) {
        this.billStep = billStep;
    }

    public void setHwBrand(String hwBrand) {
        this.hwBrand = hwBrand;
    }

    public void setJobBusiness(String jobBusiness) {
        this.jobBusiness = jobBusiness;
    }

    public String getBillStep() {
        return billStep;
    }

    public String getHwBrand() {
        return hwBrand;
    }

    public String getJobBusiness() {
        return jobBusiness;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getStepGuide() {
        return stepGuide;
    }

    public String getStepNotice() {
        return stepNotice;
    }

    public void setStepGuide(String stepGuide) {
        this.stepGuide = stepGuide;
    }

    public void setStepNotice(String stepNotice) {
        this.stepNotice = stepNotice;
    }

    public String getSimpleUrl() {
        return exampleImgUrl;
    }

    public void setSimpleUrl(String simpleUrl) {
        this.exampleImgUrl = simpleUrl;
    }

    public long getMinstaySec() {
        if (TextUtils.isEmpty(minstaySec)){
            return 0L;
        }
        return Long.parseLong(minstaySec);
    }

    public void setMinstaySec(long minstaySec) {
        this.minstaySec = minstaySec+"";
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public void setStepTitle(String stepTitle) {
        this.stepTitle = stepTitle;
    }

    public String getStepTitle() {
        return stepTitle;
    }

    public int getChecktype() {
        return checkType;
    }

    public void setChecktype(int checktype) {
        this.checkType = checktype;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    private boolean isMaeket = true;

    public boolean isMaeket() {
        return isMaeket;
    }

    public void setMaeket(boolean maeket) {
        this.isMaeket = maeket;
    }

    public String getApkName() {
        return apkName;
    }

    public String getAppName() {
        return appName;
    }

    public String getAvailable() {
        return available;
    }

    public String getContract() {
        return contract;
    }

    public String getId() {
        return aId;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public String getJobFrom() {
        return jobFrom;
    }

    public String getJobLogo() {
        return jobLogo;
    }

    public String getJobMarket() {
        return jobMarket;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobSubtitle() {
        return jobSubtitle;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getOperator() {
        return operator;
    }

    public int getSort() {
        if (TextUtils.isEmpty(sort)){
            return 999;
        }else {
            return Integer.parseInt(sort);
        }

    }

    public String getStatus() {
        return status;
    }

    public String getStepId() {
        return stepId;
    }

    public long getStepPoints() {
        if (TextUtils.isEmpty(stepPoints)){
            return 0L;
        }
        return Long.parseLong(stepPoints);
    }

    public String getTags() {
        return tags;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public void setId(String id) {
        this.aId = id;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public void setJobFrom(String jobFrom) {
        this.jobFrom = jobFrom;
    }

    public void setJobLogo(String jobLogo) {
        this.jobLogo = jobLogo;
    }

    public void setJobMarket(String jobMarket) {
        this.jobMarket = jobMarket;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobSubtitle(String jobSubtitle) {
        this.jobSubtitle = jobSubtitle;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public void setStepPoints(long stepPoints) {
        this.stepPoints = stepPoints+"";
    }

    public void setTags(String tags) {
        this.tags = tags;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }


    public String getTid() {
        return id;
    }


    public void setTid(String tid){
        this.id = tid;
    }




    private boolean  isWhite;

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }


    private boolean  isBlack;

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    //是否收集
    private String creditCard;
    //是否检测安装
    private String need_install;

    public String getCredit_card() {
        return creditCard;
    }

    public String getNeed_install() {
        return need_install;
    }


    public void setCredit_card(String credit_card) {
        this.creditCard = credit_card;
    }

    public void setNeed_install(String need_install) {
        this.need_install = need_install;
    }

    public String getJobPlay() {
        return jobPlay;
    }

    public String getJobShare() {
        return jobShare;
    }

    public void setJobShare(String jobShare) {
        this.jobShare = jobShare;
    }


}
