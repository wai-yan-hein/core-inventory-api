/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RelationKey;
import com.cv.inv.api.entity.UnitRelation;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class RelationDaoImpl extends AbstractDao<RelationKey, UnitRelation> implements RelationDao {

    @Override
    public UnitRelation save(UnitRelation relation) {
        persist(relation);
        return relation;
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        UnitRelation byKey = getByKey(key);
        return byKey;
    }

    @Override
    public List<UnitRelation> findAll() {
        String hsql = "select o from UnitRelation o";
        List<UnitRelation> listRelation = findHSQL(hsql);
        return listRelation;
    }

    @Override
    public List<UnitRelation> search(String patternId) {
        String hsql = "select o from UnitRelation o where o.unitKey.patternId = '" + patternId + "'";
        return findHSQL(hsql);
    }

}
