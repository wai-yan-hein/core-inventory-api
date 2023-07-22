/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.MillingExpenseDao;
import cv.api.dao.PurExpenseDao;
import cv.api.entity.MillingExpense;
import cv.api.entity.MillingExpenseKey;
import cv.api.entity.PurExpense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class MillingExpenseServiceImpl implements MillingExpenseService {

    @Autowired
    private MillingExpenseDao dao;

    @Override
    public MillingExpense save(MillingExpense p) {
        return dao.save(p);
    }

    @Override
    public List<MillingExpense> search(String vouNo, String compCode) {
        return dao.search(vouNo,compCode);
    }

    @Override
    public void delete(MillingExpenseKey key) {
        dao.delete(key);
    }
}
