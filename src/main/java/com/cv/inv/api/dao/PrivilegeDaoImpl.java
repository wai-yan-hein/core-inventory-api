/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Privilege;
import com.cv.inv.api.entity.PrivilegeKey;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class PrivilegeDaoImpl extends AbstractDao<PrivilegeKey, Privilege> implements PrivilegeDao {


    @Override
    public Privilege save(Privilege privilege) {
        persist(privilege);
        return privilege;
    }

}
