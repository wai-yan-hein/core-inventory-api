/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.RetOutHisDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class RetOutDetailDaoImpl extends AbstractDao<String, RetOutHisDetail> implements RetOutDetailDao {

    @Override
    public List<RetOutHisDetail> search(String code) {

        String hsql = "select v from RetOutHisDetail v where v.roKey.vouNo = '" + code + "' "
                + " order by v.uniqueId";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) throws Exception {
        String strSql = "delete from ret_out_detail_his where rd_code = '" + id + "'";
        execSQL(strSql);
        return 1;
    }

    @Override
    public RetOutHisDetail save(RetOutHisDetail pd) {
        persist(pd);
        return pd;
    }
}
