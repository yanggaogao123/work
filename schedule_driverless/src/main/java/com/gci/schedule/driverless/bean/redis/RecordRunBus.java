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
 * @Date: 2021-01-28 下午3:30
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordRunBus {
    private Long routeId;
    private List<Long> busIds;
    @JSONField(name = "createTime", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
