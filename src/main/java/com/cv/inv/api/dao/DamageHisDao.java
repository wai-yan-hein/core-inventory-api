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
 *
 * @author lenovo
 */
public interface DamageHisDao {

    public DamageHis save(DamageHis ph);

    public DamageHis findById(String id);

    public List<DamageHis> search(String from, String to, String location,
            String remark, String vouNo);

    public ResultSet searchM(String from, String to, String location,
            String remark, String vouNo) throws Exception;

    public int delete(String vouNo);
}
