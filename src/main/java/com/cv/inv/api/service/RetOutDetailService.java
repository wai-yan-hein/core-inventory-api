/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.RetOutHisDetail;

import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface RetOutDetailService {

     RetOutHisDetail save(RetOutHisDetail pd);

     List<RetOutHisDetail> search(String glCode);

     int delete(String id) throws Exception;

    //   void save(PurHis gl, List<PurchaseDetail> pd,List<String> delList);
}
