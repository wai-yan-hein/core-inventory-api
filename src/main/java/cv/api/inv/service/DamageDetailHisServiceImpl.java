/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.DamageDetailHisDao;
import cv.api.inv.entity.DamageDetailHis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class DamageDetailHisServiceImpl implements DamageDetailHisService {

    @Autowired
    private DamageDetailHisDao dao;

    @Override
    public List<DamageDetailHis> search(String dmgVouId) {
        return dao.search(dmgVouId);
    }

}
