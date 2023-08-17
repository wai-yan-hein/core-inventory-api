/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Trader;
import cv.api.entity.TraderKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
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
    public Trader findByRFID(String rfId, String compCode, Integer deptId) {
        String sql = "select code,user_code,trader_name,price_type,type\n" +
                "from trader\n" + "where comp_code='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" + "and rfid='" + rfId + "'\n" + "limit 1\n";
        try {
            ResultSet rs = getResult(sql);
            if (rs.next()) {
                Trader t = new Trader();
                TraderKey key = new TraderKey();
                key.setCompCode(compCode);
                key.setCode(rs.getString("code"));
                t.setKey(key);
                t.setDeptId(deptId);
                t.setUserCode(rs.getString("user_code"));
                t.setTraderName(rs.getString("trader_name"));
                t.setPriceType(rs.getString("price_type"));
                t.setType(rs.getString("type"));
                return t;
            }
        } catch (Exception e) {
            log.error("findByRFID : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Trader> searchTrader(String str, String type, String compCode, Integer deptId) {
        str = Util1.cleanStr(str);
        str = str + "%";
        String filter = """
                where active = true
                and deleted = false
                and comp_code =?
                and (dept_id =? or 0 =?)
                and (LOWER(REPLACE(user_code, ' ', '')) like ? or LOWER(REPLACE(trader_name, ' ', '')) like ?)
                """;
        if (!type.equals("-")) {
            filter += "and (multi =1 or type ='" + type + "')";
        }
        String sql = "select code,user_code,trader_name,price_type,type,address,credit_amt,credit_days\n" +
                "from trader\n" + filter + "\n" +
                "order by user_code,trader_name\n" +
                "limit 100\n";
        ResultSet rs = getResult(sql, compCode, deptId, deptId, str, str);
        List<Trader> list = new ArrayList<>();
        try {
            if (rs != null) {
                while (rs.next()) {
                    Trader t = new Trader();
                    TraderKey key = new TraderKey();
                    key.setCompCode(compCode);
                    key.setCode(rs.getString("code"));
                    t.setKey(key);
                    t.setDeptId(deptId);
                    t.setUserCode(rs.getString("user_code"));
                    t.setTraderName(rs.getString("trader_name"));
                    t.setPriceType(rs.getString("price_type"));
                    t.setType(rs.getString("type"));
                    t.setAddress(rs.getString("address"));
                    t.setCreditAmt(rs.getFloat("credit_amt"));
                    t.setCreditDays(rs.getInt("credit_days"));
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
        saveOrUpdate(trader, trader.getKey());
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
    public List<Trader> findAll() {
        String hsql = "select o from Trader o";
        return findHSQL(hsql);
    }

    @Override
    public int delete(TraderKey key) {
        Trader t = findById(key);
        t.setDeleted(true);
        t.setUpdatedDate(LocalDateTime.now());
        update(t);
        return 1;
    }

    @Override
    public List<Trader> findCustomer(String compCode, Integer deptId) {
        String hsql = "select o from Trader o where o.key.compCode = '" + compCode + "' and (o.deptId =" + deptId + " or 0 =" + deptId + ") and o.deleted =false and o.type = 'CUS' or o.multi = true order by o.userCode";
        return findHSQL(hsql);
    }

    @Override
    public List<Trader> findSupplier(String compCode, Integer deptId) {
        String hsql = "select o from Trader o where o.key.compCode = '" + compCode + "' and (o.deptId =" + deptId + " or 0 =" + deptId + ") and o.deleted =false and o.type = 'SUP' or o.multi = true order by o.userCode";
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
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<Trader> getTrader(LocalDateTime updatedDate) {
        String hsql = "select o from Trader o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
