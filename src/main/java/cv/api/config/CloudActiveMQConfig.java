/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Conditional(CloudActiveMqCondition.class)
@Configuration
@PropertySource(value = {"file:config/application.properties"})
@EnableJms
public class CloudActiveMQConfig {
    @Autowired
    Environment environment;

    @Bean
    public ActiveMQConnectionFactory cloudConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(environment.getRequiredProperty("cloud.activemq.url"));
        connectionFactory.setTrustedPackages(List.of("com.cv.api"));
        return connectionFactory;
    }

    @Bean(name = "cloudMQTemplate")
    public JmsTemplate cloudMQTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setDefaultDestinationName(environment.getRequiredProperty("cloud.activemq.server.queue"));
        template.setConnectionFactory(cloudConnectionFactory());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory cloudJmsListener() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cloudConnectionFactory());
        factory.setConcurrency("1-1");
        log.info("cloud active mq configured.");
        return factory;
    }
}
