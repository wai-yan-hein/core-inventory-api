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

    public ActiveMQConnectionFactory connectionFactory(String url) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(url);
        connectionFactory.setTrustedPackages(List.of("com.cv"));
        return connectionFactory;
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("INV_MSG");
    }

    @Bean(name = "topicSender")
    public JmsTemplate topicSender() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory(localUrl()));
        template.setDefaultDestination(topic());
        template.setPubSubDomain(true);
        return template;
    }

    @Bean(name = "queueSender")
    public JmsTemplate queueSender() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory(localUrl()));
        return template;
    }

    @Bean(name = "cloudMQTemplate")
    public JmsTemplate cloudMQTemplate() {
        JmsTemplate template = new JmsTemplate();
        String url = environment.getProperty("cloud.activemq.url");
        template.setConnectionFactory(connectionFactory(url));
        return template;
    }

    @Bean("local")
    public DefaultJmsListenerContainerFactory localListener() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory(localUrl()));
        factory.setConcurrency("1-1");
        log.info("active mq local configured.");
        return factory;
    }

    @Bean("cloud")
    public DefaultJmsListenerContainerFactory cloudListener() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        String url = environment.getProperty("cloud.activemq.url");
        factory.setConnectionFactory(connectionFactory(url));
        factory.setConcurrency("1-1");
        log.info("active mq cloud configured.");
        return factory;
    }

    public String localUrl() {
        return environment.getRequiredProperty("activemq.url");
    }
}
