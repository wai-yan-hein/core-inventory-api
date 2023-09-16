/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.PurHis;
import cv.api.entity.PurHisKey;
import cv.api.model.VDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PurHisDaoImpl extends AbstractDao<PurHisKey, PurHis> implements PurHisDao {

    @Autowired
    private PurHisDetailDao dao;


    @Override
    public PurHis save(PurHis sh) {
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from PurHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public PurHis findById(PurHisKey id) {
        PurHis byKey = getByKey(id);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public void delete(PurHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update pur_his set deleted = true,intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        execSql(sql);
    }

    @Override
    public void restore(PurHisKey key) throws Exception {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update pur_his set deleted = false,intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        execSql(sql);
    }


    @Override
    public List<PurHis> unUploadVoucher(LocalDateTime syncDate) {
        String hsql = "select o from PurHis o where o.intgUpdStatus is null and o.vouDate >=: syncDate";
        return createQuery(hsql).setParameter("syncDate", syncDate).getResultList();
    }


    @Override
    public List<PurHis> unUpload(String syncDate) {
        String hsql = "select o from PurHis o where o.intgUpdStatus ='ACK' and date(o.vouDate) >= '" + syncDate + "'";
        List<PurHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer depId = o.getKey().getDeptId();
            o.setListPD(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from pur_his";
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
    public List<PurHis> search(String updatedDate, List<String> keys) {
        List<PurHis> list = new ArrayList<>();
        if (keys != null) {
            for (String locCode : keys) {
                String hql = "select o from PurHis o where o.locCode='" + locCode + "' and updatedDate > '" + updatedDate + "'";
                list.addAll(findHSQL(hql));
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setListPD(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public List<VDescription> getDescription(String str, String compCode, String tranType) {
        List<VDescription> list = new ArrayList<>();
        String table = tranType.equals("Sale") ? "sale_his" : "pur_his";
        String sql = "SELECT DISTINCT car_no " +
                "FROM " + table + " " +
                "WHERE comp_code = ? " +
                "AND (car_no LIKE ?) " +
                "LIMIT 20";
        try {
            ResultSet rs = getResult(sql, compCode, str + "%");
            while (rs.next()) {
                VDescription v = new VDescription();
                v.setDescription(rs.getString("car_no"));
                list.add(v);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }
}
