/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.AccKey;
import cv.api.entity.AccSetting;

import java.util.List;

/**
 * @author wai yan
 */
public interface AccSettingDao {

    List<AccSetting> findAll(String comCope);

    AccSetting save(AccSetting setting);

    AccSetting findByCode(AccKey key);

}
