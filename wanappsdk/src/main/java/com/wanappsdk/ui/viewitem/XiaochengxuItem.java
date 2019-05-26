package com.wanappsdk.ui.viewitem;

import android.widget.ImageView;

import com.wanappsdk.R;
import com.wanappsdk.WanSdkManager;
import com.wanappsdk.baen.BaseItem;
import com.wanappsdk.baen.TaskData;
import com.wanappsdk.utils.ApkUtils;
import com.wanappsdk.utils.GlideUtil;
import com.wanappsdk.utils.TaskClickCallBack;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

public class XiaochengxuItem implements ItemViewDelegate<TaskData> {

    TaskClickCallBack taskClickCallBack;

    public XiaochengxuItem(TaskClickCallBack taskClickCallBack) {
        this.taskClickCallBack = taskClickCallBack;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.xiaochengxu_item;
    }

    @Override
    public boolean isForViewType(TaskData item, int position) {
        if (item.getType()== BaseItem.ITEM_XIAOCHENGXU){
            return true;
        }
        return false;
    }

    @Override
    public void convert(ViewHolder holder, TaskData taskData, int position) {
        if (taskData==null){
            return;
        }

        holder.setText(R.id.name,taskData.getJobName());
        holder.setText(R.id.jobdesc,taskData.getJobSubtitle());
        holder.setText(R.id.money, ApkUtils.getMuchMoney(taskData.getPlayPoints()));

        ImageView pic = holder.getView(R.id.pic);
        GlideUtil.loadImageView(WanSdkManager.getContext(),taskData.getJobLogo(),pic);


    }
}
