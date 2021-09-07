/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHis;


import java.util.List;

import com.cv.inv.api.view.VPurchase;
import org.springframework.stereotype.Repository;

/**
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class PurHisDaoImpl extends AbstractDao<String, PurHis> implements PurHisDao {


    @Override
    public PurHis save(PurHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusCode,
                               String vouNo, String userCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.equals("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!cusCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.trader.code = '" + cusCode + "'";
            } else {
                strFilter = strFilter + " and o.trader.code = '" + cusCode + "'";
            }
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        String strSql = "select o from PurHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate desc";
        }

        return (List<PurHis>) findHSQL(strSql);
    }

    @Override
    public PurHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        String strSql = "update pur_his set deleted = true where vou_no = '" + vouNo + "'";
        execSQL(strSql);
        return 1;
    }

    @Override
    public List<VPurchase> search(String vouNo) {
        String hsql = "select o from VPurchase o where o.vouNo = '" + vouNo + "' order by o.uniqueId";
        return findHSQL(hsql);
    }

}
