/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.UsrCompRole;
import cv.api.inv.entity.UsrCompRoleKey;
import cv.api.inv.entity.VUsrCompAssign;

import java.util.List;

/**
 *
 * @author winswe
 */
 public interface UsrCompRoleService {

     UsrCompRole save(UsrCompRole ucr);

     UsrCompRole findById(UsrCompRoleKey key);

     List<UsrCompRole> search(String userCode, String compCode, String roleId);
     List<VUsrCompAssign> getAssignCompany(String userCode);

     int delete(String userCode, String compCode, String roleId);


}
