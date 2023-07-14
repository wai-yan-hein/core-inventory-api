/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.entity.MilingHis;
import cv.api.entity.MilingHisKey;
import cv.api.entity.SaleHis;
import cv.api.entity.SaleHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class MilingHisDaoImpl extends AbstractDao<MilingHisKey, MilingHis> implements MilingHisDao {

    @Autowired
    private SaleHisDetailDao dao;

    @Override
    public MilingHis save(MilingHis sh) {
        saveOrUpdate(sh,sh.getKey());
        return sh;
    }

    @Override
    public List<MilingHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from MilingHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public MilingHis findById(MilingHisKey id) {
        return getByKey(id);
    }

    @Override
    public void delete(MilingHisKey key) throws Exception {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        String sql = "update miling_his set deleted = true where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        execSql(sql);
    }

    @Override
    public void restore(MilingHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        String sql = "update miling_his set deleted = false,intg_upd_status=null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        execSql(sql);
    }


    @Override
    public List<MilingHis> unUploadVoucher(LocalDateTime syncDate) {
        String hsql = "select o from MilingHis o where o.intgUpdStatus is null and o.vouDate >= :syncDate";
        return createQuery(hsql).setParameter("syncDate", syncDate).getResultList();    }

    @Override
    public List<MilingHis> unUpload(String syncDate) {
        String hql = "select o from MilingHis o where (o.intgUpdStatus ='ACK' or o.intgUpdStatus is null) and date(o.vouDate) >= '" + syncDate + "'";
        List<MilingHis> list = findHSQL(hql);
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
//            o.setListSH(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from miling_his";
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
        return Util1.getSyncDate();
    }


    @Override
    public void truncate(MilingHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql1 = "delete from miling_his where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        String sql2 = "delete from miling_raw where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        String sql3 = "delete from miling_output where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "' and " + deptId + "";
        String sql4 = "delete from miling_expense where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        execSql(sql1, sql2, sql3, sql4);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        General g = new General();
        String sql = "select count(*) vou_count,sum(paid) paid\n" +
                "from miling_his\n" +
                "where deleted = false\n" +
                "and date(vou_date)='" + vouDate + "'\n" +
                "and comp_code='" + compCode + "'";
        try {
            ResultSet rs = getResult(sql);
            if (rs.next()) {
                g.setQty(rs.getFloat("vou_count"));
                g.setAmount(rs.getFloat("paid"));
            }
        } catch (Exception e) {
            log.error("getVoucherCount : " + e.getMessage());
        }
        return g;
    }
}
