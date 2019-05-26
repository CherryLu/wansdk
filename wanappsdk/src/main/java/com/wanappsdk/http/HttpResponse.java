package com.wanappsdk.http;


import com.wanappsdk.baen.BaseString;

import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/8/9.
 */

public interface HttpResponse{
    void onNext(ResponseBody body);
    void onError(Throwable e);
    void onDecode(BaseString baseString, String json);



}
