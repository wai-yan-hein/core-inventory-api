/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.SaleHisDetailDao;
import cv.api.entity.MilingExpense;
import cv.api.entity.MilingExpenseKey;
import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class MilingExpenseServiceImpl implements MilingExpenseService {

    @Override
    public MilingExpense findById(MilingExpenseKey key) {
        return null;
    }

    @Override
    public MilingExpense save(MilingExpense exp) {
        return null;
    }

    @Override
    public List<MilingExpense> getExpense(String compCode) {
        return null;
    }

    @Override
    public void delete(MilingExpenseKey key) {

    }
}
