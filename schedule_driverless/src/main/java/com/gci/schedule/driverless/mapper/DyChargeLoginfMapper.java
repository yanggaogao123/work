package com.gci.schedule.driverless.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gci.schedule.driverless.bean.bs.DyChargeLoginf;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DyChargeLoginfMapper extends BaseMapper<DyChargeLoginf> {
    int deleteByPrimaryKey(Long id);

    int insert(DyChargeLoginf record);

    int insertSelective(DyChargeLoginf record);

    DyChargeLoginf selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyChargeLoginf record);

    int updateLogStatusById(DyChargeLoginf record);
}