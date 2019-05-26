package com.wanappsdk.ui.viewitem;

import android.view.View;
import android.widget.ImageView;

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

public class WebSmallIconItem implements ItemViewDelegate<TaskData> {


    TaskClickCallBack taskClickCallBack;

    public WebSmallIconItem(TaskClickCallBack taskClickCallBack) {
        this.taskClickCallBack = taskClickCallBack;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_small_icon;
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

    }

}
