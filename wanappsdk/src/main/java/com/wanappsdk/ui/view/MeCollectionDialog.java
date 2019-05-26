package com.wanappsdk.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wanappsdk.R;
import com.wanappsdk.baen.BaseBean;
import com.wanappsdk.baen.BaseString;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.http.ApiServiceManager;
import com.wanappsdk.http.HttpResponse;
import com.wanappsdk.utils.AlertHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class MeCollectionDialog extends Dialog implements View.OnClickListener{

    private LinearLayout layout1,layout2,layout3,layout4,layout5,layout6,layout7,layout8;
    private EditText name_input,card_input,phone_input,account_input,psw_input,submit_input,policyId_input,paycount_input;
    private Button submit_btn;
    private ImageView close_image;
    private TaskData taskData;
    private List<LinearLayout> layouts;

    private  AlertHelper.IAlertListener iAlertListener;
    private List<String> showIt;

    public MeCollectionDialog(@NonNull Context context,TaskData taskData,AlertHelper.IAlertListener iAlertListener) {
        super(context, R.style.alert_dialog);
        this.taskData = taskData;
        this.iAlertListener = iAlertListener;
    }

    public MeCollectionDialog(@NonNull Context context,TaskData taskData,AlertHelper.IAlertListener iAlertListener,List<String> showIt) {
        super(context, R.style.alert_dialog);
        this.taskData = taskData;
        this.iAlertListener = iAlertListener;
        this.showIt = showIt;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_dialog);
        intView();

    }

    private void intView() {
        layouts = new ArrayList<>();

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);
        layout7 = findViewById(R.id.layout7);
        layout8 = findViewById(R.id.layout8);

        layouts.add(layout1);
        layouts.add(layout2);
        layouts.add(layout3);
        layouts.add(layout4);
        layouts.add(layout5);
        layouts.add(layout6);
        layouts.add(layout7);
        layouts.add(layout8);

        close_image = findViewById(R.id.close_image);
        close_image.setOnClickListener(this);


        setCanceledOnTouchOutside(false);

        for (int i = 0;i<layouts.size();i++){
            layouts.get(i).setVisibility(View.GONE);
        }

        phone_input = findViewById(R.id.phone_input);

        name_input = findViewById(R.id.name_input);

        card_input = findViewById(R.id.card_input);

        account_input = findViewById(R.id.account_input);

        psw_input = findViewById(R.id.psw_input);

        submit_input = findViewById(R.id.submit_input);
        policyId_input = findViewById(R.id.policyId_input);

        paycount_input = findViewById(R.id.paycount_input);

        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);

        if (showIt!=null){
            for (int i = 0;i<showIt.size();i++){
                String  str = showIt.get(i);
                if ("1".equals(str)){
                    layouts.get(i).setVisibility(View.VISIBLE);
                }else {
                    layouts.get(i).setVisibility(View.GONE);
                }
            }
        }

    }

    /**
     * 验证身份证号是否符合规则
     * @param text 身份证号
     * @return
     */
    public boolean personIdValidation(String text) {
        String regx = "[0-9]{17}x";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) || text.matches(reg1) || text.matches(regex);
    }


    /**
     * 手机号号段校验，
     第1位：1；
     第2位：{3、4、5、6、7、8}任意数字；
     第3—11位：0—9任意数字
     * @param value
     * @return
     */
    public static boolean isTelPhoneNumber(String value) {
        if (value != null && value.length() == 11) {
            Pattern pattern = Pattern.compile("^1[3|4|5|6|7|8][0-9]\\d{8}$");
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }


    //验证银行卡号
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }
    //从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }




    @Override
    public void onClick(View v) {
       int id =  v.getId();
       if (id==R.id.submit_btn){
           if (layout2.getVisibility()==View.VISIBLE){
               String name = name_input.getText().toString();
               if (TextUtils.isEmpty(name)){
                   Toast.makeText(getContext(),"姓名栏不能为空",Toast.LENGTH_SHORT).show();
                   return;
               }
           }

        if (layout3.getVisibility()==View.VISIBLE){
            String card = card_input.getText().toString();
            if (TextUtils.isEmpty(card)){
                Toast.makeText(getContext(),"身份证号码不能为空",Toast.LENGTH_SHORT).show();
                return;
            }else if (!personIdValidation(card)){
                Toast.makeText(getContext(),"请输入正确的身份证号码",Toast.LENGTH_SHORT).show();
                return;
            }
        }

            if (layout1.getVisibility()==View.VISIBLE){
                String phone = phone_input.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(getContext(),"手机号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }else if (!isTelPhoneNumber(phone)){
                    Toast.makeText(getContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (layout4.getVisibility()==View.VISIBLE){
                String bankcard = account_input.getText().toString();
                if (TextUtils.isEmpty(bankcard)){
                    Toast.makeText(getContext(),"账号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            if (layout5.getVisibility()==View.VISIBLE){
                String address = psw_input.getText().toString();
                if (TextUtils.isEmpty(address)){
                    Toast.makeText(getContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
            }



            if (layout6.getVisibility()==View.VISIBLE){
                String remark = submit_input.getText().toString();
                if (TextUtils.isEmpty(remark)){
                    Toast.makeText(getContext(),"邀请码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            if (layout7.getVisibility()==View.VISIBLE){
                String policyId = policyId_input.getText().toString();
                if (TextUtils.isEmpty(policyId)){
                    Toast.makeText(getContext(),"订单号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            if (layout8.getVisibility()==View.VISIBLE){
                String paycount = paycount_input.getText().toString();
                if (TextUtils.isEmpty(paycount)){
                    Toast.makeText(getContext(),"支付账号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

            }

           submitInfor(phone_input.getText().toString(),name_input.getText().toString(),card_input.getText().toString(),account_input.getText().toString(),psw_input.getText().toString(),submit_input.getText().toString(),policyId_input.getText().toString(),paycount_input.getText().toString());

       }

       if (id==R.id.close_image){
           dismiss();

       }
    }


    /**
     *  提交
     */

    private void submitInfor(String isuserphone,String name,String isuseridcard,String isuseraccount,String isuserpasswd,String isuserincode,String isuserorder,String isuserweizhi ){
        ApiServiceManager.uploadInfo(taskData.getTid(),isuserphone, name, isuseridcard, isuseraccount, isuserpasswd, isuserincode, isuserorder, isuserweizhi,new HttpResponse() {
            @Override
            public void onNext(ResponseBody body) {
                dismiss();
                try {
                    String  json = body.string();
                    Gson gson = new Gson();
                    BaseBean bean = gson.fromJson(json,BaseBean.class);
                    if (110==bean.getCode()){
                        iAlertListener.sure();
                    }else {
                        iAlertListener.cancel();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    iAlertListener.cancel();
                }
            }

            @Override
            public void onError(Throwable e) {
                dismiss();
                iAlertListener.cancel();
            }

            @Override
            public void onDecode(BaseString baseString, String json) {
                iAlertListener.cancel();
                Gson gson = new Gson();
                BaseBean bean = gson.fromJson(json,BaseBean.class);
                if (110==bean.getCode()){
                    iAlertListener.sure();
                }else {
                    iAlertListener.cancel();
                }
            }
        });
    }
}
