/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.VouStatus;
import cv.api.inv.entity.VouStatusKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class VouStatusDaoImpl extends AbstractDao<VouStatusKey, VouStatus> implements VouStatusDao {

    @Override
    public VouStatus save(VouStatus vouStatus) {
        persist(vouStatus);
        return vouStatus;
    }

    @Override
    public List<VouStatus> findAll(String compCode, Integer deptId) {
        String hsql = "select o from VouStatus o where o.key.compCode = '" + compCode + "' and o.key.deptId ='" + deptId + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from VouStatus o where o.vouStatusId='" + id + "'";
        return execUpdateOrDelete(hsql);
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
}
