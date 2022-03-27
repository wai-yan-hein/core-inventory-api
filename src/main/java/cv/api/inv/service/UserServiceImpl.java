/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.AccountDao;
import cv.api.inv.entity.AppUser;
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
public class UserServiceImpl implements UserService {

    @Autowired
    private AccountDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public AppUser save(AppUser user) {
        if (user.getAppUserCode() == null || user.getAppUserCode().isEmpty()) {
            Integer macId = user.getMacId();
            String compCode = user.getCompCode();
            user.setAppUserCode(getAppUserCode(macId, "AppUser", "-", compCode));
        }

        dao.saveAccount(user);
        return user;
    }

    @Override
    public List<AppUser> search(String id, String userShort, String email, String owner) {
        return dao.search(id, userShort, email, owner);
    }

    @Override
    public int delete(String userCode) {
        return dao.delete(userCode);
    }

    @Override
    public AppUser login(String userShort, String password) {
        List<AppUser> listAU = dao.search("-", userShort, "-", "-");
        AppUser au = null;

        if (listAU != null) {
            if (!listAU.isEmpty()) {
                au = listAU.get(0);
            }
        }

        return au;
    }

    @Override
    public AppUser findById(String id) {
        return dao.findById(id);
    }

    private String getAppUserCode(Integer macId, String option, String period, String compCode) {
        int seqNo = seqService.getSequence(macId, option, period, compCode);
        return String.format("%0" + 2 + "d", macId) + "-" + String.format("%0" + 3 + "d", seqNo);
    }
}
