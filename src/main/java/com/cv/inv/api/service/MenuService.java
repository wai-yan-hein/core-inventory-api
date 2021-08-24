/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.Menu;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface MenuService {

    public Menu saveMenu(Menu menu);

    public Menu findById(String id);

    public List<Menu> search(String compCode, String nameMM, String parentId, String coaCode);

    public int delete(String id);

    public List<Menu> getParentChildMenu();

    public List getParentChildMenu(String roleId, String menuType, String compCode);

    public List getParentChildMenuSelect(String roleId, String menuType);

    public List<Menu> searchM(String updatedDate);

    public List getReports(String roleId);

    public List getReportList(String roleId, String partentCode);

}
