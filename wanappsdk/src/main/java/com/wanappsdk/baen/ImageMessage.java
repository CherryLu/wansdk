package com.wanappsdk.baen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageMessage implements Serializable{

    private List<String> imageMd5;


    public void setImageMd5(List<String> imageMd5) {
        this.imageMd5 = imageMd5;
    }

    public ImageMessage() {
        imageMd5 = new ArrayList<>();
    }

    public List<String> getImageMd5() {
        return imageMd5;
    }


    public void addList(List<String> list){
        if (imageMd5 ==null|| imageMd5.size()==0){
            imageMd5 = new ArrayList<>();
            imageMd5.addAll(list);
        }else {
            for (int i = 0;i<list.size();i++){
                    if (!imageMd5.contains(list.get(i))){
                        imageMd5.add(list.get(i));
                    }
            }

        }

    }
}
