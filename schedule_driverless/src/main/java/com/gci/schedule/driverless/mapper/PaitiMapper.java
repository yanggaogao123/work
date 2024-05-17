package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.paiti.Bus;
import com.gci.schedule.driverless.bean.paiti.PaitiDataVO;
import com.gci.schedule.driverless.bean.paiti.Route;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface PaitiMapper {

    List<PaitiDataVO> list(@Param("key") String key, @Param("ids") List<String> ids,
                           @Param("runDate") Date runDate, @Param("routeCode") String routeCode);

    void del(@Param("routeCode") String routeCode);

    void insert(@Param("list") List<PaitiDataVO> list);

    List<Bus> queryDicheBusList();

    List<Route> queryDicheRouteList();

    List<PaitiDataVO> queryDicheRecentDataList(@Param("runDate") Date runDate);

    String querySchedulePlanExist(@Param("runDate") Date runDate,@Param("routeCode") String routeCode);
}
