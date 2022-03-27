package cv.api.inv.dao;

import cv.api.inv.entity.UnitRelation;
import cv.api.inv.entity.UnitRelationDetail;
import cv.api.inv.entity.UnitRelationDetailKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UnitRelationDaoImpl extends AbstractDao<String, UnitRelation> implements UnitRelationDao {
    @Override
    public UnitRelation save(UnitRelation ur) {
        persist(ur);
        return ur;
    }

    @Override
    public List<UnitRelation> findRelation() {
        String hsql = "select o from UnitRelation o";
        return findHSQL(hsql);
    }

    @Override
    public UnitRelationDetail save(UnitRelationDetail unit) {
        getSession().saveOrUpdate(unit);
        return unit;
    }

    @Override
    public List<UnitRelationDetail> getRelationDetail(String code) {
        String hsql = "";
        if (code.equals("-")) {
            hsql = "select o from UnitRelationDetail o";
        } else {
            hsql = "select o from UnitRelationDetail o where o.key.relCode = '" + code + "'";
        }
        return getSession().createQuery(hsql, UnitRelationDetail.class).list();
    }

    @Override
    public UnitRelationDetail findByKey(UnitRelationDetailKey key) {
        return getSession().get(UnitRelationDetail.class, key);
    }
}
