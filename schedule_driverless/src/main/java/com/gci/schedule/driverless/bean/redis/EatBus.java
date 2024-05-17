package com.gci.schedule.driverless.bean.redis;

import com.alibaba.fastjson.annotation.JSONField;
import com.gci.schedule.driverless.bean.common.BsData;
import com.gci.schedule.driverless.bean.common.Constant;
import com.gci.schedule.driverless.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-09-02 11:31
 * @version: v1.0
 * @Modified by:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EatBus {

    private Long routeId;
    private Long busId;
    private String busName;

    private String upBeginTime;
    private String downBeginTime;

    private Long upEatTime;
    private Long downEatTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalTime;

    private Integer tripEatStatus;
    private Integer eatStatus;

    private String eatDirection;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    /**
     * 吃饭确认状态：0-未确认 1-已确认
     * {@link Constant.EatConfirm}
     */
    private Integer confirmStatus;

    @JSONField(serialize = false)
    public Long getEatTime(String direction) {
        return Constant.Direction.UP.equals(direction) ? getUpEatTime() : getDownEatTime();
    }

    @JSONField(serialize = false)
    public Boolean isAllEat() {
        return BsData.isLoopRoute(routeId)
                || (upBeginTime != null && downBeginTime != null);
    }

    @JSONField(serialize = false)
    public Date getUpBeginDateTime() {
        if (upBeginTime == null) {
            return null;
        }
        String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
        return new Date(DateUtil.str2Timestamp(today + " " + upBeginTime.substring(0, 2)
                + ":" + upBeginTime.substring(2, 4) + ":00", "yyyy-MM-dd HH:mm:ss"));
    }

    @JSONField(serialize = false)
    public Date getDownBeginDateTime() {
        if (downBeginTime == null) {
            return null;
        }
        String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
        return new Date(DateUtil.str2Timestamp(today + " " + downBeginTime.substring(0, 2)
                + ":" + downBeginTime.substring(2, 4) + ":00", "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"busId\":")
                .append(busId);
        sb.append(",\"busName\":\"")
                .append(busName).append('\"');
        sb.append(",\"upBeginTime\":\"")
                .append(upBeginTime).append('\"');
        sb.append(",\"downBeginTime\":\"")
                .append(downBeginTime).append('\"');
        sb.append(",\"upEatTime\":")
                .append(upEatTime);
        sb.append(",\"downEatTime\":")
                .append(downEatTime);
        sb.append(",\"arrivalTime\":\"")
                .append(arrivalTime).append('\"');
        sb.append(",\"tripEatStatus\":")
                .append(tripEatStatus);
        sb.append(",\"eatStatus\":")
                .append(eatStatus);
        sb.append(",\"operateTime\":\"")
                .append(operateTime).append('\"');
        sb.append('}');
        return sb.toString();
    }

    /**
     * 吃饭车辆:
     * HASH:dpdb.dispatch.eat.bus.route_id={routeId}
     * key:{busId}
     * value:
     * {
     *     "routeId": 94,
     *     "busId": 19234232,
     *     "busName": "2061",
     *     "upBeginTime": "11:07",      //上行吃饭开始时间
     *     "downBeginTime": "11:25",    //下行吃饭开始时间
     *     "arrivalTime": "yyyy-MM-dd HH:mm:ss",
     *     "tripEatStatus": 1,          //本躺吃饭状态: 1:需要吃饭 0:不需要吃饭
     *     "eatStatus": 1,              //总的吃饭状态: 1:需要吃饭 0:不需要吃饭
     *     "operateTime": "2020-05-13 20:00:00"
     * }
     */
}
