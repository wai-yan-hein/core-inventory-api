/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.RetOutHis;
import cv.api.inv.entity.RetOutHisKey;
import cv.api.inv.view.VReturnOut;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class RetOutDaoImpl extends AbstractDao<RetOutHisKey, RetOutHis> implements RetOutDao {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private RetOutDetailDao dao;

    @Override
    public RetOutHis save(RetOutHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from RetOutHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public RetOutHis findById(RetOutHisKey id) {
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

    @Override
    public List<RetOutHis> unUploadVoucher(String syncDate) {
        String hsql = "select o from RetOutHis o where o.intgUpdStatus is null and date(o.vouDate) >= '" + syncDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<RetOutHis> unUpload() {
        String hsql = "select o from RetOutHis o where o.intgUpdStatus ='ACK'";
        List<RetOutHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListRD(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }
}
