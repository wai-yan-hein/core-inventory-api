/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RoleProperty;
import com.cv.inv.api.entity.RolePropertyKey;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
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
    public HashMap<String, String> getRoleProperty(String roleCode) {
        HashMap<String, String> hm = new HashMap<>();
        String hsql = "select o from RoleProperty o where o.key.roleCode ='" + roleCode + "'";
        List<RoleProperty> listRP = findHSQL(hsql);
        if (!listRP.isEmpty()) {
            listRP.forEach(rp -> hm.put(rp.getKey().getPropKey(), rp.getRoleValue()));
        }
        return hm;
    }

}
