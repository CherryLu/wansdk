package com.wan.callring.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wan.callring.R;
import com.wan.callring.utils.ScreemUtils;


/**
 * 选择拨打类型（app内部，系统外部）
 */

public class ChooseCallTypeDialog extends Dialog implements View.OnClickListener {
    TextView textView_name;
    public interface onItemSelectedListener {
        void onSelected(int item);
    }


    public ChooseCallTypeDialog(Context context) {
        super(context, R.style._dialog_bg);
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
        super.show();

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    private void initUI() {
        setContentView(R.layout.dialog_choose_calltype);
        textView_name = (TextView)findViewById(R.id.textView_name);
        findViewById(R.id.textView1).setOnClickListener(this);
        findViewById(R.id.textView2).setOnClickListener(this);
        findViewById(R.id.rl_close).setOnClickListener(this);
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
        int i = v.getId();
        if (i == R.id.textView1) {
            if (listener != null) {
                listener.onSelected(0);
            }
            dismiss();
        } else if (i == R.id.textView2) {
            if (listener != null) {
                listener.onSelected(1);
            }
            dismiss();
        } else if (i == R.id.rl_close) {
            dismiss();
        }
    }
}
