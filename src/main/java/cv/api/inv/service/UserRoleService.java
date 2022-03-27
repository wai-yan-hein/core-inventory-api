/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.UserRole;

import java.util.List;

/**
 *
 * @author winswe
 */
 public interface UserRoleService {

     UserRole save(UserRole role);

     UserRole findById(Integer id);

    List<UserRole> search(String compCode);

     int delete(String id);

     List<UserRole> searchM(String updatedDate);
}
