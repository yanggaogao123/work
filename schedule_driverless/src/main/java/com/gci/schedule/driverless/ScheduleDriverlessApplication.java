package com.gci.schedule.driverless;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(basePackages = {"com.gci.schedule.driverless.mapper", "com.gci.schedule.driverless.dynamic.mapper"})
@SpringBootApplication(scanBasePackages = "com.gci.schedule.driverless",exclude = {DataSourceAutoConfiguration.class})
@EnableAsync
@EnableScheduling
public class ScheduleDriverlessApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleDriverlessApplication.class, args);
    }

}
