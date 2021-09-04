/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.TransferHis;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * @author lenovo
 */
@Repository
public class TransferHisDaoImpl extends AbstractDao<String, TransferHis> implements TransferHisDao {

    @Override
    public TransferHis save(TransferHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public TransferHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<TransferHis> search(String from, String to, String location, String remark, String vouNo) {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            strFilter = "v.tranDate between '" + from
                    + "' and '" + to + "'";
        } else if (!from.equals("-")) {
            strFilter = "v.tranDate >= '" + from + "'";
        } else if (!to.equals("-")) {
            strFilter = "v.tranDate <= '" + to + "'";
        }

        if (!location.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.location = " + location;
            } else {
                strFilter = strFilter + " and v.location = " + location;
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.remark like '%" + remark + "%'";
            } else {
                strFilter = strFilter + " like v.remark '%" + remark + "%'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.tranVouId like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " like v.tranVouId '%" + vouNo + "%'";
            }
        }

        String strSql = "select distinct v from TransferHis v";

        List<TransferHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from TransferDetailHis o where o.tranVouId = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from TransferHis o where o.tranVouId = '" + vouNo + "'";
        return execUpdateOrDelete(strSql);
    }

}
