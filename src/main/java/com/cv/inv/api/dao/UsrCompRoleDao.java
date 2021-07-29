/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.UsrCompRole;
import com.cv.inv.api.entity.UsrCompRoleKey;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface UsrCompRoleDao {

    public UsrCompRole save(UsrCompRole ucr);

    public UsrCompRole findById(UsrCompRoleKey key);

    public List<UsrCompRole> search(String userCode, String compCode, String roleId);

    public List getAssignRole(String userCode, String compCode);

    public List getAssignCompany(String userCode);

    public List getAssignCompany(String userCode, String roleId, String compCode);

    public int delete(String userCode, String compCode, String roleId);

    public ResultSet getAssignCompanySelect(String userId) throws Exception;

}
