package com.gci.schedule.driverless.dynamic.mapper;

import com.gci.schedule.driverless.dynamic.bean.RouteStaTurn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RouteStaTurnDynamicMapper {

	List<RouteStaTurn> getRouteStaTurnList(@Param("routeId")Long routeId, @Param("templateId")Integer templateId);
	
}
