/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.SaleDetailKey;
import cv.api.inv.entity.SaleHisDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
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
    public int delete(String code) {
        String strSql = "delete from sale_his_detail where sd_code = '" + code + "'";
        execSQL(strSql);
        return 1;
    }

}
