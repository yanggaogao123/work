package com.gci.schedule.driverless.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gci.schedule.driverless.bean.bs.DyChargeLoginfDetail;

public interface DyChargeLoginfDetailMapper extends BaseMapper<DyChargeLoginfDetail> {
    int deleteByPrimaryKey(Long id);

    int insert(DyChargeLoginfDetail record);

    int insertSelective(DyChargeLoginfDetail record);

    DyChargeLoginfDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DyChargeLoginfDetail record);

    int updateByPrimaryKey(DyChargeLoginfDetail record);
}