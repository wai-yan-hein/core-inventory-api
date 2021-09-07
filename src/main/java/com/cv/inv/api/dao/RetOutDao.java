/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetOutHis;
import com.cv.inv.api.view.VReturnOut;

import java.util.List;

/**
 * @author lenovo
 */
public interface RetOutDao {

    RetOutHis save(RetOutHis saleHis) throws Exception;

    List<RetOutHis> search(String fromDate, String toDate, String cusCode,
                           String vouNo, String userCode);

    RetOutHis findById(String id);

    int delete(String vouNo) throws Exception;

    List<VReturnOut> search(String vouNo);
}
