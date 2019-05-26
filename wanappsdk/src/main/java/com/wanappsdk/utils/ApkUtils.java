package com.wanappsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.wanappsdk.keeper.PacketManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApkUtils {

    public static final int START_INSTALL = 100;

    public static final int START_APP = 101;


    /**
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean hasInstall(Context context, String packageName){

        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames.contains(packageName);
    }


    /**
     * 白名单中是否有
     * @param packageName
     * @return
     */
    public static boolean isInList(String packageName){


        List<String> packageNames = PacketManager.getInstance().messace.getPacketName();


        boolean hasIt = packageNames.contains(packageName);

        return hasIt;
    }

    /**
     * 获取全部包名
     * @param context
     * @return
     */
    public static List<String> getAllPacket(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }


        return packageNames;
    }


    /**
     * 打开APP
     * @param context
     * @param packageName
     */
    public static void startAPP(Context context, String packageName){
        try{
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        }catch(Exception e){
            Toast.makeText(context, "没有安装", Toast.LENGTH_LONG).show();
        }
    }

    public static void startUrl(Context context,String url){
        if (TextUtils.isEmpty(url)){
            Toast.makeText(context,"链接为空",Toast.LENGTH_SHORT).show();
        }else {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }

    }


    /**
     * 打开APP
     * @param context
     * @param packageName
     */
    public static void startAppForResult(Activity context, String packageName){
        try{
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivityForResult(intent,START_APP);
        }catch(Exception e){
            Toast.makeText(context, "没有安装", Toast.LENGTH_LONG).show();
        }
    }



    /**
     * 跳转APP安装页面
     * @param file
     */
    public static void installApk(Activity context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                if (context.getPackageManager().canRequestPackageInstalls()){
                   // Uri apkUri = FileProvider.getUriForFile(context, "com.wanappsdk", file);  //包名.fileprovider
                    Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileProvider", file);  //包名.fileprovider
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    context.startActivityForResult(intent,START_INSTALL);
                    return;
                }else {
                    startInstallPermissionSettingActivity(context);
                }
            }else {
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileProvider", file);
                //Uri apkUri = FileProvider.getUriForFile(context, "com.wanappsdk", file);  //包名.fileprovider
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                context.startActivityForResult(intent,START_INSTALL);
                return;
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivityForResult(intent,START_INSTALL);
            return;
        }

    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startInstallPermissionSettingActivity(Context context) {
        Uri packageURI = Uri.parse("package:" + context.getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        context.startActivity(intent);
    }





    public static String getMuchMoney(long money){

        double gold  = 0d;

        if (money>=10){
             gold  = money/10d ;
        }else {
            gold  = ((money/10d)*10000);
        }
        String str = Double.toString(gold);

        String gold_money =  str;

        if (money>=10){
           return gold_money+"万金币" ;
        }else {

            gold_money = gold_money.replaceAll("0+?$", "");//去掉后面无用的零

            gold_money = gold_money.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点

            return gold_money+"金币" ;
        }



    }


    public static String getMuchMoney(Double money){

        double gold  = 0d;

        if (money>=10){
            gold  = money/10d ;
        }else {
            gold  = ((money/10d)*10000);
        }
        String str = Double.toString(gold);


        String gold_money =  str;

        if (money>=10){
            return gold_money+"万金币" ;
        }else {

            gold_money = gold_money.replaceAll("0+?$", "");//去掉后面无用的零

            gold_money = gold_money.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点


            return gold_money+"金币" ;
        }

    }





}
