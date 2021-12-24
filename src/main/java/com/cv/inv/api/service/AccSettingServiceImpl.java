/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.AccSettingDao;
import com.cv.inv.api.entity.AccSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class AccSettingServiceImpl implements AccSettingService {

    @Autowired
    private AccSettingDao dao;

    @Override
    public List<AccSetting> findAll() {
        return dao.findAll();
    }

    @Override
    public AccSetting save(AccSetting setting) {
        return dao.save(setting);
    }

    @Override
    public AccSetting findByCode(String code) {
        return dao.findByCode(code);
    }

}
