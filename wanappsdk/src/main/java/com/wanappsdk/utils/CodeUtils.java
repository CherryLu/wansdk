package com.wanappsdk.utils;

/**
 * 解密工具类
 */

public class CodeUtils {



    String deCode = "yioye@sina.cn";
    //加密密匙162b1fa4bd30b53a86558b70b9eea5f9
    public static String encryptionHandle(String data, String code) {
        data.replaceAll("\r\n","");
        //code encode编码
        String req_code_en = Helper.encode(code);
        //当前code编码后字符串反转
        String code_en_re = new StringBuilder(req_code_en).reverse().toString().substring(8, 16);
        //进行MD5加密得到加密密匙
        String code_re_md5 = MD5Util.lowerMD5(code_en_re);

        String data_dec = null;
        try {
           // BASE64Decoder decoder = new BASE64Decoder();
           // byte[] bytes = decoder.decodeBuffer(data);
           // String dats = new String(bytes);
            data_dec = new DesUtil().decrypt(data, code_re_md5);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //data_dec.replaceAll("\r\n","");
        return data_dec.replaceAll("\r\n","");
    }


    public  static  boolean  checkEnglish(String   s) {
        char   c   =   s.charAt(0);
        int   i   =(int)c;
        if((i>=65&&i<=90)||(i>=97&&i<=122)) {
            return   true;
        } else {
            return   false;
        }
    }

}
