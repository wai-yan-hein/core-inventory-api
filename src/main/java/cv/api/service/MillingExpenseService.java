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

    MillingExpense save(MillingExpense p);

    List<MillingExpense> search(String vouNo, String compCode);

void delete(MillingExpenseKey key);
}