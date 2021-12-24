/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.RelationDao;
import com.cv.inv.api.entity.RelationKey;
import com.cv.inv.api.entity.UnitRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class RelationServiceImpl implements RelationService {

    @Autowired
    private RelationDao dao;

    @Override
    public UnitRelation save(UnitRelation relation) {
        return dao.save(relation);
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        return dao.findByKey(key);
    }

    @Override
    public List<UnitRelation> findAll() {
        return dao.findAll();
    }

    @Override
    public List<UnitRelation> search(String patternId) {
        return dao.search(patternId);
    }

}
