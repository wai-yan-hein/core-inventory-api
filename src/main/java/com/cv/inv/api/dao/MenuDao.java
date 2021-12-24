/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Menu;

import java.util.List;

/**
 *
 * @author winswe
 */
 public interface MenuDao {

     Menu saveMenu(Menu menu);

     Menu findById(String id);

     List<Menu> search(String compCode, String nameMM, String parentId, String coaCode);

     List<Menu> getParentChildMenu();

     List<Menu> searchM(String updatedDate);

     List getParentChildMenu(String roleId, String menuType, String compCode);

     List getParentChildMenuSelect(String roleId, String menuType);

     int delete(String id);

     List getReports(String roleId);

     List getReportList(String roleId, String parentCode);
}
