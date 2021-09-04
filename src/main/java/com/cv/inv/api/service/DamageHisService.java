/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.DamageDetailHis;
import com.cv.inv.api.entity.DamageHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
 public interface DamageHisService {

     DamageHis save(DamageHis sdh);

     List<DamageHis> search(String from, String to, String location,
            String remark, String vouNo);

     void save(DamageHis sdh, List<DamageDetailHis> listDamageDetail,
            String vouStatus, List<String> delList);

     DamageHis findById(String id);

     int delete(String vouNo);
}
