/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.AccSetting;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class AccSettingDaoImpl extends AbstractDao<String, AccSetting> implements AccSettingDao {


    @Override
    public List findAll() {
        String hsql = "select o from AccSetting o";
        return findHSQL(hsql);
    }

    @Override
    public AccSetting save(AccSetting setting) {
        persist(setting);
        return setting;

    }

    @Override
    public AccSetting findByCode(String code) {
        return getByKey(code);
    }

}
