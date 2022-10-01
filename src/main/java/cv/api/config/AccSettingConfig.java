package cv.api.config;

import cv.api.inv.entity.AccSetting;
import cv.api.inv.service.AccSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;

@Configuration
@Slf4j
public class AccSettingConfig {
    @Autowired
    private  AccSettingService accSettingService;

    @Bean
    public HashMap<String, AccSetting> hmAccSetting() {
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
