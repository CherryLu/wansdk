package com.wanappsdk.ui.viewitem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanappsdk.R;
import com.wanappsdk.WanSdkManager;
import com.wanappsdk.baen.BaseItem;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.TaskClickCallBack;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;


/**
 * Created by Administrator on 2018/7/25.
 */

public class WebSmallPicItem implements ItemViewDelegate<TaskData> {


    TaskClickCallBack taskClickCallBack;

    public WebSmallPicItem(TaskClickCallBack taskClickCallBack) {
        this.taskClickCallBack = taskClickCallBack;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_small_pic;
    }

    @Override
    public boolean isForViewType(TaskData item, int position) {
        if (item.getType()== BaseItem.ITEM_SMALL_PIC_WEB){
            return true;
        }
        return false;
    }

    @Override
    public void convert(ViewHolder holder, final TaskData taskData, int position) {
        if (taskData==null){
            return;
        }
        holder.setText(R.id.maintitle,taskData.getReadName());
        holder.setText(R.id.subtitle,taskData.getReadSubtitle());
        //holder.setText(R.id.last_order,"剩余:"+taskData.getAvailable()+"份");
        //holder.setText(R.id.shouyi,(taskData.getStepPoints()/100d)+"元");
        holder.getView(R.id.shouyi).setVisibility(View.GONE);
        ImageView pic = holder.getView(R.id.item_pic);
        GlideUtil.loadImageView(WanSdkManager.getContext(),taskData.getReadLogo(),pic);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//详情跳转
                if (taskClickCallBack!=null){
                    taskClickCallBack.taskClick(taskData);
                }
            }
        });



        TextView lable = holder.getConvertView().findViewById(R.id.app_lable1);
        TextView lable1 = holder.getConvertView().findViewById(R.id.app_lable2);



        if (taskData.getTags()!=null){
            String[] strings = taskData.getTags().split(";");
            if (strings.length>0){
                lable.setVisibility(View.VISIBLE);
                holder.setText(R.id.app_lable1,strings[0]);
            }else {
                lable.setVisibility(View.GONE);
                lable1.setVisibility(View.GONE);
            }
            if (strings.length>1){
                holder.setText(R.id.app_lable2,strings[1]);
                lable1.setVisibility(View.VISIBLE);
                lable1.setText(strings[1]);
            }else {
                lable1.setVisibility(View.GONE);
            }
        }
    }

}
