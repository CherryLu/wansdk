package com.wanappsdk.http;

import com.wanappsdk.utils.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author zys
 * @create:2018-08-08 11:07
 * @parogram:springweb.tests
 * @description:
 */

public class HttpTest {
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //conn.setRequestProperty("Content-Type","application/json;application/x-www-form-urlencoded;charset=UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }



    public  static void  httpTest(){
        String timestamp = "1534080057077";
        String code = "dfb64d85b4fa8924b81809a6f909ae1d";
        //String str_param = "timestamp="+timestamp+"&"+"code="+code;
        //发送 GET 请求
        //String s=HttpRequest.sendGet("http://www.taobao.com", null);
        //System.out.println(s);
        //http://localhost/User/taskRecSub?deviceId=10000&&jobId=1&&stepId=1&&policyId=1000004&&keyWord=1&&getPoints=30&&uploadUrl=xxxx&&timestamp=1539657634622&&code=237c297637b5f08d7f282ab89aa31dac
        //发送 POST 请求"uid="+"1000011"+"&count_end="+"20"+"&timestamp="+"1534080057077"+"&code="+"dfb64d85b4fa8924b81809a6f909ae1d";
        //String str_param = "userAddress=11111114444444&code=6d17a1855de08defd961f2c8c7d5ace2&policyId=10000001&userBankCard=111111111111&idCard=3444444466558955&userNote=4444444444&channeluid=user1&userPhone=15551283679&userName=%E5%BC%A0%E8%82%96&deviceid=862033035160561&channelId=10001&timestamp=1541927210541";
        //String sr=HttpRequest.sendPost("http://localhost:8080/User/userInfo?", str_param);
        String str_param = "code=d76fc38a22d7d4fe198a9c4bbaa8ff6f&" +
                "policyId=10000018" +
                "&uploadUrl=https%3A%2F%2Fwanappimage.oss-cn-beijing.aliyuncs.com%2F862033035160561%3D10000018%3D0-Screenshot_2018-11-11-21-50-12-327_com.jingdong.app.mall.png-340188%3Bhttps%3A%2F%2Fwanappimage.oss-cn-beijing.aliyuncs.com%2F862033035160561%3D10000018%3D1" +
                "&apkName=com.taobao.taobao" +
                "&deviceId=962033035160561" +
                "&channelId=10001" +
                "&channelUid=user1" +
                "&timestamp=1542070791580";
        String sr=HttpRequest.sendPost("http://jz.npzyy.com:8080/User/taskRecSub?", str_param);
        LogUtils.e("HTTPTEST",sr);
       // System.out.println(sr);
    }
}
