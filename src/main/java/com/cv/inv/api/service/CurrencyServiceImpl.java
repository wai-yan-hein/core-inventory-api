/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.CurrencyDao;
import com.cv.inv.api.entity.Currency;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author WSwe
 */
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    CurrencyDao dao;

    @Override
    public Currency save(Currency cur) {
        cur = dao.save(cur);
        return cur;
    }

    @Override
    public Currency findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Currency> search(String code, String name) {
        return dao.search(code, name);
    }

    @Override
    public int delete(String code, String compCode) {
        return dao.delete(code, compCode);
    }
}
