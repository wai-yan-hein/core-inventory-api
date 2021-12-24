/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockIssueDetailHis;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author lenovo
 */
@Repository
public class StockIssueDetailHisDaoImpl extends AbstractDao<Long, StockIssueDetailHis> implements StockIssueDetailHisDao {

    @Override
    public StockIssueDetailHis save(StockIssueDetailHis sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public StockIssueDetailHis findById(Long id) {
        return getByKey(id);
    }

    @Override
    public List<StockIssueDetailHis> search(String saleInvId) {
        String strFilter = "";
          if (!saleInvId.equals("-")) {
              strFilter = "v.issueId = '" + saleInvId+"'";
          }
            String strSql = "select v from StockIssueDetailHis v";

        List<StockIssueDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from StockIssueDetailHis o where o.tranId = " + id;
        return execUpdateOrDelete(strSql);
    }

}
