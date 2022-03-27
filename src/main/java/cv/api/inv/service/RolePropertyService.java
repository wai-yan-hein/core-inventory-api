/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.RoleDefault;
import cv.api.inv.entity.RoleProperty;
import cv.api.inv.entity.RolePropertyKey;

import java.util.List;

/**
 * @author wai yan
 */
public interface RolePropertyService {

    RoleProperty save(RoleProperty prop);

    RoleProperty findByKey(RolePropertyKey key);

    List<RoleProperty> getRoleProperty(String roleCode);

    RoleDefault getRoleDefault(List<RoleProperty> property);

    void delete(RoleProperty r);
}
