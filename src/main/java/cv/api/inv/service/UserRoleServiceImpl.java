/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.MenuDao;
import cv.api.inv.dao.PrivilegeDao;
import cv.api.inv.dao.UserRoleDao;
import cv.api.inv.entity.Menu;
import cv.api.inv.entity.Privilege;
import cv.api.inv.entity.PrivilegeKey;
import cv.api.inv.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author winswe
 */
@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao dao;
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private PrivilegeDao pDao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public UserRole save(UserRole ur) {
        if (Util1.isNull(ur.getRoleCode())) {
            Integer macId = ur.getMacId();
            String compCode = ur.getCompCode();
            ur.setRoleCode(getUserRoleCode(macId, compCode));
            savePrivilege(compCode, ur.getRoleCode());
        }
        return dao.save(ur);
    }

    @Override
    public UserRole findById(Integer id) {
        return dao.findById(id);
    }

    @Override
    public List<UserRole> search(String compCode) {
        return dao.search(compCode);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }


    @Override
    public List<UserRole> searchM(String updatedDate) {
        return dao.searchM(updatedDate);
    }

    private String getUserRoleCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "UserRole", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    private void savePrivilege(String compCode, String roleCode) {
        List<Menu> menus = menuDao.search(compCode, "-", "-", "-");
        if (!menus.isEmpty()) {
            menus.forEach(menu -> {
                Privilege p = new Privilege();
                PrivilegeKey key = new PrivilegeKey();
                key.setMenuCode(menu.getCode());
                key.setRoleCode(roleCode);
                p.setKey(key);
                p.setAllow(false);
                pDao.save(p);
            });
        }

    }
}
