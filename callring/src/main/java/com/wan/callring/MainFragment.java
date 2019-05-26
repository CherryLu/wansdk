package com.wan.callring;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.wan.callring.ui.BaseFragment;
import com.wan.callring.ui.fragment.CallFragment;
import com.wan.callring.ui.fragment.MineFragment;
import com.wan.callring.ui.fragment.MoneyFragment;
import com.wan.callring.ui.fragment.NewsFragment;
import com.wan.callring.ui.fragment.ShowFragment;

public class MainFragment extends BaseFragment {
    private RadioGroup radioGroup;

    private CallFragment callFragment;
    private ShowFragment showFragment;
    private NewsFragment newsFragment;
    private MoneyFragment moneyFragment;
    private MineFragment mineFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mian,container,false);
        initView();
        return rootView;
    }


    private void initView(){
        radioGroup = rootView.findViewById(R.id.callshow_bottom_tab_layout);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.callshow_bottom_home_tab_1) {
                    setFragment(0);
                }else if (i == R.id.callshow_bottom_home_tab_2){
                    setFragment(1);
                }else if (i == R.id.callshow_bottom_home_tab_3){
                    setFragment(2);
                }else if (i == R.id.callshow_bottom_home_tab_4){
                    setFragment(3);
                }else if (i == R.id.callshow_bottom_home_tab_5){
                    setFragment(4);
                }
            }
        });
    }


    private void setFragment(int which){


        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (which){
            case 0:
                if (callFragment == null) {
                    callFragment = new CallFragment();
                }
                ft.replace(R.id.container, callFragment);
                break;
            case 1:
                if (showFragment == null) {
                    showFragment = new ShowFragment();
                }
                ft.replace(R.id.container, showFragment);
                break;
            case 2:
                if (newsFragment == null) {
                    newsFragment = new NewsFragment();
                }
                ft.replace(R.id.container, newsFragment);
                break;
            case 3:
                if (moneyFragment == null) {
                    moneyFragment = new MoneyFragment();
                }
                ft.replace(R.id.container, moneyFragment);
                break;
            case 4:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                }
                ft.replace(R.id.container, mineFragment);
                break;
        }

        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ft.commit();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

}
