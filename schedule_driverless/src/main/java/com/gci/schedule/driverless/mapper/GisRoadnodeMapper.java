package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.GisRoadInfo;
import com.gci.schedule.driverless.bean.scheduleD.GisRoadnode;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GisRoadnodeMapper {
    int deleteByPrimaryKey(Long roadnodeId);

    int insert(GisRoadnode record);

    int insertSelective(GisRoadnode record);

    GisRoadnode selectByPrimaryKey(Long roadnodeId);

    List<GisRoadInfo> selectByRouteIdAndDir(@Param("routeId") Long routeId, @Param("direction") String direction);

    List<Map<String,Object>> selectStationByRouteIdAndDir(@Param("routeId") Long routeId, @Param("direction") String direction);

    int updateByPrimaryKeySelective(GisRoadnode record);

    int updateByPrimaryKey(GisRoadnode record);
}