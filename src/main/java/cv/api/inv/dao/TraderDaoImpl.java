/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Trader;
import cv.api.inv.entity.TraderKey;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WSwe
 */
@Repository
public class TraderDaoImpl extends AbstractDao<TraderKey, Trader> implements TraderDao {
    @Override
    public Trader findById(TraderKey key) {
        return getByKey(key);
    }

    @Override
    public List<Trader> searchTrader(String str, String type, String compCode) {
        String hsql = "select o from Trader o where o.key.compCode='" + compCode + "'\n" +
                "and o.active =1\n";
        if (!type.equals("-")) {
            hsql += "and (o.multi =1 or o.type ='" + type + "')\n";
        }
        String filter = "and o.userCode like '" + str + "%'\n";
        int limit = 5;
        List<Trader> list = findHSQL(hsql + filter, limit);
        if (list.isEmpty()) {
            filter = "and o.traderName like '" + str + "%'\n";
            list = findHSQL(hsql + filter, limit);
            if (list.isEmpty()) {
                filter = "and o.traderName like '%" + str + "%'\n";
                list = findHSQL(hsql + filter, limit);
            }
        }
        return list;
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
        String hsql = "select o from Trader o where  o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(TraderKey key) {
        String sql = "delete from trader  where code = '" + key.getCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSQL(sql);
        return 1;
    }

    @Override
    public List<Trader> findCustomer(String compCode,Integer deptId) {
        String hsql = "select o from Trader o where o.key.compCode = '" + compCode + "' and o.key.deptId ="+deptId+" and o.type = 'CUS' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> findSupplier(String compCode,Integer deptId) {
        String hsql = "select o from Trader o where o.key.compCode = '" + compCode + "' and o.key.deptId ="+deptId+"  and o.type = 'SUP' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> unUploadTrader() {
        String hsql = "select o from Trader o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }
}
