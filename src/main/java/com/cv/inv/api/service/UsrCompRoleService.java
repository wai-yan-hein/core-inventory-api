/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.UsrCompRole;
import com.cv.inv.api.entity.UsrCompRoleKey;
import java.util.List;

/**
 *
 * @author winswe
 */
 public interface UsrCompRoleService {

     UsrCompRole save(UsrCompRole ucr);

     UsrCompRole findById(UsrCompRoleKey key);

     List<UsrCompRole> search(String userCode, String compCode, String roleId);

     List getAssignRole(String userCode, String compCode);

     List getAssignCompany(String userCode);

     List getAssignCompany(String userCode, String roleId, String compCode);

     int delete(String userCode, String compCode, String roleId);

     List getAssignCompanySelect(String userId) throws Exception;

}
