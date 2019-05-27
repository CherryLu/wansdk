package com.wan.callring.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.wan.callring.R;
import com.wan.callring.bean.Contact;
import com.wan.callring.utils.Preferences;
import com.wan.callring.utils.ScreemUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 同一个人有多个号码的情况选择拨打
 */

public class ChooseCallNumDialog extends Dialog implements View.OnClickListener {
    TextView textView_name;
    ListView listview;
    Context context;
    List<Contact> list =  new ArrayList<>();
    AddressBookAdapterDailog addressBookAdapterDailog;
    public interface onItemSelectedListener {
        void onSelected(int item);
    }


    public ChooseCallNumDialog(Context context) {
        super(context, R.style._dialog_bg);
        this.context = context;
        initUI();
        setCanceledOnTouchOutside(true);
    }



    @Override
    public void show() {
        getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style._dialog_animation);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ScreemUtils.getScreenWidth(getContext());
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
        super.show();

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    private void initUI() {
        setContentView(R.layout.dialog_choose_callnum);
        textView_name = (TextView)findViewById(R.id.textView_name);
        listview = (ListView)findViewById(R.id.listview);
        addressBookAdapterDailog = new AddressBookAdapterDailog(context,list);
        listview.setAdapter(addressBookAdapterDailog);
        findViewById(R.id.rl_close).setOnClickListener(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String myMobile = Preferences.getString(Preferences.UserMobile,null);
                if(TextUtils.isEmpty(myMobile)){
                    //未登录
                    return;
                }
                dismiss();
                if(onChooseNumListener!=null){
                    onChooseNumListener.onChoosed(list.get(i).getDisplay_name(),list.get(i).getNumber().replace(" ",""));
                }
            }
        });
    }

    public interface OnChooseNumListener{
        void onChoosed(String name, String phone);
    }
    OnChooseNumListener onChooseNumListener;
    public void setOOnChooseNumListener(OnChooseNumListener oOnChooseNumListener){
     this.onChooseNumListener = oOnChooseNumListener;
    }

    public void setListview(List<Contact> list){
        this.list = list;
        addressBookAdapterDailog.updateListView(list);
    }

    public void setTextView(String text){
        textView_name.setText(text);
    }


    private onItemSelectedListener listener;

    public void setOnSelectListener(onItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_close) {
            dismiss();
        }
    }

    ChooseCallTypeDialog chooseCallTypeDialog;
    String name;
    String phone;
    public void showChooseCallTypeDialog(String name, String phone){
        this.name = name;
        this.phone = phone;
        if (chooseCallTypeDialog==null){
            chooseCallTypeDialog = new ChooseCallTypeDialog(getContext());
            chooseCallTypeDialog.setOnSelectListener(onItemSelectedListener);
        }
        chooseCallTypeDialog.setTextView(name+"["+phone+"]");
        chooseCallTypeDialog.show();
    }

    private ChooseCallTypeDialog.onItemSelectedListener onItemSelectedListener = new ChooseCallTypeDialog.onItemSelectedListener() {
        @Override
        public void onSelected(int item) {
            switch (item){
                case 0:

                    break;
                case 1:

                    break;
            }
        }
    };
}
