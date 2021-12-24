/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.MenuDao;
import com.cv.inv.api.dao.PrivilegeDao;
import com.cv.inv.api.dao.UserRoleDao;
import com.cv.inv.api.entity.Menu;
import com.cv.inv.api.entity.Privilege;
import com.cv.inv.api.entity.PrivilegeKey;
import com.cv.inv.api.entity.UserRole;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
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
            ur.setRoleCode(getUserRoleCode(macId, "UserRole", "-", compCode));
            savePrivilege(compCode, ur.getRoleCode());
        }
        return dao.save(ur);
    }

    @Override
    public UserRole findById(Integer id) {
        return dao.findById(id);
    }

    @Override
    public List<UserRole> search(String roleName, String compCode) {
        return dao.search(roleName, compCode);
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public UserRole copyRole(String copyRoleId, String compCode) {
        UserRole old = findById(Integer.parseInt(copyRoleId));
        UserRole newRole = new UserRole();

        BeanUtils.copyProperties(old, newRole);
        newRole.setRoleCode(null);
        newRole.setCompCode(compCode);

        return save(newRole);
    }

    @Override
    public List<UserRole> searchM(String updatedDate) {
        return dao.searchM(updatedDate);
    }

    private String getUserRoleCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);

        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }

    private void savePrivilege(String compCode, String roleCode) {
        List<Menu> listM = menuDao.search(compCode, "-", "-", "-");
        if (!listM.isEmpty()) {
            listM.stream().map(_item -> new Privilege(new PrivilegeKey(roleCode, compCode), false)).forEachOrdered(p -> pDao.save(p));
        }
    }
}
