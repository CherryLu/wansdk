package tv.danmaku.ijk.media.example.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.wan.callring.R;

import tv.danmaku.ijk.media.example.widget.media.ICustomController;

/**
 * Created by maclay on 2017/8/23.
 */

public class CustomController implements ICustomController {
    private View prev;
    private View rew;
    private ImageView pause;
    private View ffwd;
    private View next;
    private TextView time_current;
    private SeekBar mediacontroller_progress;
    private TextView time;
    private View rootView;

    public CustomController(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.media_controller2, null);
        bindViews();
    }

    private void bindViews() {
        prev = (View) findViewById(R.id.prev);
        rew = (View) findViewById(R.id.rew);
        pause = (ImageView) findViewById(R.id.pause);
        ffwd = (View) findViewById(R.id.ffwd);
        next = (View) findViewById(R.id.next);
        time_current = (TextView) findViewById(R.id.time_current);
        mediacontroller_progress = (SeekBar) findViewById(R.id.mediacontroller_progress);
        time = (TextView) findViewById(R.id.time);
    }

    public View findViewById(int id) {
        return rootView.findViewById(id);
    }

    @Override
    public View getRootView() {
        return rootView;
    }


    @Override
    public View getPauseIBtn() {
        return pause;
    }


    @Override
    public View getFwdIBtn() {
        return ffwd;
    }


    @Override
    public View getRewIBtn() {
        return rew;
    }


    @Override
    public View getNextIBtn() {
        return next;
    }


    @Override
    public View getPrevIBtn() {
        return prev;
    }


    @Override
    public SeekBar getSeekBar() {
        return mediacontroller_progress;
    }


    @Override
    public TextView getEndTime() {
        return time;
    }


    @Override
    public TextView getCurTime() {
        return time_current;
    }

    @Override
    public void setPauseBtn(boolean isPlaying) {
        if (isPlaying) {
            pause.setImageResource(getPlayImgId());
        } else {
            pause.setImageResource(getPlayImgId());
        }
    }

    public int getPauseImgId() {
        return R.mipmap.ic_media_pause;
    }

    public int getPlayImgId() {
        return R.mipmap.ic_media_play;
    }
}
