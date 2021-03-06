package com.wanappsdk.utils;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.wanappsdk.WanSdkManager;

/** 阿里云上传工具类
 * Created by Administrator on 2018/8/19.
 */

public class OssUtils {
    private String bucketName;
    private String endpoint;
    private Context context;

    private ProgressCallback progressCallback;

    public OssUtils(Context context) {
        this.context = context;
        this.endpoint = Constant.ENDPOINT;
        this.bucketName = Constant.BUCKET;
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public void beginupload(Context context, String policeId,String filename, String path) {
        //通过填写文件名形成objectname,通过这个名字指定上传和下载的文件
        final String objectname = policeId+"/"+filename;
        if (objectname == null || objectname.equals("")) {
            Toast.makeText(context,"文件名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //下面3个参数依次为bucket名，Object名，上传文件路径
        PutObjectRequest put = new PutObjectRequest(bucketName, objectname, path);

        if (path == null || path.equals("")) {
            LogUtils.d("Upload","请选择图片....");
            //ToastUtils.showShort("请选择图片....");
            return;
        }

        LogUtils.d("Upload","正在上传中....");
        //ToastUtils.showShort("正在上传中....");
        // 异步上传，可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                LogUtils.d("currentSize: " ,  " totalSize: " + totalSize);
                double progress = currentSize * 1.0 / totalSize * 100.f;

                if (progressCallback != null) {
                    progressCallback.onProgressCallback(progress);
                }
            }
        });

        LogUtils.e("ZXZXZX",(WanSdkManager.getOss()==null)+"");

        @SuppressWarnings("rawtypes")
        OSSAsyncTask task = WanSdkManager.getOss().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtils.d("onSuccess","上传成功");
                if (progressCallback != null) {
                    progressCallback.onSuccess(objectname);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (progressCallback != null) {
                    progressCallback.onFails();
                }
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtils.e("UploadFailure","表示在OSS服务端发生错误");
                    LogUtils.e("ErrorCode", serviceException.getErrorCode());
                    LogUtils.e("RequestId", serviceException.getRequestId());
                    LogUtils.e("HostId", serviceException.getHostId());
                    LogUtils.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        //task.cancel(); // 可以取消任务
       // task.waitUntilFinished(); // 可以等待直到任务完成
    }



    public interface ProgressCallback {
        void onProgressCallback(double progress);

        void onSuccess(String name);
        void onFails();
    }

}
