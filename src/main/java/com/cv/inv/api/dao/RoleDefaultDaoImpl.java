/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.common.SystemSetting;
import com.cv.inv.api.entity.RoleDefault;
import com.cv.inv.api.entity.RoleDefaultKey;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class RoleDefaultDaoImpl extends AbstractDao<RoleDefaultKey, RoleDefault> implements RoleDefaultDao {

    @Override
    public RoleDefault save(RoleDefault du) {
        persist(du);
        return du;
    }

    @Override
    public List<RoleDefault> search(String user) {
        String hsql = "select o from RoleDefault o where o.key.userCOde = '" + user + "'";
        return findHSQL(hsql);
    }

    @Override
    public RoleDefault findById(RoleDefaultKey key) {
        return getByKey(key);
    }

    @Override
    public List<RoleDefault> search(String roleCode, String compCode, String key) {
        String hsql = "select o from RoleDefault o where o.key.roleCode = '" + roleCode + "'"
                + " and o.key.compCode = '" + compCode + "' and o.key.key = '" + key + "'";
        return findHSQL(hsql);
    }

    @Override
    public void delete(String roleCode, String compCode, String key) {
        try {
            String strSql = "delete from role_setting where role_code = '" + roleCode + "'"
                    + " and comp_code = '" + compCode + "' and default_key = '" + key + "'";
            execSQL(strSql);
        } catch (Exception ignored) {
        }
    }

    @Override
    public SystemSetting loadSS(String roleCode) {
        return null;
    }

}
