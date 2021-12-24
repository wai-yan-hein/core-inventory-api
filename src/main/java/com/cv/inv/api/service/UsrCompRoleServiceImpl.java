/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.UsrCompRoleDao;
import com.cv.inv.api.entity.UsrCompRole;
import com.cv.inv.api.entity.UsrCompRoleKey;
import com.cv.inv.api.entity.VUsrCompAssign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author winswe
 */
@Service
@Transactional
public class UsrCompRoleServiceImpl implements UsrCompRoleService {

    @Autowired
    private UsrCompRoleDao dao;

    @Override
    public UsrCompRole save(UsrCompRole ucr) {
        return dao.save(ucr);
    }

    @Override
    public UsrCompRole findById(UsrCompRoleKey key) {
        return dao.findById(key);
    }

    public List<UsrCompRole> search(String userCode, String compCode, String roleId) {
        return dao.search(userCode, compCode, roleId);
    }

    @Override
    public List<VUsrCompAssign> getAssignCompany(String userCode) {
        return dao.getAssignCompany(userCode);
    }

    public int delete(String userCode, String compCode, String roleId) {
        return dao.delete(userCode, compCode, roleId);

    }
}
