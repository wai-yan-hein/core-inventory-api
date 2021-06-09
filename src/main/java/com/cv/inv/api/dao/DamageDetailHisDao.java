/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.DamageDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface DamageDetailHisDao {

    public DamageDetailHis save(DamageDetailHis sdh);

    public DamageDetailHis findById(Long id);

    public List<DamageDetailHis> search(String saleInvId);

    public int delete(String id);
}
