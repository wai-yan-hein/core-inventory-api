/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.AppUser;

import java.util.List;

/**
 *
 * @author winswe
 */
 public interface UserService {
     AppUser save(AppUser user);
     List<AppUser> search(String id, String userShort, String email, String owner);
     int delete(String userCode);
     AppUser login(String userShort, String password);
     AppUser findById(String id);
}
