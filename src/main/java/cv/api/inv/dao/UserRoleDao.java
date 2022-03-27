/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.UserRole;

import java.util.List;

/**
 * @author winswe
 */
public interface UserRoleDao {

    UserRole save(UserRole role);

    UserRole findById(Integer id);

    List<UserRole> search(String compCode);

    List<UserRole> searchM(String updatedDate);

    int delete(String id);
}
