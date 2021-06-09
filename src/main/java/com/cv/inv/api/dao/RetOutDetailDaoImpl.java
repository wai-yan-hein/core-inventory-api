/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetOutHisDetail;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class RetOutDetailDaoImpl extends AbstractDao<String, RetOutHisDetail> implements RetOutDetailDao {

    @Override
    public List<RetOutHisDetail> search(String code) {

        String hsql = "select v from RetOutHisDetail v where v.outCompoundKey.vouNo = '" + code + "' "
                + " order by v.uniqueId";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) throws Exception {
        String strSql = "delete from ret_out_detail_his where ret_out_detail_id = '" + id + "'";
        execSQL(strSql);
        return 1;
    }

    @Override
    public RetOutHisDetail save(RetOutHisDetail pd) {
        persist(pd);
        return pd;
    }
}
