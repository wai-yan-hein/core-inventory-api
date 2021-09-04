/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UsrCompRole;
import com.cv.inv.api.entity.UsrCompRoleKey;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
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

        return (List<UsrCompRole>) findHSQL(strSql);
    }

    @Override
    public List getAssignRole(String userCode, String compCode) {
        String strSql = "select o from VUsrCompRole o where o.key.userCode = '" + userCode + "' "
                + "and o.key.compCode ='" + compCode + "'";
        return findHSQL(strSql);
    }

    @Override
    public List getAssignCompany(String userCode) {
        String strSql = "select o from VUsrCompAssign o where o.key.userCode = '" + userCode + "'";
        return findHSQL(strSql);
    }

    @Override
    public List getAssignCompany(String userCode, String roleCode, String compCode) {
        String strSql = "select o from VUsrCompAssign o where o.key.userCode = "
                + userCode + " and o.key.roleCode = " + roleCode + " and o.key.compCode = " + compCode;
        return findHSQL(strSql);
    }

    @Override
    public ResultSet getAssignCompanySelect(String userCode) throws Exception {
        String strSql = " SELECT ci.comp_code ,ci.name,ci.finicial_period_from,\n"
                + "        ci.finicial_period_to,ucr.user_code,ucr.role_code\n"
                + "    FROM\n"
                + "        (company_info ci\n"
                + "        JOIN usr_comp_role ucr)\n"
                + "    WHERE\n"
                + "        ci.comp_code = ucr.comp_code  and ucr.user_code='" + userCode + "'";
        return getResultSet(strSql);
    }

    @Override
    public int delete(String userCode, String compCode, String roleCode) {
        String strSql = "delete from UsrCompRole o where o.key.userCode = '"
                + userCode + "' and o.key.compCode = '" + compCode + "' and o.key.roleCode = '" + roleCode + "'";
        return execUpdateOrDelete(strSql);
    }
}
