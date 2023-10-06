/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Stock;
import cv.api.entity.StockCriteria;
import cv.api.entity.StockCriteriaKey;
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
public class StockCriteriaDaoImpl extends AbstractDao<StockCriteriaKey, StockCriteria> implements StockCriteriaDao {

    @Override
    public StockCriteria save(StockCriteria stock) {
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public StockCriteria findById(StockCriteriaKey key) {
        return getByKey(key);
    }

    @Override
    public List<StockCriteria> findAll(String compCode) {
        String hsql = "select o from StockCriteria o where o.deleted = false and o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public int delete(StockCriteriaKey key) {
        StockCriteria s = findById(key);
        s.setDeleted(true);
        s.setUpdatedDate(LocalDateTime.now());
        update(s);
        return 1;
    }

    @Override
    public List<StockCriteria> findActiveStock(String compCode) {
        String hsql = "select o from StockCriteria o where o.active = true and o.deleted = false and o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);

    }

    @Override
    public List<StockCriteria> search(String stockCode, String stockType, String cat, String brand, String compCode, boolean orderFavorite) {
        String hsql = "select o from Stock o where o.active = true and o.deleted = false and o.key.compCode ='" + compCode + "'\n";
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
        if (orderFavorite) {
            hsql += " order by o.favorite desc,o.userCode";
        } else {
            hsql += " order by o.userCode";
        }
        return findHSQL(hsql);
    }

    @Override
    public List<StockCriteria> getStock(String str, String compCode) {
        str = Util1.cleanStr(str);
        str = str + "%";
        return getStockList(str, compCode);
    }

    @Override
    public List<StockCriteria> getService(String compCode) {
        List<StockCriteria> list = new ArrayList<>();
        String sql = "select * from StockCriteria where calculate =0 and deleted = false and  comp_code ='" + compCode + "'";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //stock_code, active, brand_code, stock_name, category_code, stock_type_code, created_by,
                    // created_date, updated_by, updated_date, barcode, short_name, pur_price, pur_unit, licence_exp_date,
                    // sale_unit, remark, sale_price_n, sale_price_a, sale_price_b, sale_price_c,
                    // sale_price_d, sale_price_e, sale_wt, pur_wt, mig_code, comp_code, user_code, mac_id,
                    // rel_code, calculate, dept_id, rel_name, stock_type_name, cat_name, brand_name
                    StockCriteria s = new StockCriteria();
                    StockCriteriaKey key = new StockCriteriaKey();
                    key.setCriteriaCode(rs.getString("criteria_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    s.setKey(key);
                    s.setCriteriaName(rs.getString("criteria_name"));
                    s.setUserCode(rs.getString("user_code"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setUpdatedBy(rs.getString("updated_by"));
                    s.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    s.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                    s.setActive(rs.getBoolean("active"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    list.add(s);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }


    private List<StockCriteria> getStockList(String str, String compCode) {
        List<StockCriteria> listStock = new ArrayList<>();
        String sql = """
                select s.*,rel.rel_name,st.stock_type_name,cat.cat_name,b.brand_name
                from stock_criteria s
                join unit_relation rel on s.rel_code= rel.rel_code
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
                and (LOWER(REPLACE(s.user_code, ' ', '')) like ? or LOWER(REPLACE(s.stock_name, ' ', '')) like ?)
                order by s.user_code,s.stock_name
                limit 100""";
        ResultSet rs = getResult(sql, compCode, str, str);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //stock_code, active, brand_code, stock_name, category_code, stock_type_code, created_by,
                    // created_date, updated_by, updated_date, barcode, short_name, pur_price, pur_unit, licence_exp_date,
                    // sale_unit, remark, sale_price_n, sale_price_a, sale_price_b, sale_price_c,
                    // sale_price_d, sale_price_e, sale_wt, pur_wt, mig_code, comp_code, user_code, mac_id,
                    // rel_code, calculate, dept_id, rel_name, stock_type_name, cat_name, brand_name
                    StockCriteria s = new StockCriteria();
                    StockCriteriaKey key = new StockCriteriaKey();
                    key.setCriteriaCode(rs.getString("criteria_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    s.setKey(key);
                    s.setCriteriaName(rs.getString("criteria_name"));
                    s.setUserCode(rs.getString("user_code"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setUpdatedBy(rs.getString("updated_by"));
                    s.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                    s.setUpdatedDate(rs.getTimestamp("updated_date").toLocalDateTime());
                    s.setActive(rs.getBoolean("active"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setRelName(rs.getString("rel_name"));
                    s.setGroupName(rs.getString("stock_type_name"));
                    s.setCatName(rs.getString("cat_name"));
                    s.setBrandName(rs.getString("brand_name"));
//                    s.setExplode(rs.getBoolean("explode"));
                    listStock.add(s);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listStock;
    }

    @Override
    public List<StockCriteria> unUpload() {
        String hsql = "select o from StockCriteria o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from stock_criteria";
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
    public List<StockCriteria> getStock(LocalDateTime updatedDate) {
        String hsql = "select o from StockCriteria o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }


    @Override
    public StockCriteria updateStock(StockCriteria s) {
        StockCriteria obj = getByKey(s.getKey());
//        obj.setSaleQty(Util1.getFloat(s.getSaleQty()));
//        obj.setSaleClosed(s.isSaleClosed());
//        obj.setFavorite(s.isFavorite());
        obj.setUpdatedDate(LocalDateTime.now());
        update(obj);
        return obj;
    }
}
