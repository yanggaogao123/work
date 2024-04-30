package com.gci.schedule.driverless.config;

import com.gci.schedule.driverless.util.DataSourceUtil;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
/**
 * 动态数据源类
 * @date 2022/2/11
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String db = DataSourceUtil.getDB();
        return db;
    }
}
