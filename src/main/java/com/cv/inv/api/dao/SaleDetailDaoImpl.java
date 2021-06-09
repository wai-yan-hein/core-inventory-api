/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.SaleDetailKey;
import com.cv.inv.api.entity.SaleHisDetail;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class SaleDetailDaoImpl extends AbstractDao<SaleDetailKey, SaleHisDetail> implements SaleDetailDao {

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public List<SaleHisDetail> search(String vouId) {
        String hsql = "select o from SaleHisDetail o where o.saleDetailKey.vouId is '" + vouId + "' order by o.uniqueId";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from sale_his_detail where sale_detail_id = '" + id + "' ";
        try {
            execSQL(strSql);
        } catch (Exception ex) {
            Logger.getLogger(SaleDetailDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }

}
