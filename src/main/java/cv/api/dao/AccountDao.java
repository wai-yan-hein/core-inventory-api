/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.AppUser;

import javax.naming.AuthenticationException;
import java.util.List;

/**
 * @author WSwe
 */
public interface AccountDao {

    AppUser saveAccount(AppUser au);

    AppUser findUserById(Integer id);

    AppUser findUserByShort(String userShort);

    AppUser findUserByEmail(String email);

    List<AppUser> search(String id, String userShort, String email, String owner);

    AppUser login(String user, String password) throws AuthenticationException;

    int delete(String userCode);

    AppUser findById(String id);

    List<AppUser> findAll(String compCode);
}
