/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UsrCompRole;
import com.cv.inv.api.entity.UsrCompRoleKey;
import com.cv.inv.api.entity.VUsrCompAssign;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author winswe
 */
@Repository
public class UsrCompRoleDaoImpl extends AbstractDao<UsrCompRoleKey, UsrCompRole> implements UsrCompRoleDao {

    @Override
    public UsrCompRole save(UsrCompRole ucr) {
        persist(ucr);
        return ucr;
    }

    @Override
    public UsrCompRole findById(UsrCompRoleKey key) {
        return getByKey(key);
    }

    @Override
    public List<UsrCompRole> search(String userCode, String compCode, String roleCode) {
        String strSql = "select o from UsrCompRole o ";
        String strFilter = "";

        if (!userCode.equals("-")) {
            strFilter = "o.key.userCode = " + userCode;
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.key.compCode = '" + compCode + "'";
            }
        }

        if (!roleCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.roleCode = " + roleCode;
            } else {
                strFilter = strFilter + " and o.key.roleCode = " + roleCode;
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        return findHSQL(strSql);
    }

    @Override
    public List<VUsrCompAssign> getAssignCompany(String userCode) {
        String strSql = "select o from VUsrCompAssign o where o.key.userCode = '" + userCode + "'";
        return getSession().createQuery(strSql, VUsrCompAssign.class).list();
    }

    @Override
    public int delete(String userCode, String compCode, String roleCode) {
        String strSql = "delete from UsrCompRole o where o.key.userCode = '"
                + userCode + "' and o.key.compCode = '" + compCode + "' and o.key.roleCode = '" + roleCode + "'";
        return execUpdateOrDelete(strSql);
    }
}
