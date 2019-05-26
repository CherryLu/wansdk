package com.wanappsdk.baen;

import java.util.List;

public class SortData {

    private List<SortBean> tii_JidAname;

    private List<TaskData> tJob_list0;

    private List<SortBean> tcbm_jid;


    public List<TaskData> gettJob_list0() {
        return tJob_list0;
    }

    public List<SortBean> getTii_JidAname() {
        return tii_JidAname;
    }

    public List<SortBean> getTcbm_jid() {
        return tcbm_jid;
    }


    public void setTii_JidAname(List<SortBean> tii_JidAname) {
        this.tii_JidAname = tii_JidAname;
    }

    public void settJob_list0(List<TaskData> tJob_list0) {
        this.tJob_list0 = tJob_list0;
    }

    public void setTcbm_jid(List<SortBean> tcbm_jid) {
        this.tcbm_jid = tcbm_jid;
    }
}
