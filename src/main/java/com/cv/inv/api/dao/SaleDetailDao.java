/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleHisDetail;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleDetailDao {

    public SaleHisDetail save(SaleHisDetail sdh);

    public List<SaleHisDetail> search(String vouId);
    
     public int delete(String id);
}
