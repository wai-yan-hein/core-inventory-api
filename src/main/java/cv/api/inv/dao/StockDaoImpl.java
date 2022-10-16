/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Stock;
import cv.api.inv.entity.StockKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockDaoImpl extends AbstractDao<StockKey, Stock> implements StockDao {

    @Override
    public Stock save(Stock stock) {
        persist(stock);
        return stock;
    }

    @Override
    public Stock findById(StockKey key) {
        return getByKey(key);
    }

    @Override
    public List<Stock> findAll(String compCode) {
        String hsql = "select o from Stock o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(String id) {
        String sql = "delete from stock where stock_code = '" + id + "'";
        execSQL(sql);
        return 1;
    }

    @Override
    public List<Stock> findActiveStock(String compCode) {
        String hsql = "select o from Stock o where o.active is true and o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);

    }

    @Override
    public List<Stock> search(String stockCode, String stockType, String cat, String brand) {
        String hsql = "select o from Stock o where o.active = 1";
        if (!stockCode.equals("-")) {
            hsql += " and o.key.stockCode ='" + stockCode + "'\n";
        }
        if (!stockType.equals("-")) {
            hsql += " and o.typeCode ='" + stockType + "'\n";
        }
        if (!cat.equals("-")) {
            hsql += " and o.catCode ='" + cat + "'\n";
        }
        if (!brand.equals("-")) {
            hsql += " and o.brandCode ='" + brand + "'\n";
        }
        return findHSQL(hsql);
    }

    @Override
    public List<Stock> getStock(String str, String compCode, Integer deptId) {
        List<Stock> listStock = new ArrayList<>();
        String sql = "select s.*,rel.rel_name,st.stock_type_name,cat.cat_name,b.brand_name\n" + "from stock s\n" + "join unit_relation rel on s.rel_code= rel.rel_code\n" + "left join stock_type st on s.stock_type_code = st.stock_type_code\n" + "left join category cat  on s.category_code = cat.cat_code\n" + "left join stock_brand b on s.brand_code  = b.brand_code\n" + "where s.comp_code ='" + compCode + "'\n" + "and s.active =1\n" + "and s.dept_id =" + deptId + "\n" + "and (s.user_code like '" + str + "%' or s.stock_name like '%" + str + "%')\n" + "limit 20";
        ResultSet rs = getResultSet(sql);
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
                    key.setDeptId(rs.getInt("dept_id"));
                    s.setKey(key);
                    s.setBrandCode(rs.getString("brand_code"));
                    s.setCatCode(rs.getString("category_code"));
                    s.setTypeCode(rs.getString("stock_type_code"));
                    s.setPurPrice(rs.getFloat("pur_price"));
                    s.setPurUnitCode(rs.getString("pur_unit"));
                    s.setSaleUnitCode(rs.getString("sale_unit"));
                    s.setSalePriceN(rs.getFloat("sale_price_n"));
                    s.setSalePriceA(rs.getFloat("sale_price_a"));
                    s.setSalePriceB(rs.getFloat("sale_price_b"));
                    s.setSalePriceC(rs.getFloat("sale_price_c"));
                    s.setSalePriceD(rs.getFloat("sale_price_d"));
                    s.setSalePriceE(rs.getFloat("sale_price_e"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setUserCode(rs.getString("user_code"));
                    s.setRelName(rs.getString("rel_name"));
                    s.setGroupName(rs.getString("stock_type_name"));
                    s.setCatName(rs.getString("cat_name"));
                    s.setBrandName(rs.getString("brand_name"));
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
}