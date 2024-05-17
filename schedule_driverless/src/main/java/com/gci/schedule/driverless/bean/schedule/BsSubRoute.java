package com.gci.schedule.driverless.bean.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BsSubRoute {

    private Long routeId;

    private Long templateId;

    private Long subRouteId;

    private BsSubRouteDetail goSubRouteDetail;

    private BsSubRouteDetail backSubRouteDetail;

    private String updateUser;

    private Date updateTime;

    public BsSubRouteDetail getSubRouteDetail(String direction) {
        return direction.equals(goSubRouteDetail.getDirection()) ? goSubRouteDetail : backSubRouteDetail;
    }

    /**
     * 获取反向截短明细
     *
     * @param subRouteDetail
     * @return
     */
    public BsSubRouteDetail getRSubRouteDetail(BsSubRouteDetail subRouteDetail) {
        return subRouteDetail == goSubRouteDetail ? backSubRouteDetail : goSubRouteDetail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"routeId\":")
                .append(routeId);
        sb.append(",\"templateId\":")
                .append(templateId);
        sb.append(",\"subRouteId\":")
                .append(subRouteId);
        sb.append(",\"goSubRouteDetail\":")
                .append(goSubRouteDetail);
        sb.append(",\"backSubRouteDetail\":")
                .append(backSubRouteDetail);
        sb.append('}');
        return sb.toString();
    }
}
