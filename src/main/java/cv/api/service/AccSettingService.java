/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.AccKey;
import cv.api.entity.AccSetting;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
public interface AccSettingService {

    List<AccSetting> findAll(String compCode);

    AccSetting save(AccSetting setting);

    AccSetting findByCode(AccKey key);

    List<AccSetting> getAccSetting(LocalDateTime updatedDate);

}
