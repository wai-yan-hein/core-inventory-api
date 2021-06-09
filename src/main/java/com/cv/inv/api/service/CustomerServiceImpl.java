/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.CustomerDao;
import com.cv.inv.api.entity.Customer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author winswe
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Customer save(Customer cus, String traderCodeLength) {
        if (cus.getCode() == null || cus.getCode().isEmpty()) {
            Integer macId = cus.getMacId();
            String tmpTraderId = getTraderId(macId, "CUS", "-", cus.getCompCode(), traderCodeLength);
            cus.setCode(tmpTraderId);
        }
        return dao.save(cus);
    }

    @Override
    public Customer findById(String id) {
        Customer cus = dao.findById(id);
        return cus;
    }

    @Override
    public Customer save(Customer cus) {
        return dao.save(cus);
    }

    @Override
    public List<Customer> search(String code, String name, String address,
            String phone, String compCode) {
        List<Customer> listCus = dao.search(code, name, address, phone, compCode);
        return listCus;
    }

    @Override
    public int delete(Integer id) {
        int cnt = dao.delete(id);
        return cnt;
    }

    private String getTraderId(Integer macId, String option, String period, String compCode, String traderCodeLength) {
        int ttlLength = 5;
        if (traderCodeLength != null) {
            ttlLength = Integer.parseInt(traderCodeLength);
        }
        int seqNo = seqService.getSequence(macId, option, period, compCode);
        String tmpTraderId = option.toUpperCase() + String.format("%0" + ttlLength + "d", seqNo) + "-" + String.format("%0" + 3 + "d", macId);
        return tmpTraderId;
    }
}
