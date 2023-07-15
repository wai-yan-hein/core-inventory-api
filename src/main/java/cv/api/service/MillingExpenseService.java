/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.*;

import java.util.List;

/**
 * @author wai yan
 */
public interface MillingExpenseService {

    MillingExpense findById(MillingExpenseKey key);
    MillingExpense save(MillingExpense exp);

    List<MillingExpense> getExpense(String compCode);

    void delete(MillingExpenseKey key);


}
