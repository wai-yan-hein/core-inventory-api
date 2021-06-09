/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.Customer;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author winswe
 */
@Repository
public class CustomerDaoImpl extends AbstractDao<String, Customer> implements CustomerDao {

    @Override
    public Customer save(Customer cus) {
        persist(cus);
        return cus;
    }

    @Override
    public Customer findById(String id) {
        Customer cus = getByKey(id);
        return cus;
    }

    @Override
    public List<Customer> search(String code, String name, String address,
            String phone, String compCode) {
        String strSql = "select o from Customer o ";
        String strFilter = "";

        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode = '" + compCode + "'";
            }
        }

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.code = '" + code + "'";
            } else {
                strFilter = strFilter + " and o.code = '" + code + "'";
            }
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.traderName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.traderName like '%" + name + "%'";
            }
        }

        if (!address.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.address like '%" + address + "%'";
            } else {
                strFilter = strFilter + " and o.address like '%" + address + "%'";
            }
        }

        if (!phone.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.phone like '%" + phone + "%'";
            } else {
                strFilter = strFilter + " and o.phone like '%" + phone + "%'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        List<Customer> listCus = findHSQL(strSql);
        return listCus;
    }

    @Override
    public int delete(Integer id) {
        String strSql = "delete from Customer o where o.id = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

   
}
