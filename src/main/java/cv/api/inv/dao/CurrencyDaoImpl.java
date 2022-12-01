/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.Currency;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 *
 * @author WSwe
 */
@Repository
public class CurrencyDaoImpl extends AbstractDao<String, Currency> implements CurrencyDao {


    @Override
    public Currency save(Currency cur) {
        persist(cur);
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
        String strSql = "delete from Currency o where o.key.code = '" + code
                + "' and o.key.compCode = " + compCode;
        return execUpdateOrDelete(strSql);
    }

}
