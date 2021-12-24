package com.cv.inv.api;

import com.cv.inv.api.common.Util1;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Session;

@Slf4j
@AllArgsConstructor
@Component
public class MessageSender {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Environment environment;
    private static final String SENDER_QUEUE = "STOCK_QUEUE";

    public void sendMessage(String entity, String vouNo) throws Exception{
        if (Util1.getBoolean(environment.getRequiredProperty("use.activemq"))) {
            MessageCreator mc = (Session session) -> {
                MapMessage mm = session.createMapMessage();
                mm.setString("ENTITY", entity);
                mm.setString("CODE", vouNo);
                return mm;
            };
            jmsTemplate.send(SENDER_QUEUE, mc);
            log.info(String.format("sendMessage: %s : %s", entity, vouNo));
        }
    }
}
