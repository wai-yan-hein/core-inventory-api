/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import java.sql.ResultSet;

/**
 * @author wai yan
 */
public interface ReportDao {
    void executeSql(String... sql) throws Exception;

    ResultSet executeSql(String sql) throws Exception;


}
