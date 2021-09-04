/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UserRole;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class UserRoleDaoImpl extends AbstractDao<Integer, UserRole> implements UserRoleDao {

    @Override
    public UserRole save(UserRole role) {
        persist(role);
        return role;
    }

    @Override
    public UserRole findById(Integer id) {
        return getByKey(id);
    }

    @Override
    public List<UserRole> search(String roleName, String compCode) {
        String strSql = "select o from UserRole o ";
        String strFilter = "";

        if (!roleName.equals("-")) {
            strFilter = "o.roleName like '" + roleName + "%'";
        }

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode = '" + compCode + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        return (List<UserRole>) findHSQL(strSql);
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from UserRole o where o.roleCode = " + id;
        return execUpdateOrDelete(strSql);
    }

    @Override
    public List<UserRole> searchM(String updatedDate) {
        String strSql = "select o from UserRole o where o.updatedDate > '" + updatedDate + "'";
        return (List<UserRole>) findHSQL(strSql);
    }
}
