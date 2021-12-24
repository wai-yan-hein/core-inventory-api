/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import java.sql.ResultSet;

/**
 * @author Lenovo
 */
public interface ReportDao {
    void executeSql(String... sql) throws Exception;
    ResultSet executeSql(String sql) throws Exception;


}
