/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetInHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
 public interface RetInDao {

    RetInHis save(RetInHis saleHis) throws Exception;

    List<RetInHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String userCode);

    RetInHis findById(String id);

    int delete(String vouNo) throws Exception;
}
