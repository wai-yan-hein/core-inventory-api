/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Topic;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Configuration
@Conditional(ActiveMqCondition.class)
@PropertySource(value = {"file:config/application.properties"})
@EnableJms
public class ActiveMQConfig {
    @Autowired
    Environment environment;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(environment.getRequiredProperty("activemq.url"));
        connectionFactory.setTrustedPackages(List.of("com.cv.integration"));
        return connectionFactory;
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("INV_MSG");
    }

    @Bean(name = "topicSender")
    public JmsTemplate topicSender() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setDefaultDestination(topic());
        template.setPubSubDomain(true);
        return template;
    }

    @Bean(name = "queueSender")
    public JmsTemplate queueSender() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-1");
        log.info("active mq configured.");
        return factory;
    }
}
