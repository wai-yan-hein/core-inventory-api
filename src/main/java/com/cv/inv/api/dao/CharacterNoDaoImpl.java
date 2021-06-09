/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.CharacterNo;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class CharacterNoDaoImpl extends AbstractDao<String, CharacterNo> implements CharacterNoDao {

    @Override
    public CharacterNo save(CharacterNo ch) {
        persist(ch);
        return ch;
    }

    @Override
    public List<CharacterNo> findAll() {
        String hsql = "select o from CharacterNo o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String hsql = "delete from CharacterNo o where o.ch='" + id + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public CharacterNo findById(String id) {
        return getByKey(id);
    }

    @Override
    public Object search(String id) {
        String sql = "select from CharacterNo where o.ch ='" + id + "'";
        return getAggregate(sql);
    }

}
