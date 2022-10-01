package cv.api.cloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Session;

@Slf4j
@Component
public class CloudMQSender {
    //@Autowired
   // private JmsTemplate cloudMQTemplate;

    //@Scheduled(fixedRate = 100)
    private void requestStock() {
        log.info("requestStock");
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", "SETUP");
            mm.setString("TYPE", "STOCK");
            return mm;
        };
        //cloudMQTemplate.send(mc);
    }
}
