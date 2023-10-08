package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.LandingHisKey;
import cv.api.entity.LandingHis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class LandingHisDaoImpl extends AbstractDao<LandingHisKey, LandingHis> implements LandingHisDao {
    @Override
    public LandingHis findByCode(LandingHisKey key) {
        LandingHis grn = getByKey(key);
        if (grn != null) {
            grn.setVouDateTime(Util1.toZonedDateTime(grn.getVouDate()));
        }
        return grn;
    }

    @Override
    public LandingHis save(LandingHis b) {
        saveOrUpdate(b, b.getKey());
        return b;
    }

    @Override
    public List<LandingHis> findAll(String compCode, Integer deptId) {
        String hsql = "select o from LandingHis o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public boolean delete(LandingHisKey key) {
        LandingHis his = getByKey(key);
        if (his != null) {
            his.setDeleted(true);
            his.setUpdatedDate(LocalDateTime.now());
            update(his);
            return true;
        }
        return false;
    }

    @Override
    public boolean restore(LandingHisKey key) {
        LandingHis his = getByKey(key);
        if (his != null) {
            his.setDeleted(false);
            his.setUpdatedDate(LocalDateTime.now());
            update(his);
            return true;
        }
        return false;
    }

    @Override
    public List<LandingHis> getLandingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark,
                                              String userCode, String stockCode, String locCode, String compCode, Integer deptId, boolean deleted) {
        List<LandingHis> list = new ArrayList<>();
        remark += "%";
        String sql = """
                select a.vou_no,a.comp_code,a.dept_id,a.vou_date,a.created_by,a.deleted,a.remark,
                a.cargo,t.trader_name,l.loc_name,s.stock_name
                from (
                select *
                from landing_his
                where comp_code =?
                and deleted = ?
                and date(vou_date) between ? and ?
                and (trader_code = ? or '-' = ?)
                and (vou_no = ? or '-' = ?)
                and (remark = ? or '-%' = ?)
                and (created_by = ? or '-' = ?)
                and (stock_code = ? or '-' = ?)
                and (loc_code = ? or '-' = ?)
                and (dept_id = ? or 0 = ?)
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                order by vou_date desc""";
        try {
            ResultSet rs = getResult(sql, compCode, deleted, fromDate, toDate, traderCode, traderCode, vouNo, vouNo,
                    remark, remark, userCode, userCode, stockCode, stockCode, locCode, locCode, deleted, deleted);
            while (rs.next()) {
                //select a.vou_no,a.comp_code,a.dept_id,a.vou_date,a.created_by,a.deleted,a.remark,a.cargo,t.trader_name,l.loc_name,s.stock_name
                LandingHis l = new LandingHis();
                LandingHisKey key = new LandingHisKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                l.setKey(key);
                l.setDeptId(rs.getInt("dept_id"));
                l.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                l.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                l.setCreatedBy(rs.getString("created_by"));
                l.setDeleted(rs.getBoolean("deleted"));
                l.setRemark(rs.getString("remark"));
                l.setCargo(rs.getString("cargo"));
                l.setTraderName(rs.getString("trader_name"));
                l.setLocName(rs.getString("loc_name"));
                l.setStockName(rs.getString("stock_name"));
                list.add(l);
            }

        } catch (Exception e) {
            log.error("getLandingHistory : " + e.getMessage());
        }
        return list;
    }
}
