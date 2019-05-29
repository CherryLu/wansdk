package com.wan.callring.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Author：Cxb on 2016/3/17 09:58
 */
public class PhoneLocationUtils {

    private static final String TAG = "PhoneLocationUtils";
    public final static String DB_NAME = "mobileLocation.db";
    public final static String TABLE_NAME = "Dm_Mobile";
    public final static String FIELD_NUMBER="MobileNumber";
    public final static String FIELD_TYPE="MobileType";
    public final static String FIELD_AREA="MobileArea";
    public final static String FIELD_AREA_CODE="AreaCode";
    public static boolean isCopyFinished=false;

    public static String[] getPhoneLocation(Context context, String number) {

        File filePath=context.getExternalFilesDir(null);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        File file = new File(filePath, DB_NAME);
        String[] result=new String[2];
        result[0]="未知归属地";
        result[1]="";
        if (isCopyFinished) {

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);

            if (number.length() == 11 && !number.startsWith("0")) {
                Cursor cursor = db.query(TABLE_NAME, null, FIELD_NUMBER+"=?", new String[]{number.substring(0, 7)}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    result[0] = cursor.getString(cursor.getColumnIndex(FIELD_AREA));
                    result[1] = cursor.getString(cursor.getColumnIndex(FIELD_TYPE));
                    cursor.close();
                }
            } else {
                if(number.startsWith("0") && number.length()>4){
                    Cursor cursor = db.query(TABLE_NAME, null, FIELD_AREA_CODE+"=? or "+FIELD_AREA_CODE+"=?", new String[]{number.substring(0, 3),number.substring(0, 4)}, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        result[0] = cursor.getString(cursor.getColumnIndex(FIELD_AREA));
                        cursor.close();
                    }
                }
            }
            db.close();
        }
        return result;
    }

    public static boolean copyDB(Context context) {
        try {
            File filePath=context.getExternalFilesDir(null);
            if(!filePath.exists()){
                filePath.mkdirs();
            }
            File file = new File(filePath, DB_NAME);
            if (!file.exists()) {
                InputStream is = context.getAssets().open(DB_NAME);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len=0;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
            }
            isCopyFinished=true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            isCopyFinished=false;
            return false;
        }
    }

    public static void serchPhone(final String s, final Handler myHandler) {
        new Thread() {
            public void run() {
                String path = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + s;
                try {
                    URL url = new URL(path);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 提交模式
                    connection.setRequestMethod("GET");
                    //读取超时 单位毫秒
                    connection.setReadTimeout(5000);
                    //连接超时 单位毫秒
                    connection.setConnectTimeout(5000);


                    //获取
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        String string = streamToString(inputStream, "gbk");

                        String json = string.substring(string.indexOf("{"));
                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = json;
                        myHandler.sendMessage(message);
                        Log.e("serchPhone", json);
                    }
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ;
        }.start();
    }

    private static String streamToString(InputStream inputStream, String charset) {
        try {
            //输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            //得到缓冲流
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String s = null;
            StringBuilder builder = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                builder.append(s);
            }
            reader.close();
            return builder.toString();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }
}
