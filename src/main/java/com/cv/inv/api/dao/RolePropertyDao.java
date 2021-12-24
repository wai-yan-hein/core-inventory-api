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

/**
 * @author Lenovo
 */
public interface RolePropertyDao {

    RoleProperty save(RoleProperty prop);

    RoleProperty findByKey(RolePropertyKey key);

    List<RoleProperty> getRoleProperty(String roleCode);


}
