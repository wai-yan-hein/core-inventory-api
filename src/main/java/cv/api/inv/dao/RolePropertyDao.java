/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.RoleProperty;
import cv.api.inv.entity.RolePropertyKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface RolePropertyDao {

    RoleProperty save(RoleProperty prop);

    RoleProperty findByKey(RolePropertyKey key);

    List<RoleProperty> getRoleProperty(String roleCode);

    void delete(RoleProperty p);


}
