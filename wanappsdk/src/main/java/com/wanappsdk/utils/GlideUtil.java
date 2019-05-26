package com.wanappsdk.utils;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanappsdk.WanSdkManager;

/**
 * Created by 95470 on 2018/7/27.
 */

public class GlideUtil {
    //默认加载
    public static void loadImageView(Context mContext, String path, ImageView mImageView) {

        ImageLoader.getInstance().displayImage(path,mImageView, WanSdkManager.options);

       /* RequestBuilder<Drawable> builder = Glide.with(mContext).load(path);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.priority(Priority.HIGH);

        builder.apply(requestOptions);
        builder.into(mImageView);*/

    }


    public static void loadImageViewFromSD(String path, ImageView mImageView){
        ImageLoader.getInstance().displayImage(ImageDownloader.Scheme.FILE.wrap(path),mImageView, WanSdkManager.options);

    }

    public static void loadBitMapImageView(final Context mContext, final String path, final ImageView mImageView) {

        ImageLoader.getInstance().displayImage(path,mImageView, WanSdkManager.options);

      /*  RequestBuilder<Bitmap> builder = Glide.with(mContext).asBitmap().load(path);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.priority(Priority.HIGH);

        builder.apply(requestOptions);
        builder.into(mImageView);*/

       // Glide.with(mContext).load(path).asBitmap().placeholder(R.mipmap-hdpi.default_image).error(R.mipmap-hdpi.default_image).into(mImageView);
    }



    //清理磁盘缓存
    public static void GuideClearDiskCache(Context mContext) {
        //理磁盘缓存 需要在子线程中执行
    }

    //清理内存缓存
    public static void GuideClearMemory(Context mContext) {
        //清理内存缓存  可以在UI主线程中进行
    }

}
