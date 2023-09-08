package com.gci.schedule.driverless.mapper;

import com.gci.schedule.driverless.bean.bs.BsSysDdl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BsSysDdlMapper {
    int deleteByPrimaryKey(Long sysDdlId);

    int insert(BsSysDdl record);

    int insertSelective(BsSysDdl record);

    BsSysDdl selectByPrimaryKey(Long sysDdlId);

    int updateByPrimaryKeySelective(BsSysDdl record);

    int updateByPrimaryKey(BsSysDdl record);

    @Cacheable(unless = "#result == null", cacheNames = "selectSysDdlBySysType")
    List<BsSysDdl> selectSysDdlBySysType(String sysType);
}