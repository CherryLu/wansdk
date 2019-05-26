package com.wanappsdk.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.utils.CodeUtils;
import com.wanappsdk.utils.LogUtils;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;


/**
 * Created by Administrator on 2018/8/9.
 */

public class HttpCallBack implements Observer<ResponseBody> {
    private HttpResponse response;
    private String code;

    public void setCode(String code) {
        this.code = code;
    }

    public HttpCallBack(HttpResponse response) {
        this.response = response;

    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(ResponseBody value) {
        if (TextUtils.isEmpty(code)){
            response.onNext(value);
        }else {
            try {
                String json = new String(value.bytes());
                if (TextUtils.isEmpty(json)){
                    response.onError(new Exception("数据为空"));
                }else {
                    Gson gson = new Gson();
                    BaseString bean = gson.fromJson(json,BaseString.class);
                    if (bean!=null){
                        String decode = CodeUtils.encryptionHandle(bean.getUserData(), code);
                        LogUtils.longLog("Decode",decode);
                        if (TextUtils.isEmpty(decode)){
                            response.onError(new Exception("数据为空"));
                        }else {
                            response.onDecode(bean,decode);
                        }
                    }else {
                        response.onError(new Exception("数据为空"));

                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
                response.onError(e);
            }

        }

    }






    @Override
    public void onError(Throwable e) {
        if (response!=null){
            response.onError(e);
        }
    }

    @Override
    public void onComplete() {

    }
}
