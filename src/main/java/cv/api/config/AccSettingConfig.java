package cv.api.config;

import cv.api.common.Util1;
import cv.api.entity.AccSetting;
import cv.api.service.AccSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.List;

@Configuration
@Slf4j
@PropertySource(value = {"file:config/application.properties"})
public class AccSettingConfig {
    @Autowired
    private AccSettingService accSettingService;
    @Autowired
    private Environment environment;

    @Bean
    public HashMap<String, AccSetting> hmAccSetting() {
        Util1.SYNC_DATE = environment.getProperty("sync.date");
        HashMap<String, AccSetting> hmAccSetting = new HashMap<>();
        List<AccSetting> list = accSettingService.findAll();
        if (!list.isEmpty()) {
            for (AccSetting setting : list) {
                hmAccSetting.put(setting.getType(), setting);
            }
            log.info("Account Setting configured.");
        } else {
            throw new IllegalStateException("Account Setting need to configure.");
        }
        return hmAccSetting;
    }
}
