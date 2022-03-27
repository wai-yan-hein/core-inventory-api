/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.DamageDetailHis;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class DamageDetailHisDaoImpl extends AbstractDao<Long, DamageDetailHis> implements DamageDetailHisDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public DamageDetailHis save(DamageDetailHis sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public DamageDetailHis findById(Long id) {
        return getByKey(id);
    }

    @Override
    public List<DamageDetailHis> search(String saleInvId) {
        String strFilter = "";
        if (!saleInvId.equals("-")) {
            strFilter = "v.dmgVouId = '" + saleInvId + "'";
        }
        String strSql = "select v from DamageDetailHis v";

        List<DamageDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            Query<DamageDetailHis> query = sessionFactory.getCurrentSession().createQuery(strSql, DamageDetailHis.class);
            listDH = query.list();
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from DamageDetailHis o where o.dmgDetailId = " + id;
        return execUpdateOrDelete(strSql);
    }

}
