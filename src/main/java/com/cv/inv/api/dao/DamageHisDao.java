/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.DamageHis;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author lenovo
 */
public interface DamageHisDao {

    DamageHis save(DamageHis ph);

    DamageHis findById(String id);

    List<DamageHis> search(String from, String to, String location,
                           String remark, String vouNo);

    int delete(String vouNo);
}
