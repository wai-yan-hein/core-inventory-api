/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.VouStatus;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class VouStatusDaoImpl extends AbstractDao<String, VouStatus> implements VouStatusDao {

    @Override
    public VouStatus save(VouStatus vouStatus) {
        persist(vouStatus);
        return vouStatus;
    }

    @Override
    public List<VouStatus> findAll() {
        String hsql = "select o from VouStatus o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from VouStatus o where o.vouStatusId='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public VouStatus findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<VouStatus> search(String statusDesp) {
        String strSql = "";

        if (!statusDesp.equals("-")) {
            if (strSql.isEmpty()) {
                strSql = "o.statusDesp like '%" + statusDesp + "%'";
            } else {
                strSql = strSql + " and o.statusDesp like '%" + statusDesp + "%'";
            }
        }

        if (strSql.isEmpty()) {
            strSql = "select o from VouStatus o";
        } else {
            strSql = "select o from VouStatus o where " + strSql;
        }

        List<VouStatus> listVS = findHSQL(strSql);
        return listVS;
    }
}
