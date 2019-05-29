package tv.danmaku.ijk.media.example.widget.media;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 自定义控制布局需要实现的接口
 * Created by maclay on 2017/8/23.
 */

public interface ICustomController {
    public View getRootView();

    public View getPauseIBtn();

    public View getFwdIBtn();

    public View getRewIBtn();

    public View getNextIBtn();

    public View getPrevIBtn();

    public SeekBar getSeekBar();

    public TextView getEndTime();

    public TextView getCurTime();

    public void setPauseBtn(boolean isPlaying);
}
