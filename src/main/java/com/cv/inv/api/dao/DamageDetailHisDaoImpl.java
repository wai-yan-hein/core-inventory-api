/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.DamageDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class DamageDetailHisDaoImpl extends AbstractDao<Long, DamageDetailHis> implements DamageDetailHisDao {

    @Override
    public DamageDetailHis save(DamageDetailHis sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public DamageDetailHis findById(Long id) {
        DamageDetailHis sdh = getByKey(id);
        return sdh;
    }

    @Override
    public List<DamageDetailHis> search(String saleInvId) {
        String strFilter = "";
          if (!saleInvId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.dmgVouId = '" + saleInvId+"'";
            } else {
                strFilter = strFilter + " and v.dmgVouId = '" + saleInvId+"'";
            }
        }
            String strSql = "select v from DamageDetailHis v";

        List<DamageDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from DamageDetailHis o where o.dmgDetailId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

}
