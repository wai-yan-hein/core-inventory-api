/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.MillingOutDetail;
import cv.api.entity.MillingOutDetailKey;
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
public class MillingOutDaoImpl extends AbstractDao<MillingOutDetailKey, MillingOutDetail> implements MillingOutDao {

    @Override
    public MillingOutDetail save(MillingOutDetail sdh) {
        saveOrUpdate(sdh, sdh.getKey());
        return sdh;
    }

    @Override
    public List<MillingOutDetail> search(String vouNo, String compCode, Integer deptId) {
        List<MillingOutDetail> listOP = new ArrayList<>();
        String sql = "select op.*,s.user_code,s.stock_name,l.loc_name\n" +
                "from milling_output op\n" +
                "join location l on op.loc_code = l.loc_code\n" +
                "and op.comp_code = l.comp_code\n" +
                "join stock s on op.stock_code = s.stock_code\n" +
                "and op.comp_code = s.comp_code\n" +
                "where op.vou_no ='" + vouNo + "'\n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "order by unique_id";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    MillingOutDetail op = new MillingOutDetail();
                    MillingOutDetailKey key = new MillingOutDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setWeight(rs.getDouble("weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setQty(rs.getDouble("qty"));
                    op.setPrice(rs.getDouble("price"));
                    op.setAmount(rs.getDouble("amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("unit"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setTotalWeight(rs.getDouble("tot_weight"));
                    op.setPercent(rs.getDouble("percent"));
                    op.setPercentQty(rs.getDouble("percent_qty"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }

    @Override
    public List<MillingOutDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<MillingOutDetail> list = new ArrayList<>();
        //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
        String sql = "select *\n" + "from miling_output\n" + "where vou_no='" + vouNo + "'\n" + "and comp_code ='" + compCode + "'\n" + "and dept_id ='" + deptId + "'\n" + "order by unique_id";
        try {
            ResultSet rs = getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    MillingOutDetail sh = new MillingOutDetail();
                    MillingOutDetailKey key = new MillingOutDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    sh.setKey(key);
                    sh.setDeptId(rs.getInt("dept_id"));
                    sh.setStockCode(rs.getString("stock_code"));
                    sh.setQty(rs.getDouble("qty"));
                    sh.setUnitCode(rs.getString("sale_unit"));
                    sh.setPrice(rs.getDouble("sale_price"));
                    sh.setAmount(rs.getDouble("sale_amt"));
                    sh.setLocCode(rs.getString("loc_code"));
                    list.add(sh);
                }
            }
        } catch (Exception e) {
            log.error("searchDetail : " + e.getMessage());
        }
        return list;
    }

    @Override
    public int delete(MillingOutDetailKey key) {
        remove(key);
        return 1;
    }



}
