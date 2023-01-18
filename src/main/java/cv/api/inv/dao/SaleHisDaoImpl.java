/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.common.Util1;
import cv.api.inv.entity.SaleHis;
import cv.api.inv.entity.SaleHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class SaleHisDaoImpl extends AbstractDao<SaleHisKey, SaleHis> implements SaleHisDao {

    @Autowired
    private SaleHisDetailDao dao;

    @Override
    public SaleHis save(SaleHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusCode,
                                String vouNo, String remark, String userCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.equals("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!cusCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.trader.code = '" + cusCode + "'";
            } else {
                strFilter = strFilter + " and o.trader.code = '" + cusCode + "'";
            }
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark like '" + remark + "%'";
            } else {
                strFilter = strFilter + " and o.remark like '" + remark + "%'";
            }
        }
        String strSql = "select o from SaleHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public SaleHis findById(SaleHisKey id) {
        return getByKey(id);
    }

    @Override
    public void delete(SaleHisKey key) throws Exception {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update sale_his set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);
    }

    @Override
    public void restore(SaleHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update sale_his set deleted =0,intg_upd_status=null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);
    }


    @Override
    public List<SaleHis> unUploadVoucher(String syncDate) {
        String hsql = "select o from SaleHis o where o.intgUpdStatus is null and date(o.vouDate) >= '" + syncDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<SaleHis> unUpload(String syncDate) {
        String hql = "select o from SaleHis o where (o.intgUpdStatus ='ACK' or o.intgUpdStatus is null) and date(o.vouDate) >= '" + syncDate + "'";
        List<SaleHis> list = findHSQL(hql);
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListSH(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from sale_his";
        ResultSet rs = getResultSet(sql);
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
        return Util1.getSyncDate();
    }

    @Override
    public List<SaleHis> search(String updatedDate, List<String> location) {
        List<SaleHis> list = new ArrayList<>();
        if (location != null) {
            for (String locCode : location) {
                String sql = "select * from sale_his o where o.loc_code='" + locCode + "' and o.updated_date > '" + updatedDate + "'";
                ResultSet rs = getResultSet(sql);
                if (rs != null) {
                    try {
                        while (rs.next()) {
                            //vou_no, trader_code, saleman_code, vou_date, credit_term, cur_code, remark,
                            // vou_total, grand_total, discount, disc_p, tax_amt, tax_p, created_date,
                            // created_by, deleted, paid, vou_balance, updated_by,
                            // updated_date, comp_code, address, order_code, reg_code, loc_code, mac_id,
                            // session_id, intg_upd_status, reference, dept_id
                            SaleHis sh = new SaleHis();
                            SaleHisKey k = new SaleHisKey();
                            k.setVouNo(rs.getString("vou_no"));
                            k.setCompCode(rs.getString("comp_code"));
                            k.setDeptId(rs.getInt("dept_id"));
                            sh.setKey(k);
                            sh.setTraderCode(rs.getString("trader_code"));
                            sh.setSaleManCode(rs.getString("saleman_code"));
                            sh.setVouDate(rs.getDate("vou_date"));
                            sh.setCreditTerm(rs.getDate("credit_term"));
                            sh.setCurCode(rs.getString("cur_code"));
                            sh.setRemark(rs.getString("remark"));
                            sh.setVouTotal(rs.getFloat("vou_total"));
                            sh.setGrandTotal(rs.getFloat("grand_total"));
                            sh.setDiscount(rs.getFloat("discount"));
                            sh.setDiscP(rs.getFloat("disc_p"));
                            sh.setTaxAmt(rs.getFloat("tax_amt"));
                            sh.setTaxPercent(rs.getFloat("tax_p"));
                            sh.setCreatedDate(rs.getTimestamp("created_date"));
                            sh.setCreatedBy(rs.getString("created_by"));
                            sh.setDeleted(rs.getBoolean("deleted"));
                            sh.setPaid(rs.getFloat("paid"));
                            sh.setBalance(rs.getFloat("vou_balance"));
                            sh.setUpdatedBy(rs.getString("updated_by"));
                            sh.setUpdatedDate(rs.getTimestamp("updated_date"));
                            sh.setAddress(rs.getString("address"));
                            sh.setOrderCode(rs.getString("order_code"));
                            sh.setLocCode(rs.getString("loc_code"));
                            sh.setMacId(rs.getInt("mac_id"));
                            sh.setIntgUpdStatus(rs.getString("intg_upd_status"));
                            sh.setReference(rs.getString("reference"));
                            list.add(sh);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }

            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListSH(dao.searchDetail(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void truncate(SaleHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql1 = "delete from sale_his where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        String sql2 = "delete from sale_his_detail where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        execSQL(sql1, sql2);
    }
}
