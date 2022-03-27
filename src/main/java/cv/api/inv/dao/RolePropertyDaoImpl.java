/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.RoleProperty;
import cv.api.inv.entity.RolePropertyKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class RolePropertyDaoImpl extends AbstractDao<RolePropertyKey, RoleProperty> implements RolePropertyDao {

    @Override
    public RoleProperty save(RoleProperty prop) {
        persist(prop);
        return prop;
    }

    @Override
    public RoleProperty findByKey(RolePropertyKey key) {
        return getByKey(key);
    }

    @Override
    public List<RoleProperty> getRoleProperty(String roleCode) {
        String hsql = "select o from RoleProperty o where o.key.roleCode ='" + roleCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public void delete(RoleProperty p) {
        deleteEntity(p);
    }

}
