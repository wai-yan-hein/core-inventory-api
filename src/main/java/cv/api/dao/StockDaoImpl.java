/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Stock;
import cv.api.entity.StockKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockDaoImpl extends AbstractDao<StockKey, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        stock.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public Stock findById(StockKey key) {
        return getByKey(key);
    }

    @Override
    public List<Stock> findAll(String compCode, Integer deptId) {
        String hsql = "select o from Stock o where o.deleted = false and o.key.compCode = '" + compCode + "' and (o.deptId =" + deptId + " or 0=" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public int delete(StockKey key) {
        Stock s = findById(key);
        s.setDeleted(true);
        s.setUpdatedDate(LocalDateTime.now());
        update(s);
        return 1;
    }

    @Override
    public List<Stock> findActiveStock(String compCode, Integer deptId) {
        String hsql = "select o from Stock o where o.active = true and o.deleted = false and o.key.compCode = '" + compCode + "' and (o.deptId =" + deptId + " or 0=" + deptId + ")";
        return findHSQL(hsql);

    }

    @Override
    public List<Stock> search(String stockCode, String stockType, String cat,
                              String brand, String compCode,
                              Integer deptId, boolean orderFavorite) {
        List<Stock> list = new ArrayList<>();
        String sql = """
                select s.*,st.stock_type_name,cat.cat_name
                from stock s
                left join stock_type st
                on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join category cat
                on s.category_code = cat.cat_code
                and s.comp_code = cat.comp_code
                where s.comp_code =?
                and s.deleted = false
                and (stock_code =? or '-'=?)
                and (s.stock_type_code =? or '-'=?)
                and (s.category_code =? or '-'=?)
                and (s.brand_code =? or '-'=?)
                and (s.dept_id =? or 0=?)
                order by st.user_code,cat.user_code,s.user_code,s.stock_name
                """;
        ResultSet rs = getResult(sql, compCode, stockCode, stockCode, stockType, stockType, cat, cat, brand, brand, deptId, deptId);
        try {
            while (rs.next()) {
                //stock_code, comp_code, active, brand_code, stock_name, category_code,
                // stock_type_code, created_by, created_date, updated_by, updated_date,
                // barcode, short_name, pur_price, pur_unit, licence_exp_date,
                // sale_unit, remark, sale_price_n, sale_price_a, sale_price_b,
                // sale_price_c, sale_price_d, sale_price_e,
                 Stock s = new Stock();
                StockKey key = new StockKey();
                key.setStockCode(rs.getString("stock_code"));
                key.setCompCode(rs.getString("comp_code"));
                s.setKey(key);
                s.setActive(rs.getBoolean("active"));
                s.setBrandCode(rs.getString("brand_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setCatCode(rs.getString("category_code"));
                s.setTypeCode(rs.getString("stock_type_code"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                s.setUpdatedBy(rs.getString("updated_by"));
                s.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                s.setBarcode(rs.getString("barcode"));
                s.setShortName(rs.getString("short_name"));
                s.setPurPrice(rs.getDouble("pur_price"));
                s.setPurUnitCode(rs.getString("pur_unit"));
                s.setSaleUnitCode(rs.getString("sale_unit"));
                s.setRemark(rs.getString("remark"));
                s.setSalePriceN(rs.getDouble("sale_price_n"));
                s.setSalePriceA(rs.getDouble("sale_price_a"));
                s.setSalePriceB(rs.getDouble("sale_price_b"));
                s.setSalePriceC(rs.getDouble("sale_price_c"));
                s.setSalePriceD(rs.getDouble("sale_price_d"));
                s.setSalePriceE(rs.getDouble("sale_price_e"));
                s.setUserCode(rs.getString("user_code"));
                s.setMacId(rs.getInt("mac_id"));
                s.setRelCode(rs.getString("rel_code"));
                s.setCalculate(rs.getBoolean("calculate"));
                s.setDeptId(rs.getInt("dept_id"));
                s.setExplode(rs.getBoolean("explode"));
                s.setIntgUpdStatus(rs.getString("intg_upd_status"));
                s.setWeightUnit(rs.getString("weight_unit"));
                s.setWeight(rs.getDouble("weight"));
                s.setFavorite(rs.getBoolean("favorite"));
                s.setSaleClosed(rs.getBoolean("sale_closed"));
                s.setDeleted(rs.getBoolean("deleted"));
                s.setSaleQty(rs.getDouble("sale_qty"));
                s.setFormulaCode(rs.getString("formula_code"));
                s.setSaleAmt(rs.getDouble("sale_amt"));
                s.setPurAmt(rs.getDouble("pur_amt"));
                s.setPurQty(rs.getDouble("pur_qty"));
                s.setGroupName(rs.getString("stock_type_name"));
                s.setCatName(rs.getString("cat_name"));
                list.add(s);
            }
            // sale_wt, pur_wt, mig_code, user_code, mac_id, rel_code,
            // calculate, dept_id, explode, intg_upd_status, weight_unit, weight, favorite,
            // sale_closed, deleted, sale_qty, formula_code, sale_amt, pur_amt, pur_qty, stock_type_name, cat_name

        } catch (Exception e) {
            log.error("searchStock : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Stock> getStock(String str, String compCode, Integer deptId) {
        str = Util1.cleanStr(str);
        str = str + "%";
        return getStockList(str, compCode, deptId);
    }

    @Override
    public List<Stock> getService(String compCode, Integer deptId) {
        List<Stock> list = new ArrayList<>();
        String sql = "select * from stock where calculate =0 and deleted = false and  comp_code ='" + compCode + "' and (dept_id =" + deptId + " or 0=" + deptId + ")";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //stock_code, active, brand_code, stock_name, category_code, stock_type_code, created_by,
                    // created_date, updated_by, updated_date, barcode, short_name, pur_price, pur_unit, licence_exp_date,
                    // sale_unit, remark, sale_price_n, sale_price_a, sale_price_b, sale_price_c,
                    // sale_price_d, sale_price_e, sale_wt, pur_wt, mig_code, comp_code, user_code, mac_id,
                    // rel_code, calculate, dept_id, rel_name, stock_type_name, cat_name, brand_name
                    Stock s = new Stock();
                    StockKey key = new StockKey();
                    key.setStockCode(rs.getString("stock_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    s.setKey(key);
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setSaleUnitCode(rs.getString("sale_unit"));
                    s.setSalePriceN(rs.getDouble("sale_price_n"));
                    s.setSalePriceA(rs.getDouble("sale_price_a"));
                    s.setSalePriceB(rs.getDouble("sale_price_b"));
                    s.setSalePriceC(rs.getDouble("sale_price_c"));
                    s.setSalePriceD(rs.getDouble("sale_price_d"));
                    s.setSalePriceE(rs.getDouble("sale_price_e"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setUserCode(rs.getString("user_code"));
                    list.add(s);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }


    private List<Stock> getStockList(String str, String compCode, Integer deptId) {
        List<Stock> listStock = new ArrayList<>();
        String sql = """
                select s.*,rel.rel_name,st.stock_type_name,cat.cat_name,b.brand_name
                from stock s
                left join unit_relation rel on s.rel_code= rel.rel_code
                and s.comp_code = rel.comp_code
                left join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join category cat  on s.category_code = cat.cat_code
                and s.comp_code = cat.comp_code
                left join stock_brand b on s.brand_code  = b.brand_code
                and s.comp_code = b.comp_code
                where s.deleted = false
                and s.comp_code =?
                and s.active = true
                and (s.dept_id = ? or 0 =?)
                and (LOWER(REPLACE(s.user_code, ' ', '')) like ? or LOWER(REPLACE(s.stock_name, ' ', '')) like ?)
                order by s.user_code,s.stock_name
                limit 100""";
        ResultSet rs = getResult(sql, compCode, deptId, deptId, str, str);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //stock_code, active, brand_code, stock_name, category_code, stock_type_code, created_by,
                    // created_date, updated_by, updated_date, barcode, short_name, pur_price, pur_unit, licence_exp_date,
                    // sale_unit, remark, sale_price_n, sale_price_a, sale_price_b, sale_price_c,
                    // sale_price_d, sale_price_e, sale_wt, pur_wt, mig_code, comp_code, user_code, mac_id,
                    // rel_code, calculate, dept_id, rel_name, stock_type_name, cat_name, brand_name
                    Stock s = new Stock();
                    StockKey key = new StockKey();
                    key.setStockCode(rs.getString("stock_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    s.setKey(key);
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setBrandCode(rs.getString("brand_code"));
                    s.setCatCode(rs.getString("category_code"));
                    s.setTypeCode(rs.getString("stock_type_code"));
                    s.setPurPrice(rs.getDouble("pur_price"));
                    s.setPurUnitCode(rs.getString("pur_unit"));
                    s.setSaleUnitCode(rs.getString("sale_unit"));
                    s.setWeightUnit(rs.getString("weight_unit"));
                    s.setWeight(rs.getDouble("weight"));
                    s.setSalePriceN(rs.getDouble("sale_price_n"));
                    s.setSalePriceA(rs.getDouble("sale_price_a"));
                    s.setSalePriceB(rs.getDouble("sale_price_b"));
                    s.setSalePriceC(rs.getDouble("sale_price_c"));
                    s.setSalePriceD(rs.getDouble("sale_price_d"));
                    s.setSalePriceE(rs.getDouble("sale_price_e"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setUserCode(rs.getString("user_code"));
                    s.setFormulaCode(rs.getString("formula_code"));
                    s.setRelName(rs.getString("rel_name"));
                    s.setGroupName(rs.getString("stock_type_name"));
                    s.setCatName(rs.getString("cat_name"));
                    s.setBrandName(rs.getString("brand_name"));
                    s.setExplode(rs.getBoolean("explode"));
                    s.setPurQty(rs.getDouble("pur_qty"));
                    s.setPurAmt(rs.getDouble("pur_amt"));
                    s.setSaleAmt(rs.getDouble("sale_amt"));
                    listStock.add(s);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listStock;
    }

    @Override
    public List<Stock> unUpload() {
        String hsql = "select o from Stock o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock";
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
    public List<Stock> getStock(LocalDateTime updatedDate) {
        String hsql = "select o from Stock o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }


    @Override
    public Stock updateStock(Stock s) {
        Stock obj = getByKey(s.getKey());
        obj.setSaleQty(Util1.getDouble(s.getSaleQty()));
        obj.setSaleClosed(s.isSaleClosed());
        obj.setFavorite(s.isFavorite());
        obj.setUpdatedDate(LocalDateTime.now());
        update(obj);
        return obj;
    }
}
