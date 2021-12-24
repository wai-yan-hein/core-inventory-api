/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleDetailKey;
import com.cv.inv.api.entity.SaleHisDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleHisDetailDaoImpl extends AbstractDao<SaleDetailKey, SaleHisDetail> implements SaleHisDetailDao {

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public List<SaleHisDetail> search(String vouNo) {
        String hsql = "select o from SaleHisDetail o where o.sdKey.vouNo = '" + vouNo + "' order by o.uniqueId";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String vouNo) {
        String strSql = "delete from SaleHisDetail o where o.sdKey.sdCode = '" + vouNo + "'";
        return execUpdateOrDelete(strSql);
    }

}
