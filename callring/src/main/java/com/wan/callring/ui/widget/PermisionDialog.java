package com.wan.callring.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wan.callring.R;

public class PermisionDialog extends Dialog implements View.OnClickListener {

    private ImageView  check_point_private,check_point_public,check_point;
    private TextView names;

    public PermisionDialog(Context context) {
        super(context);
    }

    public PermisionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PermisionDialog(Context context, boolean cancelable,  DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permision);
        check_point_private = findViewById(R.id.check_point_private);
        check_point_public = findViewById(R.id.check_point_public);
        check_point = findViewById(R.id.check_point);

        check_point_private.setOnClickListener(this);
        check_point_public.setOnClickListener(this);
        check_point.setOnClickListener(this);

        names = findViewById(R.id.names);
    }


    private void setAll(){
        check_point_private.setImageResource(R.drawable.point_unselect);
        check_point_public.setImageResource(R.drawable.point_unselect);
        check_point.setImageResource(R.drawable.point_unselect);
    }

    /**
     * 设置字符串
     * @param dtat
     */
    public void setDtat(String dtat){
        if (names!=null){
            names.setText(dtat);
        }
    }

    private void setCheck(int  which){
        setAll();
        if (0==which){
            check_point_private.setImageResource(R.drawable.point_selected);
        }else if (1==which){
            check_point_public.setImageResource(R.drawable.point_selected);
        }else {
            check_point.setImageResource(R.drawable.point_selected);
        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.check_point_private) {
            setCheck(0);
        } else if (i == R.id.check_point_public) {
            setCheck(1);
        } else if (i == R.id.check_point) {
            setCheck(2);
        }

    }
}
