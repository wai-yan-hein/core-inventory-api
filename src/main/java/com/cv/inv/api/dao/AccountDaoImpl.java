/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.AppUser;
import java.util.List;
import javax.naming.AuthenticationException;
import org.springframework.stereotype.Repository;

/**
 *
 * @author WSwe
 */
@Repository
public class AccountDaoImpl extends AbstractDao<Integer, AppUser> implements AccountDao {
    //private BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public AppUser saveAccount(AppUser au) {
        persist(au);
        return au;
    }

    @Override
    public AppUser findUserById(Integer id) {
        AppUser au = getByKey(id);
        return au;
    }

    @Override
    public AppUser findUserByShort(String userShort) {
        List<AppUser> listAU = search("-", userShort, "-", "-");
        AppUser au = null;

        if (listAU != null) {
            if (!listAU.isEmpty()) {
                au = listAU.get(0);
            }
        }

        return au;
    }

    @Override
    public AppUser findUserByEmail(String email) {
        List<AppUser> listAU = search("-", "-", email, "-");
        AppUser au = null;

        if (listAU != null) {
            if (!listAU.isEmpty()) {
                au = listAU.get(0);
            }
        }

        return au;
    }

    @Override
    public List<AppUser> search(String id, String userShort, String email, String owner) {

        String strSql = "select o from AppUser o";
        String strFilter = null;

        if (!id.equals("-")) {
            if (strFilter == null) {
                strFilter = "o.userCode like '" + id + "%'";
            } else {
                strFilter = strFilter + " and o.userCode like '" + id + "%'";
            }
        }

        if (!userShort.equals("-")) {
            if (strFilter == null) {
                strFilter = "o.userShort = '" + userShort + "'";
            } else {
                strFilter = strFilter + " and o.userShort = '" + userShort + "'";
            }
        }

        if (!email.equals("-")) {
            if (strFilter == null) {
                strFilter = "o.userCode = '" + email + "'";
            } else {
                strFilter = strFilter + " and o.userCode = '" + email + "'";
            }
        }

        if (!owner.equals("-")) {
            if (strFilter == null) {
                strFilter = "(o.owner = " + owner + " or o.userCode = " + owner + ")";
            } else {
                strFilter = strFilter + " and (o.owner = " + owner + " or o.userCode = " + owner + ")";
            }
        }

        if (strFilter != null) {
            strSql = strSql + " where " + strFilter;
        }

        List<AppUser> listAU = findHSQL(strSql);
        return listAU;
    }

    @Override
    public AppUser login(String user, String password) throws AuthenticationException {
        AppUser au = findUserByShort(user);

        if (au == null) {
            throw new AuthenticationException(
                    "Either username/password is worng.");
        } else if (!au.getActive()) {
            throw new AuthenticationException(
                    "Either username/password is worng.");
        } else if (!au.getPassword().equals(password)) {
            throw new AuthenticationException(
                    "Either username/password is worng.");
        }

        return au;
    }

    @Override
    public int delete(String userCode) {
        String strSql = "delete from AppUser o where o.userCode = " + userCode;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public AppUser finfById(String id) {
        return getByKey(Integer.parseInt(id));
    }

    @Override
    public List<AppUser> findAll(String compCode) {

        String strSql = "select o from AppUser  o where o.compCode = '" + compCode + "'";
        List<AppUser> ListAU = findHSQL(strSql);
        return ListAU;

    }
}
