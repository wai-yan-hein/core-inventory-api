/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.UserRole;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface UserRoleService {

    public UserRole save(UserRole role);

    public UserRole findById(Integer id);

    public List<UserRole> search(String roleName, String compCode);

    public int delete(String id);

    public UserRole copyRole(String copyRoleId, String compCode);

    public List<UserRole> searchM(String updatedDate);
}
