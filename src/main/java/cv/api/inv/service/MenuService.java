/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.Menu;
import cv.api.inv.entity.VRoleMenu;

import java.util.List;

/**
 * @author winswe
 */
public interface MenuService {

    Menu saveMenu(Menu menu);

    void deleteMenu(Menu menu);

    Menu findById(String id);

    List<Menu> search(String compCode, String nameMM, String parentId, String coaCode);

    int delete(String id);

    List<VRoleMenu> getParentChildMenu(String roleId, String menuType, String compCode);

    List<VRoleMenu> getReports(String roleId);

    List<Menu> getMenuTree(String compCode);

    List<Menu> getParentMenu(String compCode);


}
