/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * @author Lenovo
 */
@Repository
public class ReportDaoImpl extends AbstractDao<Serializable, Object> implements ReportDao {

    @Override
    public void executeSql(String... sql) throws Exception {
        execSQL(sql);
    }

    @Override
    public ResultSet executeSql(String sql) throws Exception {
        return getResultSet(sql);
    }
}
