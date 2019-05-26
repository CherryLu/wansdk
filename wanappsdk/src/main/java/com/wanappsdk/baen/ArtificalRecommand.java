package com.wanappsdk.baen;

import java.io.Serializable;

public class ArtificalRecommand implements Serializable{



    private int count;

    private Long lastTime;

    private int hasCommit = 0;

    private int press;

    public int getHasCommit() {
        return hasCommit;
    }

    public void setHasCommit(int hasCommit) {
        this.hasCommit = hasCommit;
    }

    public ArtificalRecommand() {

    }



    public int getCount() {
        return count;
    }



    public Long getLastTime() {
        return lastTime;
    }


    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPress() {
        return press;
    }

    public void setPress(int press) {
        this.press = press;
    }
}
