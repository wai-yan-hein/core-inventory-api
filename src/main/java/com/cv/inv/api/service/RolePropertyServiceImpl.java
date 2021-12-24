/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.RoleDefault;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.*;
import com.cv.inv.api.entity.RoleProperty;
import com.cv.inv.api.entity.RolePropertyKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static com.cv.inv.api.common.SettingKey.*;

/**
 * @author Lenovo
 */
@Service
@Transactional
public class RolePropertyServiceImpl implements RolePropertyService {

    @Autowired
    private RolePropertyDao dao;
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private SaleManDao saleManDao;
    @Autowired
    private CurrencyDao currencyDao;
    @Autowired
    private LocationDao locationDao;

    @Override
    public RoleProperty save(RoleProperty prop) {
        return dao.save(prop);
    }

    @Override
    public RoleProperty findByKey(RolePropertyKey key) {
        return dao.findByKey(key);
    }

    @Override
    public List<RoleProperty> getRoleProperty(String roleCode) {
        return dao.getRoleProperty(roleCode);
    }

    @Override
    public RoleDefault getRoleDefault(String roleCode) {
        RoleDefault rd = new RoleDefault();
        List<RoleProperty> roleProperty = getRoleProperty(roleCode);
        if (!roleProperty.isEmpty()) {
            HashMap<String, String> hm = new HashMap<>();
            for (RoleProperty rp : roleProperty) {
                hm.put(rp.getKey().getPropKey(), rp.getPropValue());
            }
            String cusCode = hm.get("default.customer");
            String supCode = hm.get("default.supplier");
            String saleManCode = hm.get("default.saleman");
            String locCode = hm.get("default.location");
            String curCode = hm.get("default.currency");
            rd.setDefaultCustomer(traderDao.findByCode(Util1.isNull(cusCode, "-")));
            rd.setDefaultSupplier(traderDao.findByCode(Util1.isNull(supCode, "-")));
            rd.setDefaultSaleMan(saleManDao.findByCode(Util1.isNull(saleManCode, "-")));
            rd.setDefaultLocation(locationDao.findByCode(Util1.isNull(locCode, "-")));
            rd.setDefaultCurrency(currencyDao.findById(Util1.isNull(cusCode, "-")));
        }

        return rd;
    }
}
