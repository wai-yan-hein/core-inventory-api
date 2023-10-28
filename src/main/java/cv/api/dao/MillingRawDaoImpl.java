/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.MillingRawDetail;
import cv.api.entity.MillingRawDetailKey;
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
public class MillingRawDaoImpl extends AbstractDao<MillingRawDetailKey, MillingRawDetail> implements MillingRawDao {

    @Override
    public MillingRawDetail save(MillingRawDetail sdh) {
        saveOrUpdate(sdh, sdh.getKey());
        return sdh;
    }

    @Override
    public List<MillingRawDetail> search(String vouNo, String compCode, Integer deptId) {
        List<MillingRawDetail> listOP = new ArrayList<>();
        String sql = """
                select op.*,s.user_code,s.stock_name,l.loc_name,u1.unit_name,u2.unit_name weight_unit_name
                from milling_raw op
                join location l on op.loc_code = l.loc_code
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                left join stock_unit u1 on op.unit = u1.unit_code
                and op.comp_code = u1.comp_code
                left join stock_unit u2 on op.weight_unit= u2.unit_code
                and op.comp_code = u2.comp_code
                where op.vou_no =?
                and op.comp_code =?
                order by unique_id""";
        ResultSet rs = getResult(sql, vouNo, compCode);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    MillingRawDetail op = new MillingRawDetail();
                    MillingRawDetailKey key = new MillingRawDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setWeight(rs.getFloat("weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setQty(rs.getFloat("qty"));
                    op.setPrice(rs.getFloat("price"));
                    op.setAmount(rs.getFloat("amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("unit"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setTotalWeight(rs.getFloat("tot_weight"));
                    op.setUnitName(rs.getString("unit_name"));
                    op.setWeightUnitName(rs.getString("weight_unit_name"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }

    @Override
    public List<MillingRawDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<MillingRawDetail> list = new ArrayList<>();
        //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
        String sql = "select *\n" + "from miling_raw\n" + "where vou_no='" + vouNo + "'\n" + "and comp_code ='" + compCode + "'\n" + "and dept_id ='" + deptId + "'\n" + "order by unique_id";
        try {
            ResultSet rs = getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    MillingRawDetail sh = new MillingRawDetail();
                    MillingRawDetailKey key = new MillingRawDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    sh.setKey(key);
                    sh.setDeptId(rs.getInt("dept_id"));
                    sh.setStockCode(rs.getString("stock_code"));
                    sh.setQty(rs.getFloat("qty"));
                    sh.setUnitCode(rs.getString("sale_unit"));
                    sh.setPrice(rs.getFloat("sale_price"));
                    sh.setAmount(rs.getFloat("sale_amt"));
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
    public int delete(MillingRawDetailKey key) {
        remove(key);
        return 1;
    }

}
