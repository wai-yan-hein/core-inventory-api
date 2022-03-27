/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.MenuDao;
import cv.api.inv.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author winswe
 */
@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao dao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserRoleService roleService;

    @Override
    public Menu saveMenu(Menu mu) {
        if (Util1.isNull(mu.getCode())) {
            Integer macId = mu.getMacId();
            String compCode = mu.getCompCode();
            mu.setCode(getMenuCode(macId, compCode));
            updatePrivileges(mu.getCode(), mu.getCompCode());
        }
        return dao.saveMenu(mu);
    }

    @Override
    public void deleteMenu(Menu menu) {
        dao.deleteMenu(menu);
    }

    private void updatePrivileges(String menuCode, String compCode) {
        List<UserRole> roles = roleService.search(compCode);
        if (!roles.isEmpty()) {
            for (UserRole role : roles) {
                Privilege p = new Privilege();
                PrivilegeKey key = new PrivilegeKey();
                key.setRoleCode(role.getRoleCode());
                key.setMenuCode(menuCode);
                p.setKey(key);
                p.setAllow(true);
                privilegeService.save(p);
            }
        }
    }

    @Override
    public Menu findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Menu> search(String compCode, String nameMM, String parentId, String coaCode) {
        return dao.search(compCode, nameMM, parentId, coaCode);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }


    @Override
    public List<VRoleMenu> getParentChildMenu(String roleId, String menuType, String compCode) {
        return dao.getParentChildMenu(roleId, menuType, compCode);
    }

    @Override
    public List<VRoleMenu> getReports(String roleId) {
        return dao.getReports(roleId);
    }

    @Override
    public List<Menu> getMenuTree(String compCode) {
        return dao.getMenuTree(compCode);
    }

    @Override
    public List<Menu> getParentMenu(String compCode) {
        return dao.getParentMenu(compCode);
    }


    private String getMenuCode(Integer macId, String compCode) {

        int seqNo = seqService.getSequence(macId, "Menu", "-", compCode);

        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }
}
