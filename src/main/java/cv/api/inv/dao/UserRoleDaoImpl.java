/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
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
    public List<UserRole> search(String compCode) {
        String strSql = "select o from UserRole o where o.compCode = '" + compCode + "' ";
        return findHSQL(strSql);
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from UserRole o where o.roleCode = " + id;
        return execUpdateOrDelete(strSql);
    }

    @Override
    public List<UserRole> searchM(String updatedDate) {
        String strSql = "select o from UserRole o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(strSql);
    }
}
