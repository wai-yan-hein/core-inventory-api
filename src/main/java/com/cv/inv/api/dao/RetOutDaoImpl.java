/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetOutHis;
import com.cv.inv.api.view.VReturnOut;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lenovo
 */
@Repository
public class RetOutDaoImpl extends AbstractDao<String, RetOutHis> implements RetOutDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public RetOutHis save(RetOutHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from RetOutHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public RetOutHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        String strSql = "update ret_in_his set deleted = true where voucher_no = '" + vouNo + "'";
        execSQL(strSql);
        return 1;
    }

    @Override
    public List<VReturnOut> search(String vouNo) {
        String hsql = "select o from VReturnOut o where o.vouNo = '" + vouNo + "' order by o.uniqueId";
        return sessionFactory.getCurrentSession().createQuery(hsql, VReturnOut.class).list();
    }
}
