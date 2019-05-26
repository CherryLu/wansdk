package com.wanappsdk.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanappsdk.R;
import com.wanappsdk.utils.AlertHelper;

public class TipsDailog extends Dialog {




    public TipsDailog(@NonNull Context context) {
        super(context, R.style.alert_dialog);
    }




    TextView title,main_title,btn_cancel,btn_comfirm;
    ImageView close_image;
    private  AlertHelper.IAlertListener iAlertListener;


    public void setiAlertListener(AlertHelper.IAlertListener iAlertListener) {
        this.iAlertListener = iAlertListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.close_dialog);
        title = findViewById(R.id.title);
        main_title = findViewById(R.id.title);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_comfirm = findViewById(R.id.btn_comfirm);
        close_image = findViewById(R.id.close_image);
        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (iAlertListener!= null){
                    iAlertListener.cancel();
                }

            }
        });

        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (iAlertListener!=null){
                    iAlertListener.sure();
                }

            }
        });

        setCancelable(true);
        setCanceledOnTouchOutside(true);

    }





}
