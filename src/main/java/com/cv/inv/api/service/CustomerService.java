/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.Customer;
import java.util.List;

/**
 *
 * @author winswe
 */
public interface CustomerService {

    public Customer save(Customer cus, String traderCodeLength);

    public Customer save(Customer cus);

    public Customer findById(String id);
    
    public List<Customer> search(String code, String name, String address,
            String phone, String compCode);

    public int delete(Integer id);
}
