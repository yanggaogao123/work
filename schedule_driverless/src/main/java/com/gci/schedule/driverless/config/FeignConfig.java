package com.gci.schedule.driverless.config;

import com.gci.schedule.driverless.feign.*;
import com.gci.schedule.driverless.feign.config.DoNotRetryer;
import com.gci.schedule.driverless.feign.config.FastJsonDecoder;
import com.gci.schedule.driverless.feign.config.FastJsonEncoder;
import com.gci.schedule.driverless.feign.config.Slf4jLogger;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: xieqingduan
 * @Description: TODO:
 * @Date: 2020-07-08 14:14
 * @version: v1.0
 * @Modified by:
 **/
@Configuration
public class FeignConfig {

    @Value("${dispatch.server.url}")
    private String DISPATCH_URL;
    @Value("${dispatch.server.connectTimeout}")
    private String DISPATCH_CONNECTTIMEOUT;
    @Value("${dispatch.server.readTimeout}")
    private String DISPATCH_READTIMEOUT;

    @Value("${runbus.server.url}")
    private String RUNBUS_URL;
    @Value("${runbus.server.connectTimeout}")
    private String RUNBUS_CONNECTTIMEOUT;
    @Value("${runbus.server.readTimeout}")
    private String RUNBUS_READTIMEOUT;

    @Value("${schedule.server.url}")
    private String SCHEDULE_URL;
    @Value("${schedule2.server.url}")
    private String SCHEDULE2_URL;
    @Value("${schedule.server.connectTimeout}")
    private String SCHEDULE_CONNECTTIMEOUT;
    @Value("${schedule.server.readTimeout}")
    private String SCHEDULE_READTIMEOUT;

    @Value("${wxxt.server.url}")
    private String WXXT_URL;
    @Value("${wxxt.server.url2}")
    private String WXXT_URL2;
    @Value("${wxxt.server.connectTimeout}")
    private String WXXT_CONNECTTIMEOUT;
    @Value("${wxxt.server.readTimeout}")
    private String WXXT_READTIMEOUT;

    final Decoder decoder = new FastJsonDecoder();
    final Encoder encoder = new FastJsonEncoder();
    final Encoder formEncoder = new FormEncoder(encoder);
    final Retryer retryer = new DoNotRetryer();

    @Value("${predictmileage.server.url}")
    private String PREDICTMILEAGE_URL;

    @Value("${charge.server.url}")
    private String CHARGE_URL;

    @Value("${need.charge.server.url}")
    private String NEED_CHARGE_URL;

    @Value("${aptsBase.server.url}")
    private String APTS_SERVER_URL;

    @Bean
    public WxxtApp wxxtApp() {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(WxxtApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(WXXT_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(WXXT_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(WxxtApp.class, WXXT_URL);
    }

    @Bean
    public WxxtApp2 wxxtApp2() {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(WxxtApp2.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(WXXT_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(WXXT_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(WxxtApp2.class, WXXT_URL2);
    }

    @Bean
    public ScheduleApp scheduleApp() {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(ScheduleApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(SCHEDULE_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(SCHEDULE_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(ScheduleApp.class, SCHEDULE_URL);
    }

    @Bean
    public ScheduleApp scheduleApp2() {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(ScheduleApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(SCHEDULE_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(SCHEDULE_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(ScheduleApp.class, SCHEDULE2_URL);
    }

    @Bean
    public RunBusApp runBusApp() {
        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(RunBusApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(RUNBUS_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(RUNBUS_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(RunBusApp.class, RUNBUS_URL);
    }

    @Bean
    public DispatchApp dispatchApp() {
        return Feign.builder()
                .encoder(formEncoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(DispatchApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(DISPATCH_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(DISPATCH_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(DispatchApp.class, DISPATCH_URL);
    }

    @Bean
    public ChargeApp chargeApp() {
        return Feign.builder()
                .encoder(formEncoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(ChargeApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(DISPATCH_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(DISPATCH_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(ChargeApp.class, CHARGE_URL);
    }

    @Bean
    public PredictmileageApp predictmileageApp() {
        return Feign.builder()
                .encoder(formEncoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(PredictmileageApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(DISPATCH_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(DISPATCH_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(PredictmileageApp.class, PREDICTMILEAGE_URL);
    }

    @Bean
    public NeedChargeApp needChargeApp() {
        return Feign.builder()
                .encoder(formEncoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(NeedChargeApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(DISPATCH_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(DISPATCH_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(NeedChargeApp.class, NEED_CHARGE_URL);
    }

    @Bean
    public AptsBaseApp routeStationApp() {
        return Feign.builder()
                .encoder(formEncoder)
                .decoder(decoder)
                .retryer(retryer)
                .logger(new Slf4jLogger(AptsBaseApp.class))
                .logLevel(Logger.Level.FULL)
                .options(new Request.Options(Long.parseLong(DISPATCH_CONNECTTIMEOUT), TimeUnit.SECONDS,
                        Long.parseLong(DISPATCH_READTIMEOUT), TimeUnit.SECONDS, true))
                .target(AptsBaseApp.class, APTS_SERVER_URL);
    }
}
