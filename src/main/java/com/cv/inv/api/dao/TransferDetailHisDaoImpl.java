/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.TransferDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class TransferDetailHisDaoImpl extends AbstractDao<Long, TransferDetailHis> implements TransferDetailHisDao {

    @Override
    public TransferDetailHis save(TransferDetailHis sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public TransferDetailHis findById(Long id) {
        TransferDetailHis sdh = getByKey(id);
        return sdh;
    }

    @Override
    public List<TransferDetailHis> search(String saleInvId) {
        String strFilter = "";
          if (!saleInvId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.tranVouId = '" + saleInvId+"'";
            } else {
                strFilter = strFilter + " and v.tranVouId = '" + saleInvId+"'";
            }
        }
            String strSql = "select v from TransferDetailHis v";

        List<TransferDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from TransferDetailHis o where o.tranDetailId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

}
