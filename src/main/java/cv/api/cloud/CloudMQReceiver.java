package cv.api.cloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Slf4j
@Component
public class CloudMQReceiver {

    @JmsListener(destination = "princess.server")
    public void receivedMessage(final MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        String type = message.getString("TYPE");
        log.info(entity);
        log.info(type);

    }

}
