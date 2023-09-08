package com.gci.schedule.driverless.mapper;


import com.gci.schedule.driverless.bean.bs.DyPredictMileageRecord;

public interface DyPredictMileageRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DyPredictMileageRecord record);

    int insertSelective(DyPredictMileageRecord record);

    DyPredictMileageRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyPredictMileageRecord record);

    int updateByPrimaryKey(DyPredictMileageRecord record);
}