/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.AccKey;
import cv.api.entity.AccSetting;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class AccSettingDaoImpl extends AbstractDao<AccKey, AccSetting> implements AccSettingDao {

    @Override
    public List<AccSetting> findAll(String compCode) {
        String hsql = "select o from AccSetting o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public AccSetting save(AccSetting setting) {
        saveOrUpdate(setting,setting.getKey());
        return setting;

    }

    @Override
    public AccSetting findByCode(AccKey key) {
        return getByKey(key);
    }

}
