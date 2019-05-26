package com.wanappsdk.baen;

import java.io.Serializable;
import java.util.HashMap;

public class ArtificalData implements Serializable {


    public ArtificalData() {
        hashMap = new HashMap<>();
    }



    private HashMap<String,ArtificalRecommand> hashMap;


    public HashMap<String, ArtificalRecommand> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, ArtificalRecommand> hashMap) {
        this.hashMap = hashMap;
    }
}
