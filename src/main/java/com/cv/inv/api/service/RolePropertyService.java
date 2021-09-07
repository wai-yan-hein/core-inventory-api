/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.RoleProperty;
import com.cv.inv.api.entity.RolePropertyKey;
import java.util.HashMap;

/**
 *
 * @author Lenovo
 */
public interface RolePropertyService {

    RoleProperty save(RoleProperty prop);

    RoleProperty findByKey(RolePropertyKey key);

    HashMap<String, String> getRoleProperty(String roleCode);

}
