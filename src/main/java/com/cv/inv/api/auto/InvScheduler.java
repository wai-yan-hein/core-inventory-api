package com.cv.inv.api.auto;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.entity.ReorderLevel;
import com.cv.inv.api.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@PropertySource(value = {"file:config/application.properties"})
@Component
public class InvScheduler {
    private final String topic = "INV_MESSAGE";
    @Autowired
    private JmsTemplate template;
    @Autowired
    private ReportService reportService;
    @Value("${default.compcode}")
    private String compCode;
    String exportPath = String.format("temp%s%s.json", File.separator, "ReorderLevel");

    @Scheduled(fixedRate = 10 * 60 * 1000)
    protected void notificationScheduler() {
        String type = "REORDER";
        try {
            reportService.generateReorder(compCode);
            List<ReorderLevel> reorderLevel = reportService.getReorderLevel(compCode);
            List<ReorderLevel> filter = new ArrayList<>();
            for (ReorderLevel ol : reorderLevel) {
                float minQty = ol.getMinQty();
                float balQty = ol.getBalQty();
                float orderQty = minQty - balQty;
                if (orderQty > 0) {
                    ol.setOrderQty(orderQty);
                    ol.setOrderUnit(ol.getBalUnit());
                    filter.add(ol);
                }
            }
            if (!filter.isEmpty()) {
                Util1.writeJsonFile(filter, exportPath);
                byte[] file = IOUtils.toByteArray(new FileInputStream(exportPath));
                sendMessage(type, file);
            }

        } catch (Exception e) {
            log.error(String.format("notificationScheduler: %s", e.getMessage()));
        }
    }

    private void sendMessage(String type, byte[] file) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("MSG_TYPE", type);
            mm.setBytes("FILE", file);
            return mm;
        };
        template.send(mc);
        log.info(String.format("sendMessage: %s", type));
    }
}
