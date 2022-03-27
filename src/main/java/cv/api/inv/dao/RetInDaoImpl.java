/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.RetInHis;
import cv.api.inv.view.VReturnIn;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class RetInDaoImpl extends AbstractDao<String, RetInHis> implements RetInDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public RetInHis save(RetInHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusCode,
                                 String vouNo, String remark, String userCode) {
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
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark like '" + remark + "%'";
            } else {
                strFilter = strFilter + " and o.remark like '" + remark + "%'";
            }
        }
        String strSql = "select o from RetInHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public RetInHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        String strSql = "update ret_in_his set deleted = true where voucher_no = '" + vouNo + "'";
        execSQL(strSql);
        return 1;
    }

    @Override
    public List<VReturnIn> search(String vouNo) {
        String hsql = "select o from VReturnIn o where o.vouNo = '" + vouNo + "' order by uniqueId";
        return sessionFactory.getCurrentSession().createQuery(hsql, VReturnIn.class).list();
    }
}
