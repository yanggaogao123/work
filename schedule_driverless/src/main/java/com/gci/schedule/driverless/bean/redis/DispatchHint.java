package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-01-05 下午2:16
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchHint {
    private String hintCode;     //提示编码
    private String hint;         //提示内容
    private Long hintSeconds;  //提示秒
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;   //创建时间
    private String employeeName;
    private String busName;
}
