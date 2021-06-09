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

    public SaleMan save(SaleMan saleMan);

    public List<SaleMan> findAll();

    public int delete(String id);

    public SaleMan findById(String id);
}
