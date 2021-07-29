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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
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
    public List getAssignRole(String userCode, String compCode) {
        return dao.getAssignRole(userCode, compCode);
    }

    @Override
    public List getAssignCompany(String userCode) {
        return dao.getAssignCompany(userCode);
    }

    @Override
    public List getAssignCompany(String userCode, String roleId, String compCode) {
        return dao.getAssignCompany(userCode, roleId, compCode);
    }

    @Override
    public List getAssignCompanySelect(String userId) throws Exception {
        ResultSet rs = dao.getAssignCompanySelect(userId);
        List listC = null;

        if (rs != null) {
            listC = new ArrayList();
            while (rs.next()) {
                VUsrCompAssign vs = new VUsrCompAssign();
                UsrCompRoleKey key = new UsrCompRoleKey();
                key.setUserCode(rs.getString("user_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setRoleCode(rs.getString("role_code"));
                vs.setCompName(rs.getString("name"));
                vs.setFinicialPeriodFrom(rs.getDate("finicial_period_from"));
                vs.setFinicialPeriodTo(rs.getDate("finicial_period_to"));
                vs.setKey(key);
                listC.add(vs);
            }

            rs.close();
        }
        return listC;
    }

    public int delete(String userCode, String compCode, String roleId) {
        return dao.delete(userCode, compCode, roleId);

    }
}
