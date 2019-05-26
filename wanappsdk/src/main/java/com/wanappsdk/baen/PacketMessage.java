package com.wanappsdk.baen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PacketMessage implements Serializable{

    private List<String> packetName;


    public void setPacketName(List<String> packetName) {
        this.packetName = packetName;
    }


    public List<String> getPacketName() {
        return packetName;
    }


    public void addList(List<String> list){
        if (packetName==null||packetName.size()==0){
            packetName = new ArrayList<>();
            packetName.addAll(list);
        }else {
            for (int i = 0;i<list.size();i++){
                    if (!packetName.contains(list.get(i))){
                        packetName.add(list.get(i));
                    }
            }

        }
    }
}
