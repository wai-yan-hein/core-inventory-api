/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHisDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Repository
public class PurHisDetailDaoImpl extends AbstractDao<String, PurHisDetail> implements PurHisDetailDao {


    @Override
    public PurHisDetail save(PurHisDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<PurHisDetail> search(String vouNo) {
        String hsql = "select o from PurHisDetail o where o.pdKey.vouNo = '" + vouNo + "' order by o.uniqueId";
        return findHSQL(hsql);

    }

    @Override
    public int delete(String id) throws Exception {
        String strSql = "delete from pur_his_detail where pur_detail_code = '" + id + "'";
        execSQL(strSql);
        return 1;
    }
}
