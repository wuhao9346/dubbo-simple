package com.wh.dubbo.base.config;

import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingDefaultTracingHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingReceiverTracingHandler;
import org.apache.skywalking.apm.toolkit.micrometer.observation.SkywalkingSenderTracingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservationConfiguration {
    @ConditionalOnBean(ObservationRegistry.class)
    @Bean
    ApplicationModel applicationModel(@Autowired ObservationRegistry observationRegistry) {
        ApplicationModel applicationModel = ApplicationModel.defaultModel();
        observationRegistry.observationConfig()
                .observationHandler(new ObservationHandler.FirstMatchingCompositeObservationHandler(
                        new SkywalkingSenderTracingHandler(), new SkywalkingReceiverTracingHandler(),
                        new SkywalkingDefaultTracingHandler()
                ));
        applicationModel.getBeanFactory().registerBean(observationRegistry);
        return applicationModel;
    }
}