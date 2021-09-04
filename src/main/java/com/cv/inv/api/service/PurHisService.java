/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.PurHis;

import java.util.List;

/**
 * @author Mg Kyaw Thura Aung
 */
 public interface PurHisService {


    PurHis save(PurHis ph) throws Exception;

    List<PurHis> search(String fromDate, String toDate, String cusCode,
                        String vouNo, String userCode);

    PurHis findById(String id);

    int delete(String vouNo) throws Exception;
}
