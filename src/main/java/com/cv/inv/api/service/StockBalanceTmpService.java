/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.StockBalanceTmp;

import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface StockBalanceTmpService {

     StockBalanceTmp save(StockBalanceTmp balance);

     List<StockBalanceTmp> search(String machineId);

}
