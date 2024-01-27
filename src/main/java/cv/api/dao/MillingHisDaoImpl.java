/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.entity.MillingHis;
import cv.api.entity.MillingHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class MillingHisDaoImpl extends AbstractDao<MillingHisKey, MillingHis> implements MillingHisDao {

    @Override
    public MillingHis save(MillingHis sh) {
        sh.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<MillingHis> getMillingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference, String userCode, String stockCode, String locCode,
                                              String compCode, Integer deptId, boolean deleted,
                                              String projectNo, String curCode) {
        String sql = """
                select a.*,t.trader_name, v.description
                from (
                select vou_date vou_date,vou_no,remark,created_by,reference,vou_status_id, trader_code,comp_code,dept_id
                from milling_his p
                where comp_code = ?
                and (dept_id = ? or 0 = ?)
                and deleted =?
                and date(vou_date) between ? and ?
                and cur_code = ?
                and (vou_no = ? or '-' = ?)
                and (remark LIKE CONCAT(?, '%') or '-'= ?)
                and (reference LIKE CONCAT(?, '%') or '-'= ?)
                and (trader_code = ? or '-'= ?)
                and (created_by = ? or '-'= ?)
                and (project_no =? or '-' =?)
                group by vou_no)a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                join vou_status v on a.vou_status_id = v.code
                and a.comp_code = v.comp_code
                order by vou_date desc""";
        ResultSet rs = getResult(sql, compCode, deptId, deptId, deleted, fromDate, toDate, curCode, vouNo, vouNo, remark, remark, reference,
                reference, traderCode, traderCode, userCode, userCode, projectNo, projectNo);
        List<MillingHis> purchaseList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            try {
                while (rs.next()) {
                    MillingHis s = new MillingHis();
                    MillingHisKey key = new MillingHisKey();
                    key.setVouNo(rs.getString("vou_no"));
                    s.setKey(key);
                    s.setVouDateStr(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                    s.setTraderName(rs.getString("trader_name"));
                    s.setProcessType(rs.getString("description"));
                    s.setRemark(rs.getString("remark"));
                    s.setReference(rs.getString("reference"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setDeptId(rs.getInt("dept_id"));
                    purchaseList.add(s);
                }
            } catch (Exception e) {
                log.error("getMillingHistory : " + e.getMessage());
            }
        }
        return purchaseList;
    }

    @Override
    public MillingHis findById(MillingHisKey id) {
        MillingHis byKey = getByKey(id);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public void delete(MillingHisKey key) throws Exception {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        String sql = "update milling_his set deleted = true where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        execSql(sql);
    }

    @Override
    public void restore(MillingHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        String sql = "update milling_his set deleted = false,intg_upd_status=null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        execSql(sql);
    }


    @Override
    public List<MillingHis> unUploadVoucher(LocalDateTime syncDate) {
        String hsql = "select o from milling o where o.intgUpdStatus is null and o.vouDate >= :syncDate";
        return createQuery(hsql).setParameter("syncDate", syncDate).getResultList();
    }

    @Override
    public List<MillingHis> unUpload(String syncDate) {
        String hql = "select o from milling o where (o.intgUpdStatus ='ACK' or o.intgUpdStatus is null) and date(o.vouDate) >= '" + syncDate + "'";
        List<MillingHis> list = findHSQL(hql);
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
//            o.setListSH(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }



    @Override
    public void truncate(MillingHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        String sql1 = "delete from milling_his where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        String sql2 = "delete from milling_raw where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        String sql3 = "delete from milling_output where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        String sql4 = "delete from milling_expense where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        execSql(sql1, sql2, sql3, sql4);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        General g = General.builder().build();
        String sql = "select count(*) vou_count,sum(paid) paid\n" +
                "from milling_his\n" +
                "where deleted = false\n" +
                "and date(vou_date)='" + vouDate + "'\n" +
                "and comp_code='" + compCode + "'";
        try {
            ResultSet rs = getResult(sql);
            if (rs.next()) {
                g.setQty(rs.getDouble("vou_count"));
                g.setAmount(rs.getDouble("paid"));
            }
        } catch (Exception e) {
            log.error("getVoucherCount : " + e.getMessage());
        }
        return g;
    }
}
