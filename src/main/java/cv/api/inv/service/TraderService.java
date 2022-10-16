/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.Trader;
import cv.api.inv.entity.TraderKey;

import java.util.List;

/**
 * @author WSwe
 */
public interface TraderService {

    Trader findById(TraderKey key);

    List<Trader> searchTrader(String str, String type, String compCode, Integer deptId);

    List<Trader> search(String regionCode, String coaCode);


    Trader saveTrader(Trader trader) throws Exception;

    List<Trader> findAll(String compCode);

    List<Trader> findCustomer(String compCode, Integer deptId);

    List<Trader> findSupplier(String compCode, Integer deptId);

    List<String> delete(TraderKey key);

    List<Trader> unUploadTrader();
}
