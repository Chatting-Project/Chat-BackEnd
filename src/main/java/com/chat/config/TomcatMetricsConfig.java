package com.chat.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.metrics.web.tomcat.TomcatMetricsBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatMetricsConfig {

    @Bean
    public TomcatMetricsBinder tomcatMetricsBinder(MeterRegistry registry) {
        return new TomcatMetricsBinder(registry);
    }
}
