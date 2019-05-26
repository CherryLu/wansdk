package com.wanappsdk.animotion;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;

import com.wanappsdk.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 金币掉落
 */
public class ShowGold {

    private  FlakeView flakeView;

    public ShowGold(Context context) {
        /**
         * 金币掉落
         */
        flakeView = new FlakeView(context);
        // 设置同时出现在屏幕上的金币数量 建议64以内 过多会引起卡顿
        flakeView.addFlakes(30);

        /**
         * 绘制的类型
         *
         * @see View.LAYER_TYPE_HARDWARE
         * @see View.LAYER_TYPE_SOFTWARE
         * @see View.LAYER_TYPE_NONE
         */
        flakeView.setLayerType(View.LAYER_TYPE_NONE, null);
    }

    public FlakeView getFlakeView() {
        return flakeView;
    }

    /**
     * 显示金币掉落动画
     */
    private TimerTask timerTaskGold;

    public void show(final LinearLayout container, Context context, final AnimotionFinish animotionFinish) {
        container.setVisibility(View.VISIBLE);
        container.removeAllViews();
        // 将flakeView 添加到布局中
        container.addView(flakeView);

        if (timerTaskGold != null)
            timerTaskGold.cancel();
        timerTaskGold = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        container.removeAllViews();
                        container.setVisibility(View.GONE);
                        if (animotionFinish!=null){
                            animotionFinish.onFinish();
                        }
                    }
                });
            }
        };
        new Timer().schedule(timerTaskGold, 3 * 1000);
        MediaPlayer player = MediaPlayer.create(context, R.raw.shake);
        player.start();

    }


   public interface AnimotionFinish{
        void onFinish();
    }
}
