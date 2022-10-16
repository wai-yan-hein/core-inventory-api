package cv.api.auto;

import cv.api.common.Util1;
import cv.api.inv.entity.ReorderLevel;
import cv.api.inv.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Session;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Slf4j
@PropertySource(value = {"file:config/application.properties"})
@Component
public class InvScheduler {
    @Autowired
    @Qualifier("topicSender")
    private JmsTemplate topicSender;
    @Autowired
    private ReportService reportService;
    @Value("${default.compcode}")
    private String compCode;
    @Value("${reorder.alert}")
    private String alert;
    String exportPath = String.format("temp%s%s.json", File.separator, "ReorderLevel");

    @Scheduled(fixedRate = 60 * 60 * 1000)
    protected void notificationScheduler() {
        if (Util1.getBoolean(alert)) {
            String type = "REORDER";
            try {
                reportService.generateReorder(compCode);
                List<ReorderLevel> filter = reportService.getReorderLevel("-", "-", "-", "-", compCode,1, 0);
                if (!filter.isEmpty()) {
                    Util1.writeJsonFile(filter, exportPath);
                    byte[] file = new FileInputStream(exportPath).readAllBytes();
                    sendMessage(type, file);
                }
            } catch (Exception e) {
                log.error(String.format("notificationScheduler: %s", e.getMessage()));
            }
        }
    }

    private void sendMessage(String type, byte[] file) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("MSG_TYPE", type);
            mm.setBytes("FILE", file);
            return mm;
        };
        topicSender.send(mc);
        log.info(String.format("sendMessage: %s", type));
    }
}
