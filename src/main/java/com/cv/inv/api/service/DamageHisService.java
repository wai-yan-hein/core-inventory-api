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

    public DamageHis save(DamageHis sdh);

    public List<DamageHis> search(String from, String to, String location,
            String remark, String vouNo);

    public void save(DamageHis sdh, List<DamageDetailHis> listDamageDetail,
            String vouStatus, List<String> delList);

    public DamageHis findById(String id);

    public int delete(String vouNo);
}
