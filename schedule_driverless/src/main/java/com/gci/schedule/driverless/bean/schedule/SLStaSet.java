package com.gci.schedule.driverless.bean.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:cjc
 * @Description:
 * @Date:2019/9/29
 * @Modified by:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SLStaSet {
    private Long routeId;
    private String direction;
    private Long templateId; //模板Id
    private Integer updateUser;
    private Date updateTime;
    private Long lastStaId;
    private String lastStaName;
    private Long nextFirstStaId;
    private String nextFirstStaName;

    private Integer turnAroundTime; //短线掉头时间（以分钟为单位）

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"direction\":\"")
                .append(direction).append('\"');
        sb.append(",\"templateId\":")
                .append(templateId);
        sb.append(",\"updateUser\":")
                .append(updateUser);
        sb.append(",\"updateTime\":\"")
                .append(updateTime).append('\"');
        sb.append(",\"lastStaId\":")
                .append(lastStaId);
        sb.append(",\"lastStaName\":\"")
                .append(lastStaName).append('\"');
        sb.append(",\"nextFirstStaId\":")
                .append(nextFirstStaId);
        sb.append(",\"nextFirstStaName\":\"")
                .append(nextFirstStaName).append('\"');
        sb.append(",\"turnAroundTime\":")
                .append(turnAroundTime);
        sb.append('}');
        return sb.toString();
    }
}
