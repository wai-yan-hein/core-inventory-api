/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import java.sql.ResultSet;

/**
 * @author wai yan
 */
public interface ReportDao {
    void executeSql(String... sql);

    ResultSet executeSql(String sql, Object... params);
    ResultSet executeSql(String sql);

}
