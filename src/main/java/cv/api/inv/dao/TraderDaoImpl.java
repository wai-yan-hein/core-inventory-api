/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.Trader;
import cv.api.inv.entity.TraderKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author WSwe
 */
@Slf4j
@Repository
public class TraderDaoImpl extends AbstractDao<TraderKey, Trader> implements TraderDao {
    @Override
    public Trader findById(TraderKey key) {
        return getByKey(key);
    }

    @Override
    public List<Trader> searchTrader(String str, String type, String compCode, Integer deptId) {
        String filter = "where active =1\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and (user_code like '" + str + "%' or trader_name like '" + str + "%') \n";
        if (!type.equals("-")) {
            filter += "and (multi =1 or type ='" + type + "')";
        }
        String sql = "select code,user_code,trader_name,price_type,type\n" +
                "from trader\n" +
                "" + filter + "\n" +
                "limit 20\n";
        ResultSet rs = getResultSet(sql);
        List<Trader> list = new ArrayList<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Trader t = new Trader();
                    TraderKey key = new TraderKey();
                    key.setCompCode(compCode);
                    key.setCode(rs.getString("code"));
                    key.setDeptId(deptId);
                    t.setKey(key);
                    t.setUserCode(rs.getString("user_code"));
                    t.setTraderName(rs.getString("trader_name"));
                    t.setPriceType(rs.getString("price_type"));
                    t.setType(rs.getString("type"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
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
    public List<Trader> findCustomer(String compCode, Integer deptId) {
        String hsql = "select o from Trader o where o.key.compCode = '" + compCode + "' and (o.key.deptId =" + deptId + " or 0 ="+deptId+") and o.type = 'CUS' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> findSupplier(String compCode, Integer deptId) {
        String hsql = "select o from Trader o where o.key.compCode = '" + compCode + "' and (o.key.deptId =" + deptId + " or 0 ="+deptId+")  and o.type = 'SUP' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> unUploadTrader() {
        String hsql = "select o from Trader o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from trader";
        ResultSet rs = getResultSet(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<Trader> getTrader(String updatedDate) {
        String hsql = "select o from Trader o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }
}
