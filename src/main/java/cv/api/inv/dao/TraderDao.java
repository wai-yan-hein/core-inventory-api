/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Trader;
import cv.api.inv.entity.TraderKey;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * @author WSwe
 */
 public interface TraderDao {

    Trader findById(TraderKey key);

    List<Trader> searchTrader(String str,String type, String compCode,Integer deptId);

     Trader saveTrader(Trader trader);

    List<Trader> search(String regionCode, String coaCode);

    List<Trader> findAll(String compCode);

    int delete(TraderKey code);

    List<Trader> findCustomer(String compCode,Integer deptId);

    List<Trader> findSupplier(String compCode,Integer deptId);

    List<Trader> unUploadTrader();
    Date getMaxDate();
    List<Trader> getTrader(String updatedDate);

}
