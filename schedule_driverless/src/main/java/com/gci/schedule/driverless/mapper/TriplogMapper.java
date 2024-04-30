package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.RouteAvgMileage;
import com.gci.schedule.driverless.bean.bs.RefuelDto;
import com.gci.schedule.driverless.bean.bs.TriplogSimpleDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TriplogMapper {

    List<TriplogSimpleDto> selectAll(@Param("routeId") Long routeId,
                                     @Param("runDate") Date runDate,
                                     @Param("serviceType") String serviceType);

    List<TriplogSimpleDto> selectList(Long busId);

    TriplogSimpleDto getLastTripLog(@Param("routeId") Long routeId,
                                    @Param("firstStationId") Long firstStationId,
                                    @Param("planTime") Date planTime,
                                    @Param("runDate") Date runDate);

    TriplogSimpleDto getLastTripLogByTaskId(@Param("routeId") Long routeId,
                                            @Param("taskId") Long taskId,
                                            @Param("planTime") Date planTime);

    TriplogSimpleDto getLast(@Param("routeId") Long routeId,
                             @Param("serviceType") String serviceType,
                             @Param("firstStationId") Long firstStationId,
                             @Param("runDate") Date runDate);

    List<TriplogSimpleDto> selecListByServiceType(@Param("routeId") Long routeId,
                                                  @Param("busId") Long busId,
                                                  @Param("runDate") Date runDate,
                                                  @Param("startTime") Date startTime,
                                                  @Param("serviceType") String serviceType);

    List<TriplogSimpleDto> selecListByTime(@Param("routeId") Long routeId,
                                           @Param("direction") String direction,
                                           @Param("runDate") Date runDate,
                                           @Param("startTime") Date startTime,
                                           @Param("endTime") Date endTime,
                                           @Param("serviceType") String serviceType);


    List<TriplogSimpleDto> selectRunTripLog(@Param("busId") Long busId,
                                            @Param("runDate") Date runDate,
                                            @Param("startTime") Date startTime);

    List<TriplogSimpleDto> selectFirstList(@Param("routeId") Long routeId,
                                           @Param("runDate") Date runDate,
                                           @Param("busIds") List<Long> busIds,
                                           @Param("serviceType") String serviceType);

    TriplogSimpleDto getFirst(@Param("routeId") Long routeId,
                              @Param("runDate") Date runDate,
                              @Param("busId") Long busId);

    List<TriplogSimpleDto> selectByFromToStationId(@Param("routeId") Long routeId,
                                                   @Param("direction") String direction,
                                                   @Param("startTime") Date startTime,
                                                   @Param("endTime") Date endTime,
                                                   @Param("fromStationId") Long fromStationId,
                                                   @Param("toStationId") Long toStationId);

    List<TriplogSimpleDto> selectByCurrentDay(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    List<TriplogSimpleDto> selectByDirection(@Param("routeId") Long routeId,
                                             @Param("startRunDate") Date startRunDate,
                                             @Param("endRunDate") Date endRunDate,
                                             @Param("serviceType") String serviceType,
                                             @Param("direction") String direction);

    List<TriplogSimpleDto> selectByDirectionAndTime(@Param("routeId") Long routeId,
                                                    @Param("startRunDate") Date startRunDate,
                                                    @Param("endRunDate") Date endRunDate,
                                                    @Param("serviceType") String serviceType,
                                                    @Param("direction") String direction);

    List<TriplogSimpleDto> selectBySdAndEdAndSt(@Param("routeId") Long routeId,
                                                @Param("startRunDate") Date startRunDate,
                                                @Param("endRunDate") Date endRunDate,
                                                @Param("serviceType") String serviceType);

    List<TriplogSimpleDto> selectListByRouteId(@Param("routeId") Long routeId, @Param("runDate") Date runDate);

    List<RouteAvgMileage> selectAvgMileage();

    List<RefuelDto> selectRefuel(@Param("routeId") Long routeId);

    List<TriplogSimpleDto> selectListByRouteBusId(@Param("routeId") Long routeId, @Param("busId") Long busId);

    List<TriplogSimpleDto> selectListByRouteBusIdRunDate(@Param("routeId") Long routeId,
                                                         @Param("busId") Long busId,
                                                         @Param("runDate") Date runDate);

    List<TriplogSimpleDto> selectByEmployeeId(@Param("busId") Long employeeId,
                                              @Param("startTime") Date startTime);

    List<TriplogSimpleDto> selectByToStationId(@Param("toStationId") Long toStationId,
                                               @Param("startTime") Date startTime,
                                               @Param("tripTime") Date tripTime);
}
