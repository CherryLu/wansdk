package com.wan.callring.bean;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */

public class ContactBean {
    String name;
    String phone;
    String sortLetters;
    String date;
    int duration;
    String type;
    public String simpleNumber;
    public String allPinyin;
    public String pinyin;//模糊搜索使用的名字字母缩写
    public int spanableType = -1;//0 表示号码，1表示拼音缩写，2表示全拼音
    public String spannableString;
    public SortToken sortToken = new SortToken();
    public List<Contact> contactList = new ArrayList<>();
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        if(phone!=null){
            this.simpleNumber=phone.replaceAll("\\-|\\s", "");
        }
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getAllPinyin() {
        return allPinyin;
    }

    public void setAllPinyin(String allPinyin) {
        this.allPinyin = allPinyin;
    }
}
