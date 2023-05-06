/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.Currency;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WSwe
 */
@Repository
public class CurrencyDaoImpl extends AbstractDao<String, Currency> implements CurrencyDao {


    @Override
    public Currency save(Currency cur) {
        saveOrUpdate(cur,cur.getCurCode());
        return cur;
    }

    @Override
    public Currency findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<Currency> search(String code, String name) {
        String strSql = "select o from Currency o ";
        String strFilter = "";

        if (!code.equals("-")) {
            strFilter = "o.curCode like '" + code + "%'";
        }

        if (!name.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.currencyName like '%" + name + "%'";
            } else {
                strFilter = strFilter + " and o.currencyName like '%" + name + "%'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        return (List<Currency>) findHSQL(strSql);
    }

    @Override
    public int delete(String code, String compCode) {
    return 1;
    }

}
