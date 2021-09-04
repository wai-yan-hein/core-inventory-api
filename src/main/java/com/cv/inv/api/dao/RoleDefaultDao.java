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

/**
 *
 * @author Lenovo
 */
 public interface RoleDefaultDao {

     RoleDefault save(RoleDefault du);

     RoleDefault findById(RoleDefaultKey key);

     List<RoleDefault> search(String user);

     List<RoleDefault> search(String roleCode, String compCode, String key);

     void delete(String roleCode, String compCode, String key);

     SystemSetting loadSS(String roleCode);

}
