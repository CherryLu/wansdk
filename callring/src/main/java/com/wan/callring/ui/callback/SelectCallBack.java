package com.wan.callring.ui.callback;

import com.wan.callring.bean.ContactBean;

public interface SelectCallBack {

    void selectedConstant(ContactBean contactBean);
    void unselectedConstant(ContactBean contactBean);
}
