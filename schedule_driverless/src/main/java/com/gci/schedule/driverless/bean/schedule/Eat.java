package com.gci.schedule.driverless.bean.schedule;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Eat {

    private Long id;

    private Long routeId;
    //吃饭总站
    private String direction;
    //开始时间
    private String beginTime;
    //吃饭时间
    private Long eatTime;

    private Long templateId; //模板Id

    private Integer updateUser;

    private Date updateTime;

    @JSONField(serialize = false)
    public Date getBeginDateTime() {
        if (beginTime == null) {
            return null;
        }
        String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
        return new Date(DateUtil.str2Timestamp(today + " " + beginTime.substring(0, 2)
                + ":" + beginTime.substring(2, 4) + ":00", "yyyy-MM-dd HH:mm:ss"));
    }

}
