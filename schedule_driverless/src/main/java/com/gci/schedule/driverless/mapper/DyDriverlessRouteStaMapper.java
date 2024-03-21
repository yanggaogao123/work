package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.scheduleD.DyDriverlessRouteSta;

import java.util.List;

public interface DyDriverlessRouteStaMapper {
    int deleteByPrimaryKey(Long routeStaId);

    int insert(DyDriverlessRouteSta record);

    int insertSelective(DyDriverlessRouteSta record);

    DyDriverlessRouteSta selectByPrimaryKey(Long routeStaId);

    int updateByPrimaryKeySelective(DyDriverlessRouteSta record);

    int updateByPrimaryKey(DyDriverlessRouteSta record);

    List<DyDriverlessRouteSta> selectByRouteId(Long routeId);
}