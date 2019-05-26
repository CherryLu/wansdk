package com.wan.callring.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wan.callring.R;
import com.wan.callring.ui.BaseFragment;
import com.wan.callring.ui.widget.PagerSlidingTabStrip;

public class CallFragment extends BaseFragment {

    String[] tabs = new String[]{"拨号","联系人"};
    LinearLayout layout_title;
    PagerSlidingTabStrip pagerSlidingTabStrip;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_call,container,false);
        initView();
        return rootView;
    }


    private void initView(){
        layout_title = rootView.findViewById(R.id.layout_title);
        pagerSlidingTabStrip = rootView.findViewById(R.id.pagerSlidingTabStrip);
        for ( int i = 0; i<tabs.length ; i++){
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.callshow_square_text_tab, layout_title, false);
            textView.setTextColor(getActivity().getResources().getColor(R.color.white));
            layout_title.addView(textView);
            textView.setText(tabs[i]);
        }


    }
}
