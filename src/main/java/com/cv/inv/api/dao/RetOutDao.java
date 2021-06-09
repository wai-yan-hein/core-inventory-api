/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetOutHis;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface RetOutDao {

    public RetOutHis save(RetOutHis retOutHis);

    public RetOutHis findById(String id);

    public List<RetOutHis> search(String fromDate, String toDate,
            String cusId, String locId, String vouNo, String filterCode);

    public ResultSet searchM(String fromDate, String toDate,
            String cusId, String locId, String vouNo, String filterCode) throws Exception;

    public int delete(String id) throws Exception;

}
