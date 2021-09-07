/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.RoleProperty;
import com.cv.inv.api.entity.RolePropertyKey;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cv.inv.api.dao.RolePropertyDao;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class RolePropertyServiceImpl implements RolePropertyService {

    @Autowired
    private RolePropertyDao dao;

    @Override
    public RoleProperty save(RoleProperty prop) {
        return dao.save(prop);
    }

    @Override
    public RoleProperty findByKey(RolePropertyKey key) {
        return dao.findByKey(key);
    }

    @Override
    public HashMap<String, String> getRoleProperty(String roleCode) {
        return dao.getRoleProperty(roleCode);
    }

}
