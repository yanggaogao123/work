package com.gci.schedule.driverless.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteFirstLastSta {

    private Long routeId;
    private Long upFirstRouteStaId;
    private Long upLastRouteStaId;
    private Long downFirstRouteStaId;
    private Long downLastRouteStaId;

    private Long upFirstStationId;
    private Long upLastStationId;
    private Long downFirstStationId;
    private Long downLastStationId;

    public RouteFirstLastSta(Long routeId) {
        this.routeId = routeId;
    }
}
