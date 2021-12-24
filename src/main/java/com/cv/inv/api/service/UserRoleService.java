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

     UserRole save(UserRole role);

     UserRole findById(Integer id);

     List<UserRole> search(String roleName, String compCode);

     int delete(String id);

     UserRole copyRole(String copyRoleId, String compCode);

     List<UserRole> searchM(String updatedDate);
}
