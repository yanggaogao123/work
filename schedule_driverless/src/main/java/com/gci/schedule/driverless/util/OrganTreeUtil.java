package com.gci.schedule.driverless.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gci.schedule.driverless.bean.OgranExists;
import com.gci.schedule.driverless.bean.OgranTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhouyanjie
 * @Description:
 * @Date: Created in 2019/9/20 9:32
 */
public class OrganTreeUtil {

    public  static Map<String ,String > organTreeMap = new HashMap<>(); //机构树 key:userId, value:organTree

    public  static Map<String ,String > organMap = new HashMap<>(); //机构及其子机构树 key:organId, value:organTree

    /**
     * @Author: zhouyanjie
     * @param
     * organIdList:机构编号集合
     * organTreeOwnList:该用户所属机构
     * ogranExists:机构存在中间表
     * @Description:获取机构编号集合存在于该用户所属机构下的机构的个数
     * @Date: Created in 2019/9/18  9:28
     */
    public static void getOgranListExistsSize(List<Integer > organIdList, List<OgranTree> organTreeOwnList , OgranExists ogranExists ){
        //存在机构id的数量
        for(int i =0; i<organIdList.size();i++){
            Integer organId = organIdList.get(i);
            for(int j=0 ; j<organTreeOwnList.size();j++){
                if(organId.toString().equals(organTreeOwnList.get(j).getId().toString())){
                    Integer existsSize =ogranExists.getExistSize()+1;
                    ogranExists.setExistSize(existsSize);
                }else{
                    List<Integer > organIdListThat  = new ArrayList<>();
                    organIdListThat.add(organId);
                    //递归
                    getOgranListExistsSize(organIdListThat,organTreeOwnList.get(j).getChildren(),ogranExists);
                }
            }
        }

    }


    /**
     * @Author: zhouyanjie
     * @param
     * organIdList:机构编号集合
     * organTreeOwnList:该用户所属机构
     * ogranExists:机构存在中间表
     * recursionFlag：递归标示
     * @Description:查找机构编号集合下所有的机构
     * @Date: Created in 2019/9/18  9:25
     */
    public static void getAllOgranIdList(List<Integer > organIdList, List<OgranTree> organTreeOwnList, OgranExists ogranExists , boolean recursionFlag){
        for(int i =0; i<organIdList.size();i++){
            Integer organId = organIdList.get(i);
            for(int j = 0; j<organTreeOwnList.size(); j++){
                Integer thatOrganId =organTreeOwnList.get(j).getId().intValue();
                List<Integer > organIdListThat  = new ArrayList<>();
                organIdListThat.add(organId);
                if(organId.toString().equals(thatOrganId.toString())||recursionFlag){
                    ogranExists.getAllOgranIdList().add(thatOrganId);
                    if(organTreeOwnList.get(j).getChildren().size()>0){
                        //递归
                        getAllOgranIdList(organIdListThat,organTreeOwnList.get(j).getChildren(),ogranExists,true);
                    }
                }else {
                    getAllOgranIdList(organIdListThat,organTreeOwnList.get(j).getChildren(),ogranExists,false);
                }
            }
        }
    }

    public static Integer[] getOrganIds(JSONArray jsonArray){
        if(jsonArray !=null && jsonArray.size() > 0){
            List<Integer> list = new ArrayList<Integer>();
            getOrganIds(jsonArray,list);
            return (Integer[])list.toArray(new Integer[list.size()]);
        }
        return null;
    }

    public static void getOrganIds(JSONArray jsonArray, List<Integer> list){
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add(jsonObject.getInteger("id"));
            if(jsonObject.containsKey("children")){
                getOrganIds(jsonObject.getJSONArray("children"),list);
            }
        }
    }
}
