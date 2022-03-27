/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.RoleDefault;
import cv.api.common.Util1;
import cv.api.inv.dao.*;
import cv.api.inv.entity.RoleProperty;
import cv.api.inv.entity.RolePropertyKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * @author wai yan
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
    public RoleDefault getRoleDefault(List<RoleProperty> roleProperty) {
        RoleDefault rd = new RoleDefault();
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
            rd.setDefaultCurrency(currencyDao.findById(Util1.isNull(curCode, "-")));
        }

        return rd;
    }

    @Override
    public void delete(RoleProperty p) {
        dao.delete(p);
    }
}
