package com.wan.callring.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.wan.callring.R;

public class PermisionDialog extends Dialog implements View.OnClickListener {

    private ImageView  check_point_private,check_point_public,check_point;

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
    }


    private void setAll(){
        check_point_private.setImageResource(R.drawable.point_unselect);
        check_point_public.setImageResource(R.drawable.point_unselect);
        check_point.setImageResource(R.drawable.point_unselect);
    }

    private void setCheck(int  which){
        setAll();
        if (0==which){
            check_point_private.setImageResource(R.drawable.point_selected);
        }else if (1==which){

        }else {

        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.check_point_private) {

        } else if (i == R.id.check_point_public) {

        } else if (i == R.id.check_point) {

        }

    }
}
