/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.SystemSetting;
import com.cv.inv.api.dao.CurrencyDao;
import com.cv.inv.api.dao.LocationDao;
import com.cv.inv.api.dao.RoleDefaultDao;
import com.cv.inv.api.dao.SaleManDao;
import com.cv.inv.api.dao.TraderDao;
import com.cv.inv.api.entity.RoleDefault;
import com.cv.inv.api.entity.RoleDefaultKey;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class RoleDefaultServiceImpl implements RoleDefaultService {

    @Autowired
    private RoleDefaultDao dao;
    @Autowired
    private CurrencyDao curDao;
    @Autowired
    private LocationDao locDao;
    @Autowired
    private SaleManDao smDao;
    @Autowired
    private TraderDao traderDao;

    @Override
    public RoleDefault save(RoleDefault du) {
        return dao.save(du);
    }

    @Override
    public List<RoleDefault> search(String user) {
        return dao.search(user);

    }

    @Override
    public RoleDefault findById(RoleDefaultKey key) {
        return dao.findById(key);
    }

    @Override
    public List<RoleDefault> search(String userCode, String compCode, String key) {
        return dao.search(userCode, compCode, key);
    }

    @Override
    public void delete(String roleCode, String compCode, String key) {
        dao.delete(roleCode, compCode, key);
    }

    @Override
    public SystemSetting loadSS(String roleCode) {
        SystemSetting ss = new SystemSetting();
        RoleDefault defCur = dao.findById(new RoleDefaultKey(ss.getCurKey(), roleCode));
        if (defCur != null) {
            ss.setDefaultCurrency(curDao.findById(defCur.getValue()));
        }
        RoleDefault defLoc = dao.findById(new RoleDefaultKey(ss.getLocKey(), roleCode));
        if (defLoc != null) {
            ss.setDefaultLocation(locDao.findByCode(defLoc.getValue()));
        }
        RoleDefault defSM = dao.findById(new RoleDefaultKey(ss.getSaleManKey(), roleCode));
        if (defSM != null) {
            ss.setDefaultSaleMan(smDao.findByCode(defSM.getValue()));
        }
        RoleDefault deCus = dao.findById(new RoleDefaultKey(ss.getSaleManKey(), roleCode));
        if (deCus != null) {
            ss.setDefaultCustomer(traderDao.findByCode(deCus.getValue()));
        }
        RoleDefault defSup = dao.findById(new RoleDefaultKey(ss.getSaleManKey(), roleCode));
        if (defSup != null) {
            ss.setDefaultSupplier(traderDao.findByCode(defSup.getValue()));
        }
        RoleDefault cashDown = dao.findById(new RoleDefaultKey(ss.getCashDownKey(), roleCode));
        ss.setCashDown(cashDown == null ? "false" : cashDown.getValue());
        return ss;
    }

    @Override
    public SystemSetting saveSS(SystemSetting ss) {
        RoleDefault rd;
        String roleCode = ss.getRoleCode();
        if (ss.getDefaultCurrency() != null) {
            rd = new RoleDefault();
            rd.setKey(new RoleDefaultKey(ss.getCurKey(), roleCode));
            rd.setValue(ss.getDefaultCurrency().getCurCode());
            dao.save(rd);
        }
        if (ss.getDefaultCustomer() != null) {
            rd = new RoleDefault();
            rd.setKey(new RoleDefaultKey(ss.getCusKey(), roleCode));
            rd.setValue(ss.getDefaultCustomer().getCode());
            dao.save(rd);
        }
        if (ss.getDefaultSupplier() != null) {
            rd = new RoleDefault();
            rd.setKey(new RoleDefaultKey(ss.getSupKey(), roleCode));
            rd.setValue(ss.getDefaultSupplier().getCode());
            dao.save(rd);
        }
        if (ss.getDefaultSaleMan() != null) {
            rd = new RoleDefault();
            rd.setKey(new RoleDefaultKey(ss.getSaleManKey(), roleCode));
            rd.setValue(ss.getDefaultSaleMan().getSaleManCode());
            dao.save(rd);
        }
        if (ss.getDefaultLocation() != null) {
            rd = new RoleDefault();
            rd.setKey(new RoleDefaultKey(ss.getLocKey(), roleCode));
            rd.setValue(ss.getDefaultLocation().getLocationCode());
            dao.save(rd);
        }
        if (ss.getCashDown() != null) {
            rd = new RoleDefault();
            rd.setKey(new RoleDefaultKey(ss.getCashDownKey(), roleCode));
            rd.setValue(ss.getCashDown());
            dao.save(rd);
        }
        return loadSS(roleCode);
    }

}
