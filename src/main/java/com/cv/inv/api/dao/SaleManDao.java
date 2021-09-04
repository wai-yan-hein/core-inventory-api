/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleMan;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
 public interface SaleManDao {

     SaleMan save(SaleMan saleMan);

     List<SaleMan> findAll(String compCode);

     int delete(String id);

     SaleMan findByCode(String code);
}
