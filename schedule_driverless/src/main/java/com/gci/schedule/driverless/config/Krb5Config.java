package com.gci.schedule.driverless.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2021-05-20 10:06
 * @version: v1.0
 * @Modified by:
 **/
@Configuration
@ConditionalOnProperty(prefix = "spring.kafka.jaas",name = "enabled",havingValue = "true")
public class Krb5Config implements InitializingBean {

    @Value("${spring.kafka.krb5}")
    private String SPRING_KAFKA_KRB5;
    @Value("${spring.kafka.keyTab}")
    private String KAFKA_KEYTAB;

    @Override
    public void afterPropertiesSet() {
        System.setProperty("java.security.auth.login.config", KAFKA_KEYTAB);
        System.setProperty("java.security.krb5.conf", SPRING_KAFKA_KRB5);
    }
}
