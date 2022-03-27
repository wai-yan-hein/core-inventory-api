/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Trader;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WSwe
 */
@Repository
public class TraderDaoImpl extends AbstractDao<String, Trader> implements TraderDao {

    @Override
    public Trader findByCode(String id) {
        return getByKey(id);
    }

    @Override
    public List<Trader> searchTrader(String code, String name, String address,
                                     String phone, String parentCode, String compCode, String appTraderCode) {
        String strSql = "select o from Trader o ";
        String strFilter = "";

        if (!compCode.equals("-")) {
            strFilter = "o.compCode = '" + compCode + "'";
        }

        if (!code.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.code like '" + code + "%'";
            } else {
                strFilter = strFilter + " and o.code like '" + code + "%'";
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

        if (!parentCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.coaParent like '%" + parentCode + "%'";
            } else {
                strFilter = strFilter + " and o.coaParent like '%" + parentCode + "%'";
            }
        }
        if (!appTraderCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.appTraderCode ='" + appTraderCode + "'";
            } else {
                strFilter = strFilter + " and o.appTraderCode  '" + appTraderCode + "'";
            }
        }

        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
        }

        strSql = strSql + " order by o.traderName";
        return findHSQL(strSql);
    }

    @Override
    public Trader saveTrader(Trader trader) {
        persist(trader);
        return trader;
    }


    @Override
    public List<Trader> search(String regionCode, String coaCode) {
        String hsql = null;
        if (!regionCode.equals("-")) {
            hsql = "select distinct o.region.regCode from Trader o where o.region.regCode = '" + regionCode + "'";
        } else if (!coaCode.equals("-")) {
            hsql = "select distinct o.account.code from Trader o where o.account.code = '" + coaCode + "'";
        }
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> findAll(String compCode) {
        String hsql = "select o from Trader o where  o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String code) {
        String hsql = "delete from Trader o where o.code = '" + code + "'";
        return execUpdateOrDelete(hsql);
    }

    @Override
    public List<Trader> findCustomer(String compCode) {
        String hsql = "select o from Trader o where o.compCode = '" + compCode + "' and o.type = 'CUS' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> findSupplier(String compCode) {
        String hsql = "select o from Trader o where o.compCode = '" + compCode + "' and o.type = 'SUP' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }
}
