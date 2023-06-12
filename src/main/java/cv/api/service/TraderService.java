/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.entity.Trader;
import cv.api.entity.TraderKey;

import java.util.Date;
import java.util.List;

/**
 * @author WSwe
 */
public interface TraderService {

    Trader findById(TraderKey key);

    Trader findByRFID(String rfId, String compCode, Integer deptId);

    List<Trader> searchTrader(String str, String type, String compCode, Integer deptId);

    List<Trader> search(String regionCode, String coaCode);


    Trader saveTrader(Trader trader);

    List<Trader> findAll(String compCode);
    List<Trader> findAll();

    List<Trader> findCustomer(String compCode, Integer deptId);

    List<Trader> findSupplier(String compCode, Integer deptId);

    List<General> delete(TraderKey key);

    List<Trader> unUploadTrader();

    Date getMaxDate();

    List<Trader> getTrader(String updatedDate);

}
