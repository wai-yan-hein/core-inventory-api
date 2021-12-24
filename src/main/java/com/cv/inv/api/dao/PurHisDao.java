/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHis;
import com.cv.inv.api.view.VPurchase;

import java.util.List;

/**
 * @author Mg Kyaw Thura Aung
 */
public interface PurHisDao {

    PurHis save(PurHis ph);

    List<PurHis> search(String fromDate, String toDate, String cusCode,
                        String vouNo, String userCode);

    PurHis findById(String id);

    int delete(String vouNo) throws Exception;

    List<VPurchase> search(String vouNo);

}
