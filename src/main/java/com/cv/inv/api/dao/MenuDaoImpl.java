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
        return getByKey(id);
    }

    @Override
    public List<Menu> search(String compCode, String nameMM, String parentId, String coaCode) {
        String strSql = "select o from Menu o ";
        String strFilter = "";

        if (!compCode.equals("-")) {
            strFilter = "o.compCode = '" + compCode + "'";
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
                strFilter = "o.sourceAccCode = " + coaCode;
            } else {
                strFilter = strFilter + " and o.sourceAccCode = " + coaCode;
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        return (List<Menu>) findHSQL(strSql);
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from Menu o where o.code = '" + id + "'";
        return execUpdateOrDelete(strSql);
    }

    @Override
    public List<Menu> getParentChildMenu() {
        String strSql = "select o from Menu o where o.parent = '1'";
        List<Menu> listRootMenu = findHSQL(strSql);
        listRootMenu.forEach(this::getChild);

        return listRootMenu;
    }

    private Menu getChild(Menu parent) {
        String strSql = "select o from Menu o where o.parent = '" + parent.getCode() + "'";
        List<Menu> listChild = findHSQL(strSql);

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                listChild.forEach(this::getChild);
            }
        }

        return parent;
    }

    @Override
    public List getParentChildMenu(String roleCode, String menuType, String compCode) {
        String strSql = "select o from VRoleMenu o where o.key.roleCode = '" + roleCode
                + "' and o.parent = '1' and o.compCode = '" + compCode + "' order by o.orderBy";
        List listRootMenu = findHSQL(strSql);
        for (Object rootMenu : listRootMenu) {
            VRoleMenu parent = (VRoleMenu) rootMenu;
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
                for (Object o : listChild) {
                    VRoleMenu child = (VRoleMenu) o;
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
        for (Object rootMenu : listRootMenu) {
            Menu parent = (Menu) rootMenu;
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
                for (Object o : listChild) {
                    Menu child = (Menu) o;
                    getChildSelect(child, roleCode, menuType);
                }
            }
        }
    }

    @Override
    public List<Menu> searchM(String updatedDate) {
        String strSql = "select o from Menu o where o.updatedDate > '" + updatedDate + "'";
        return (List<Menu>) findHSQL(strSql);

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
