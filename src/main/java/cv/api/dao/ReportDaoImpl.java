/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * @author wai yan
 */
@Repository
public class ReportDaoImpl extends AbstractDao<Serializable, Object> implements ReportDao {

    @Override
    public void executeSql(String... sql) {
        execSql(sql);
    }

    @Override
    public ResultSet getResultSql(String sql, Object... params) {
        return getResult(sql, params);
    }

    @Override
    public ResultSet executeSql(String sql)  {
        return getResult(sql);
    }
}
