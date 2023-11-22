/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.AccSettingDao;
import cv.api.entity.AccKey;
import cv.api.entity.AccSetting;
import cv.api.entity.OutputCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class AccSettingServiceImpl implements AccSettingService {

    @Autowired
    private AccSettingDao dao;

    @Override
    public List<AccSetting> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public AccSetting save(AccSetting setting) {
        return dao.save(setting);
    }

    @Override
    public AccSetting findByCode(AccKey key) {
        return dao.findByCode(key);
    }

    @Override
    public List<AccSetting> getAccSetting(LocalDateTime updatedDate) {
        return dao.getAccSetting(updatedDate);
    }
}
