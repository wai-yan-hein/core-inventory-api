/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.MillingExpense;
import cv.api.entity.MillingExpenseKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class MillingExpenseServiceImpl implements MillingExpenseService {

    @Override
    public MillingExpense findById(MillingExpenseKey key) {
        return null;
    }

    @Override
    public MillingExpense save(MillingExpense exp) {
        return null;
    }

    @Override
    public List<MillingExpense> getExpense(String compCode) {
        return null;
    }

    @Override
    public void delete(MillingExpenseKey key) {

    }
}
