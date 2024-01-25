/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class ReportDaoImpl extends AbstractDao<Serializable, Object> implements ReportDao {

    @Override
    public void executeSql(String... sql) {
//        log.info(Arrays.toString(sql));
        execSql(sql);
    }

    @Override
    public ResultSet getResultSql(String sql, Object... params) {
        return getResult(sql, params);
    }

    @Override
    public ResultSet executeSql(String sql) {
//        log.info(sql);
        return getResult(sql);
    }
}
