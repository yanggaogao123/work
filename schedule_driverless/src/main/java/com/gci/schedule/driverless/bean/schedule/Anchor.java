package com.gci.schedule.driverless.bean.schedule;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import lombok.Data;

import java.util.Date;

@Data
public class Anchor {
    private Long routeId;

    private String direction;

    private String beginTime; //开始时间

    private String endTime; //结束时间

    private Long anchorDurationMin; //最小停站时长

    private Long anchorDurationMax; //最大停站时长

    private Short busOccupancy; //满载率

    private String peakType; //高平低峰设置(1:早高峰,2:晚高峰,3:平峰)

    private Long templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

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
