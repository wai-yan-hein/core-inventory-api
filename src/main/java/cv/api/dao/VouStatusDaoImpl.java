/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.VouStatus;
import cv.api.entity.VouStatusKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class VouStatusDaoImpl extends AbstractDao<VouStatusKey, VouStatus> implements VouStatusDao {

    @Override
    public VouStatus save(VouStatus vouStatus) {
        saveOrUpdate(vouStatus,vouStatus.getKey());
        return vouStatus;
    }

    @Override
    public List<VouStatus> findAll(String compCode, Integer deptId) {
        String hsql = "select o from VouStatus o where o.key.compCode = '" + compCode + "' and (o.key.deptId ='" + deptId + "' or 0=" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
       return 1;
    }

    @Override
    public VouStatus findById(VouStatusKey id) {
        return getByKey(id);
    }

    @Override
    public List<VouStatus> search(String statusDesp) {
        String strSql = "";

        if (!statusDesp.equals("-")) {
            strSql = "o.statusDesp like '%" + statusDesp + "%'";
        }

        if (strSql.isEmpty()) {
            strSql = "select o from VouStatus o";
        } else {
            strSql = "select o from VouStatus o where " + strSql;
        }

        return findHSQL(strSql);
    }

    @Override
    public List<VouStatus> unUpload() {
        String hsql = "select o from VouStatus o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from vou_status";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<VouStatus> getVouStatus(String updatedDate) {
        String hsql = "select o from VouStatus o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }
}
