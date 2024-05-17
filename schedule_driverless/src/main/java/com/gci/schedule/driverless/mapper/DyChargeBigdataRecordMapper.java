package com.gci.schedule.driverless.mapper;


import com.gci.schedule.driverless.bean.bs.DyChargeBigdataRecord;

public interface DyChargeBigdataRecordMapper {
    int deleteByPrimaryKey(Long id);

    Long insert(DyChargeBigdataRecord record);

    int insertSelective(DyChargeBigdataRecord record);

    DyChargeBigdataRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyChargeBigdataRecord record);

    int updateByPrimaryKey(DyChargeBigdataRecord record);
}