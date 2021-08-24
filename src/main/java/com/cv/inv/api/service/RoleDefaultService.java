/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.SystemSetting;
import com.cv.inv.api.entity.RoleDefault;
import com.cv.inv.api.entity.RoleDefaultKey;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface RoleDefaultService {

    public RoleDefault save(RoleDefault du);

    public RoleDefault findById(RoleDefaultKey key);

    public List<RoleDefault> search(String roleCode, String compCode, String key);

    public List<RoleDefault> search(String user);

    public void delete(String roleCode, String compCode, String key);

    public SystemSetting loadSS(String roleCode);

    public SystemSetting saveSS(SystemSetting ss);
}
