/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.SaleHis;
import com.cv.inv.api.view.VSale;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface SaleHisService {

    SaleHis save(SaleHis saleHis) throws Exception;

    List<SaleHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String userCode);

    SaleHis findById(String id);

    int delete(String vouNo) throws Exception;

    List<VSale> search(String vouNo);
}
