package com.wan.callring.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wan.callring.R;
import com.wan.callring.bean.DetailsBean;
import com.wan.callring.utils.GlideUtil;
import com.wan.callring.utils.ScreemUtils;

import java.util.List;

public class GridListAdapter extends BaseRecyclerAdapter<GridListAdapter.ViewHolder> {
    private List<DetailsBean> list;
    private int largeCardHeight, smallCardHeight;
    Context context;
    public GridListAdapter(List<DetailsBean> list, Context context) {
        this.list = list;
        this.context = context;
        largeCardHeight = ScreemUtils.dip2px(context, 150);
        smallCardHeight = ScreemUtils.dip2px(context, 100);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position, boolean isItem) {
        String url = list.get(position).sthumbnail;

        GlideUtil.loadImageViewWithListener(url, holder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                if (holder.imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
                params.height = (int) ((ScreemUtils.getScreenWidth(context)/2-80)*imageHeight/imageWidth);;
                holder.imageView.setLayoutParams(params);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        holder.tv_buttom_title.setText(list.get(position).sname);
        if(position==0&&isMy) {
            holder.iv_left_top_icon.setVisibility(View.VISIBLE);
        }else{
            holder.iv_left_top_icon.setVisibility(View.GONE);
        }
        final int pos = position;
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra(PreviewActivity._DATA,list.get(pos));
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getAdapterItemViewType(int position) {
        return 0;
    }

    @Override
    public int getAdapterItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }
    boolean isMy;
    public void setData(List<DetailsBean> list, boolean isMy) {
        this.list = list;
        this.isMy = isMy;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_grid_list, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void insert(DetailsBean data, int position) {
        insert(list, data, position);
    }

    public void remove(int position) {
        remove(list, position);
    }

    public void clear() {
        clear(list);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView iv_left_top_icon;
        TextView tv_buttom_title;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview_grid_item);
            iv_left_top_icon = (ImageView) itemView.findViewById(R.id.iv_left_top_icon);
            tv_buttom_title = (TextView)itemView.findViewById(R.id.tv_buttom_title);
        }
    }

    public DetailsBean getItem(int position) {
        if (position < list.size())
            return list.get(position);
        else
            return null;
    }

}