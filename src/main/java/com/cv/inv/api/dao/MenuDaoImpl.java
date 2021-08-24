/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Menu;
import com.cv.inv.api.entity.VRoleMenu;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class MenuDaoImpl extends AbstractDao<String, Menu> implements MenuDao {

    @Override
    public Menu saveMenu(Menu menu) {
        persist(menu);
        return menu;
    }

    @Override
    public Menu findById(String id) {
        Menu menu = getByKey(id);
        return menu;
    }

    @Override
    public List<Menu> search(String compCode, String nameMM, String parentId, String coaCode) {
        String strSql = "select o from Menu o ";
        String strFilter = "";

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode  '" + compCode + "'";
            }
        }

        if (!nameMM.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.menuNameMM like '" + nameMM + "%'";
            } else {
                strFilter = strFilter + " and o.menuNameMM like '" + nameMM + "%'";
            }
        }

        if (!parentId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.parent = " + parentId;
            } else {
                strFilter = strFilter + " and o.parent = " + parentId;
            }
        }
        if (!coaCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.soureAccCode = " + coaCode;
            } else {
                strFilter = strFilter + " and o.soureAccCode = " + coaCode;
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<Menu> listMenu = findHSQL(strSql);
        return listMenu;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from Menu o where o.code = '" + id + "'";
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

    @Override
    public List<Menu> getParentChildMenu() {
        String strSql = "select o from Menu o where o.parent = '1'";
        List<Menu> listRootMenu = findHSQL(strSql);
        listRootMenu.forEach(parent -> {
            getChild(parent);
        });

        return listRootMenu;
    }

    private Menu getChild(Menu parent) {
        String strSql = "select o from Menu o where o.parent = '" + parent.getCode() + "'";
        List<Menu> listChild = findHSQL(strSql);

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                listChild.forEach(parentMenu -> {
                    getChild(parentMenu);
                });
            }
        }

        return parent;
    }

    @Override
    public List getParentChildMenu(String roleCode, String menuType, String compCode) {
        String strSql = "select o from VRoleMenu o where o.key.roleCode = '" + roleCode
                + "' and o.parent = '1' and o.compCode = '" + compCode + "' order by o.orderBy";
        List listRootMenu = findHSQL(strSql);
        for (int i = 0; i < listRootMenu.size(); i++) {
            VRoleMenu parent = (VRoleMenu) listRootMenu.get(i);
            getChild(parent, roleCode, menuType, compCode);
        }

        return listRootMenu;
    }

    private void getChild(VRoleMenu parent, String roleCode, String menuType, String compCode) {
        String strSql = "select o from VRoleMenu o where o.parent = '" + parent.getKey().getMenuCode()
                + "' and o.key.roleCode = '" + roleCode + "' and o.compCode = '" + compCode + "'";
        if (!menuType.equals("-")) {
            strSql = strSql + " and o.menuType = '" + menuType + "'";
        }
        List listChild = findHSQL(strSql);

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                for (int i = 0; i < listChild.size(); i++) {
                    VRoleMenu child = (VRoleMenu) listChild.get(i);
                    getChild(child, roleCode, menuType, compCode);
                }
            }
        }
    }

    @Override
    public List getParentChildMenuSelect(String roleCode, String menuType) {
        String strSql = "select m from Menu m where m.parent = '1' and "
                + " m.code in(select p.key.menuCode from Privilege p where p.isAllow=true and p.key.roleCode = " + roleCode + ") order by m.orderBy";
        List listRootMenu = findHSQL(strSql);
        for (int i = 0; i < listRootMenu.size(); i++) {
            Menu parent = (Menu) listRootMenu.get(i);
            getChildSelect(parent, roleCode, "-");
        }

        return listRootMenu;
    }

    private void getChildSelect(Menu parent, String roleCode, String menuType) {
        String strSql = "select m from Menu m where m.parent=" + parent.getCode()
                + " and m.code in(select p.key.menuCode from Privilege p where p.isAllow=true and p.key.roleCode='" + roleCode + "') order by m.orderBy";
        if (!menuType.equals("-")) {
            strSql = strSql + " and m.menuType = '" + menuType + "'";
        }
        List listChild = findHSQL(strSql);

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                for (int i = 0; i < listChild.size(); i++) {
                    Menu child = (Menu) listChild.get(i);
                    getChildSelect(child, roleCode, menuType);
                }
            }
        }
    }

    @Override
    public List<Menu> searchM(String updatedDate) {
        String strSql = "select o from Menu o where o.updatedDate > '" + updatedDate + "'";
        List<Menu> listMU = findHSQL(strSql);
        return listMU;

    }

    @Override
    public List getReports(String roleCode) {
        String hsql = "select o from VRoleMenu o where o.key.roleCode = " + roleCode + " and o.menuType = 'Reports' and o.isAllow = true";
        return findHSQL(hsql);
    }

    @Override
    public List getReportList(String roleCode, String parentCode) {
        String hsql = "select o from VRoleMenu o where o.key.roleCode = " + roleCode + " and o.isAllow = true"
                + "  and o.parent ='" + parentCode + "'";
        return findHSQL(hsql);
    }
}
