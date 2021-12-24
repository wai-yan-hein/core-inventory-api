/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.PurHis;
import com.cv.inv.api.entity.PurHisDetail;

import java.util.List;

/**
 *
 * @author Lenovo
 */
 public interface PurHisDetailService {

     PurHisDetail save(PurHisDetail pd);
    
     void saveH2(PurHis pur, List<PurHisDetail> listPD, List<String> delList);
     List<PurHisDetail> search(String glCode);

     void save(PurHis gl, List<PurHisDetail> pd, List<String> delList);

     int delete(String code) throws Exception;

}
