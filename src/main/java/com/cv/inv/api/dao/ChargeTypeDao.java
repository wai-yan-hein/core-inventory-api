/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.ChargeType;

import java.util.List;

/**
 * @author Mg Kyaw Thura Aung
 */
 public interface ChargeTypeDao {

    ChargeType save(ChargeType chargeType);

    List<ChargeType> findAll();

    int delete(String id);

    List<ChargeType> search(String ctId, String descripiton);
}
