/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.StockReceiveDetailHisDao;
import cv.api.inv.entity.StockReceiveDetailHis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class StockReceiveDetailHisServiceImpl implements StockReceiveDetailHisService {

    @Autowired
    private StockReceiveDetailHisDao dao;

    @Override
    public List<StockReceiveDetailHis> search(String dmgVouId) {
        return dao.search(dmgVouId);
    }

}
