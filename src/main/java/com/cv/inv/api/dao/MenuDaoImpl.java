/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Menu;
import com.cv.inv.api.entity.VRoleMenu;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author winswe
 */
@Repository
public class MenuDaoImpl extends AbstractDao<String, Menu> implements MenuDao {
    @Autowired
    private SessionFactory sessionFactory;

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
        Query<Menu> query = sessionFactory.getCurrentSession().createQuery(strSql, Menu.class);
        return query.list();
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from Menu o where o.code = '" + id + "'";
        return execUpdateOrDelete(strSql);
    }

    @Override
    public List<Menu> getParentChildMenu() {
        String strSql = "select o from Menu o where o.parent = '1'";
        Query<Menu> query = sessionFactory.getCurrentSession().createQuery(strSql, Menu.class);
        List<Menu> listRootMenu = query.list();
        listRootMenu.forEach(this::getChild);

        return listRootMenu;
    }

    private void getChild(Menu parent) {
        String strSql = "select o from Menu o where o.parent = '" + parent.getCode() + "'";
        Query<Menu> query = sessionFactory.getCurrentSession().createQuery(strSql, Menu.class);
        List<Menu> listChild = query.list();

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                listChild.forEach(this::getChild);
            }
        }

    }

    @Override
    public List<VRoleMenu> getParentChildMenu(String roleCode, String menuType, String compCode) {
        String strSql = "select o from VRoleMenu o where o.key.roleCode = '" + roleCode
                + "' and o.parent = '1' and o.compCode = '" + compCode + "' order by o.orderBy";
        Query<VRoleMenu> query = sessionFactory.getCurrentSession().createQuery(strSql, VRoleMenu.class);
        List<VRoleMenu> listRootMenu = query.list();
        for (VRoleMenu menu : listRootMenu) {
            getChild(menu, roleCode, menuType, compCode);
        }

        return listRootMenu;
    }

    private void getChild(VRoleMenu parent, String roleCode, String menuType, String compCode) {
        String strSql = "select o from VRoleMenu o where o.parent = '" + parent.getKey().getMenuCode()
                + "' and o.key.roleCode = '" + roleCode + "' and o.compCode = '" + compCode + "'";
        if (!menuType.equals("-")) {
            strSql = strSql + " and o.menuType = '" + menuType + "'";
        }
        Query<VRoleMenu> query = sessionFactory.getCurrentSession().createQuery(strSql, VRoleMenu.class);
        List<VRoleMenu> listChild = query.list();

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                for (VRoleMenu child : listChild) {
                    getChild(child, roleCode, menuType, compCode);
                }
            }
        }
    }

    @Override
    public List<Menu> getParentChildMenuSelect(String roleCode, String menuType) {
        String strSql = "select m from Menu m where m.parent = '1' and "
                + " m.code in(select p.key.menuCode from Privilege p where p.isAllow=true and p.key.roleCode = " + roleCode + ") order by m.orderBy";
        Query<Menu> query = sessionFactory.getCurrentSession().createQuery(strSql, Menu.class);
        List<Menu> listRootMenu = query.list();
        for (Menu parent : listRootMenu) {
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
        Query<Menu> query = sessionFactory.getCurrentSession().createQuery(strSql, Menu.class);
        List<Menu> listChild = query.list();

        if (listChild != null) {
            if (listChild.size() > 0) {
                parent.setChild(listChild);
                for (Menu child : listChild) {
                    getChildSelect(child, roleCode, menuType);
                }
            }
        }
    }

    @Override
    public List<Menu> searchM(String updatedDate) {
        String strSql = "select o from Menu o where o.updatedDate > '" + updatedDate + "'";
        Query<Menu> query = sessionFactory.getCurrentSession().createQuery(strSql, Menu.class);
        return query.list();

    }

    @Override
    public List<VRoleMenu> getReports(String roleCode) {
        String hsql = "select o from VRoleMenu o where o.key.roleCode = '" + roleCode + "' and o.menuType = 'Report' and o.isAllow = true";
        Query<VRoleMenu> query = sessionFactory.getCurrentSession().createQuery(hsql, VRoleMenu.class);
        return query.list();
    }

    @Override
    public List<VRoleMenu> getReportList(String roleCode, String parentCode) {
        String hsql = "select o from VRoleMenu o where o.key.roleCode = " + roleCode + " and o.isAllow = true"
                + "  and o.parent ='" + parentCode + "'";
        Query<VRoleMenu> query = sessionFactory.getCurrentSession().createQuery(hsql, VRoleMenu.class);
        return query.list();
    }
}
