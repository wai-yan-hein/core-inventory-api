/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.AccSetting;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class AccSettingDaoImpl extends AbstractDao<String, AccSetting> implements AccSettingDao {

    @Override
    public List<AccSetting> findAll() {
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
