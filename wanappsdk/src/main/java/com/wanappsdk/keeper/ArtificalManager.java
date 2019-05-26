package com.wanappsdk.keeper;

import com.wanappsdk.baen.ArtificalData;
import com.wanappsdk.baen.ArtificalRecommand;
import com.wanappsdk.utils.FileUtils;
import com.wanappsdk.utils.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class ArtificalManager {


    public ArtificalData artificalData;

    private static ArtificalManager manager;


    private ArtificalManager() {

    }

    public static ArtificalManager getInstance(){

        if (manager==null){
            manager = new ArtificalManager();
        }

        return manager;
    }

    /**
     * 保存
     */
    public void saveUserData() {
        // TODO Auto-generated method stub
        String path = FileUtils.getAppBasePath() + "artifical.dat";
        ArtificalKeeper keeper = new ArtificalKeeper();
        keeper.artificalData = artificalData;

        ObjectUtils.saveObjectData(keeper, path);
    }


    /**
     * 读取
     * @return
     */
    public boolean readUserData() {
        String path = FileUtils.getAppBasePath() + "artifical.dat";
        Object obj = FileUtils.loadObjectData(path);
        if (obj != null) {
            ArtificalKeeper keeper = (ArtificalKeeper) obj;
            this.artificalData = keeper.artificalData;
            if (artificalData != null&&artificalData.getHashMap()!=null) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * 增加或更新
     * @param id
     * @return
     */
    public void addTask(String id,ArtificalRecommand recommand){
        if (artificalData!=null&&artificalData.getHashMap()!=null){
            Date now = new Date();
            recommand.setLastTime(now.getTime());
            artificalData.getHashMap().put(id,recommand);

            saveUserData();
        }

    }


    /**
     * 根据ID获取最后更新时间
     * @param id
     * @return
     */
    public Long getLastTime(String id){

        if (artificalData!=null&&artificalData.getHashMap()!=null){
           return artificalData.getHashMap().get(id).getLastTime();
        }

        return 0L;

    }


    /**
     * 根据ID获取当前阅读数
     * @param id
     * @return
     */
    public int getcurrentCount(String id){
        if (artificalData!=null&&artificalData.getHashMap()!=null){
           ArtificalRecommand recommand =  artificalData.getHashMap().get(id);
            if (recommand!=null){
                return recommand.getCount();
            }else {
                return 0;
            }
        }

        return 0;

    }


    /**
     * 根据ID获取当前阅读数
     * @param id
     * @return
     */
    public int getcurrentPress(String id){
        if (artificalData!=null&&artificalData.getHashMap()!=null){
            ArtificalRecommand recommand =  artificalData.getHashMap().get(id);
            if (recommand!=null){
                return recommand.getPress();
            }else {
                return 0;
            }
        }

        return 0;

    }




    /**
     * 根据ID获取已完成次数
     * @param id
     * @return
     */
    public int gethasCompleteCount(String id){
        if (artificalData!=null&&artificalData.getHashMap()!=null){
            ArtificalRecommand recommand =  artificalData.getHashMap().get(id);
            if (recommand!=null){
                return recommand.getHasCommit();
            }else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 刷新
     */
    public void refresh(){
        if (artificalData!=null&&artificalData.getHashMap()!=null){

            for (Iterator<Map.Entry<String, ArtificalRecommand>> it = artificalData.getHashMap().entrySet().iterator(); it.hasNext();){
                Map.Entry<String, ArtificalRecommand> item = it.next();

                Date date = new Date();
                SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
                String now = format.format(date);

                String last = format.format(item.getValue().getLastTime());

                if (!last.equals(now)){//不是今天啊
                    it.remove();
                }
            }

        }

        saveUserData();
    }

}
