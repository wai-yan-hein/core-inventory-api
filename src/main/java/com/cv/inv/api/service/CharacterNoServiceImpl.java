/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.CharacterNoDao;
import com.cv.inv.api.entity.CharacterNo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Transactional
@Service
public class CharacterNoServiceImpl implements CharacterNoService {

    @Autowired
    private CharacterNoDao dao;

    @Override
    public CharacterNo save(CharacterNo ch) {
        return dao.save(ch);
    }

    @Override
    public List<CharacterNo> findAll() {
        return dao.findAll();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    @Override
    public CharacterNo findById(String id) {
        return dao.findById(id);
    }

}
