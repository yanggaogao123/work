package com.gci.schedule.driverless.bean.schedule;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import lombok.Data;

import java.util.Date;

//最低发班设置
@Data
public class Classes {

    private Long routeId;

    private String direction;

    private String beginTime;

    private String endTime;

    private Long classesNumMin; //最小发班数

    private Long maxDispatchInterval; //最大发班间隔

    private Integer isRegularBus;  //是否定点班车

    private Long templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    @JSONField(serialize = false)
    public Boolean isRegularBus() {
        return isRegularBus != null && isRegularBus == 1;
    }

    @JSONField(serialize = false)
    public Date getBeginDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getFirstTime(beginTime, endTime);
    }

    @JSONField(serialize = false)
    public Date getEndDateTime() {
        if (beginTime == null || endTime == null) {
            return null;
        }
        return BsData.getLatestTime(beginTime, endTime);
    }
}
