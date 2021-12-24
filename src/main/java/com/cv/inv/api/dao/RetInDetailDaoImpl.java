/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetInHisDetail;
import com.cv.inv.api.entity.RetInKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Repository
public class RetInDetailDaoImpl extends AbstractDao<RetInKey, RetInHisDetail> implements RetInDetailDao {

    @Override
    public RetInHisDetail save(RetInHisDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<RetInHisDetail> search(String retInCode) {
        String strSql = "select o from RetInHisDetail o where o.riKey.vouNo = '" + retInCode + "'"
                + " order by o.uniqueId";
        return findHSQL(strSql);
    }

    @Override
    public int delete(String id) throws Exception {
        String strSql = "delete from ret_in_his_detail where rd_code = '" + id + "'";
        execSQL(strSql);
        return 1;
    }
}
