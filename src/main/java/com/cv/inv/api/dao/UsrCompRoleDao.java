/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UsrCompRole;
import com.cv.inv.api.entity.UsrCompRoleKey;
import com.cv.inv.api.entity.VUsrCompAssign;

import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author winswe
 */
 public interface UsrCompRoleDao {

     UsrCompRole save(UsrCompRole ucr);

     UsrCompRole findById(UsrCompRoleKey key);

     List<UsrCompRole> search(String userCode, String compCode, String roleId);

     List<VUsrCompAssign> getAssignCompany(String userCode);

     int delete(String userCode, String compCode, String roleId);

}
