/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Menu;
import cv.api.inv.entity.VRoleMenu;
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
    public void deleteMenu(Menu menu) {
        deleteEntity(menu);
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
    public List<VRoleMenu> getReports(String roleCode) {
        String hsql = "select o from VRoleMenu o where o.key.roleCode = '" + roleCode + "'\n" +
                "and o.menuType = 'Report' and o.isAllow = true order by o.menuName ";
        Query<VRoleMenu> query = sessionFactory.getCurrentSession().createQuery(hsql, VRoleMenu.class);
        return query.list();
    }

    @Override
    public List<Menu> getMenuTree(String compCode) {
        String hsql = "select o from Menu o where  o.parent = '1' and o.compCode = '" + compCode + "'";
        List<Menu> menus = findHSQL(hsql);
        for (Menu menu : menus) {
            getChild(menu, compCode);
        }
        return menus;
    }

    @Override
    public List<Menu> getParentMenu(String compCode) {
        String hsql = "select o from Menu o where  o.parent = '1' and o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    private void getChild(Menu parent, String compCode) {
        String hsql = "select o from Menu o where o.parent = '" + parent.getCode()
                + "' and o.compCode = '" + compCode + "'";
        List<Menu> menus = findHSQL(hsql);
        parent.setChild(menus);
        if (!menus.isEmpty()) {
            for (Menu child : menus) {
                getChild(child, compCode);
            }
        }
    }
}
