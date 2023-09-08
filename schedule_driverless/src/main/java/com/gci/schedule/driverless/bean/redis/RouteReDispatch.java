package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-01-16 下午1:50
 * @version: v1.0
 * @Modified by:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteReDispatch {
    private Long routeId;
    private List<ReDispatch> reDispatchList;
    private Long countDown;
    @JSONField(name = "expireTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private String reDispatchType;  //重发类型 1-预测变化 2-短线跟车位前插 3-到站即走保不断位
    private String singleSelect;    //是否单选 1-是 0-否
    /**
     * 重排触发类型：0-系统 1-人工
     */
    private String reDispatchTriggerType;

    @JSONField(serialize = false)
    public Boolean isForecastReDispatch() {
        return "1".equals(reDispatchType);
    }

    @JSONField(serialize = false)
    public Boolean isRunPlanReDispatch() {
        return "2".equals(reDispatchType);
    }

    @JSONField(serialize = false)
    public Boolean isExpired() {
        return expireTime == null ? new Date().getTime() > createTime.getTime() + 3 * 60 * 1000
                : new Date().getTime() > expireTime.getTime();
    }
}
