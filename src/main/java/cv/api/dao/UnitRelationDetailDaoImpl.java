/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.UnitRelationDetail;
import cv.api.entity.UnitRelationDetailKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class UnitRelationDetailDaoImpl extends AbstractDao<UnitRelationDetailKey, UnitRelationDetail> implements UnitRelationDetailDao {


    @Override
    public UnitRelationDetail save(UnitRelationDetail unit) {
        saveOrUpdate(unit,unit.getKey());
        return  unit;
    }

    @Override
    public List<UnitRelationDetail> getRelationDetail(String code, String compCode) {
        String hsql;
        if (code.equals("-")) {
            hsql = "select o from UnitRelationDetail o where o.key.compCode ='" + compCode + "'";
        } else {
            hsql = "select o from UnitRelationDetail o where o.key.relCode = '" + code + "' and o.key.compCode ='" + compCode + "'";
        }
        return findHSQL(hsql);    }

    @Override
    public UnitRelationDetail findByKey(UnitRelationDetailKey key) {
        return getByKey(key);
    }
}
