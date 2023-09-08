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
public class RouteReDispatchLostParams {
    private Long routeId;
    private List<ReDispatchLostParams> reDispatchList;
    private Long countDown;
    @JSONField(name = "expireTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Boolean isExpired() {
        return expireTime == null ? new Date().getTime() > createTime.getTime() + 3 * 60 * 1000
                : new Date().getTime() > expireTime.getTime();
    }
}
