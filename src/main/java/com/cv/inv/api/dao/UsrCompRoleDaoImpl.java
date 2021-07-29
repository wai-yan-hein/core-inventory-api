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
        UsrCompRole ucr = getByKey(key);
        return ucr;
    }

    @Override
    public List<UsrCompRole> search(String userCode, String compCode, String roleCode) {
        String strSql = "select o from UsrCompRole o ";
        String strFilter = "";

        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.key.userCode = " + userCode;
            } else {
                strFilter = strFilter + " and o.key.userCode = " + userCode;
            }
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

        List<UsrCompRole> listUsrCompRole = findHSQL(strSql);
        return listUsrCompRole;
    }

    @Override
    public List getAssignRole(String userCode, String compCode) {
        String strSql = "select o from VUsrCompRole o where o.key.userCode = '" + userCode + "' "
                + "and o.key.compCode ='" + compCode + "'";
        List listAssignRole = findHSQL(strSql);
        return listAssignRole;
    }

    @Override
    public List getAssignCompany(String userCode) {
        String strSql = "select o from VUsrCompAssign o where o.key.userCode = '" + userCode + "'";
        List listAssignComp = findHSQL(strSql);
        return listAssignComp;
    }

    @Override
    public List getAssignCompany(String userCode, String roleCode, String compCode) {
        String strSql = "select o from VUsrCompAssign o where o.key.userCode = "
                + userCode + " and o.key.roleCode = " + roleCode + " and o.key.compCode = " + compCode;
        List listAssignComp = findHSQL(strSql);
        return listAssignComp;
    }

    @Override
    public ResultSet getAssignCompanySelect(String userCode) throws Exception {
        /* String strSql = "select ucr from UsrCompRole ucr  where ucr.key.userCode= '" + userCode + "' and "
                + "ucr.key.compCode in (select  ci.compCode from CompanyInfo ci)";*/
        String strSql = " SELECT ci.comp_code ,ci.name,ci.finicial_period_from,\n"
                + "        ci.finicial_period_to,ucr.user_code,ucr.role_code\n"
                + "    FROM\n"
                + "        (company_info ci\n"
                + "        JOIN usr_comp_role ucr)\n"
                + "    WHERE\n"
                + "        ci.comp_code = ucr.comp_code  and ucr.user_code='" + userCode + "'";
        ResultSet rs = getResultSet(strSql);
        return rs;
    }

    @Override
    public int delete(String userCode, String compCode, String roleCode) {
        String strSql = "delete from UsrCompRole o where o.key.userCode = '"
                + userCode + "' and o.key.compCode = '" + compCode + "' and o.key.roleCode = '" + roleCode + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }
}
