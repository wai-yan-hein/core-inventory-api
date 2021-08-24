/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleHisDaoImpl extends AbstractDao<String, SaleHis> implements SaleHisDao {

    @Override
    public SaleHis save(SaleHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusCode,
            String vouNo, String userCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.saleDate) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.saleDate) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.saleDate) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(o.saleDate) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.saleDate) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.saleDate) <= '" + toDate + "'";
            }
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
        String strSql = "select o from SaleHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by date(o.saleDate) desc";
        }

        List<SaleHis> listSaleHis = findHSQL(strSql);
        return listSaleHis;
    }

    @Override
    public SaleHis findById(String id) {
        SaleHis sh = getByKey(id);
        return sh;
    }

    @Override
    public int delete(String vouNo) throws Exception {
        String strSql = "update sale_his set deleted = true where voucher_no = '" + vouNo + "'";
        execSQL(strSql);
        return 1;
    }
}
