/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHisDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class PurchaseDetailDaoImpl extends AbstractDao<String, PurHisDetail> implements PurchaseDetailDao {

    @Override
    public PurHisDetail save(PurHisDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<PurHisDetail> search(String vouId) {
        String hsql = "select o from PurHisDetail o where o.purDetailKey.vouId = '" + vouId + "' order by o.uniqueId";
        return findHSQL(hsql);

    }

    @Override
    public int delete(String id) throws Exception {
        String strSql = "delete from pur_his_detail where pur_detail_code = '" + id + "'";
        execSQL(strSql);
        return 1;
    }
}
