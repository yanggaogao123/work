package com.gci.schedule.driverless.bean;

import java.util.List;

//机构存在中间表
public class OgranExists {
    private Integer userid;
    private Integer existSize; //机构编号集合存在于该用户所属机构下的机构的个数
    private List<Integer> allOgranIdList; //查找机构编号集合下所有的机构

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getExistSize() {
        return existSize;
    }

    public void setExistSize(Integer existSize) {
        this.existSize = existSize;
    }

    public List<Integer> getAllOgranIdList() {
        return allOgranIdList;
    }

    public void setAllOgranIdList(List<Integer> allOgranIdList) {
        this.allOgranIdList = allOgranIdList;
    }
}
