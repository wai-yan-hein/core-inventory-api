/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.RetInHis;
import cv.api.entity.RetInHisKey;
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
public class RetInDaoImpl extends AbstractDao<RetInHisKey, RetInHis> implements RetInDao {

    @Autowired
    private RetInDetailDao dao;

    @Override
    public RetInHis save(RetInHis sh) {
        sh.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from RetInHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public RetInHis findById(RetInHisKey id) {
        RetInHis byKey = getByKey(id);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public void delete(RetInHisKey key) throws Exception {
        RetInHis s = findById(key);
        s.setDeleted(true);
        s.setUpdatedDate(LocalDateTime.now());
        updateEntity(s);
    }

    @Override
    public void restore(RetInHisKey key) throws Exception {
        RetInHis s = findById(key);
        s.setDeleted(false);
        s.setUpdatedDate(LocalDateTime.now());
        updateEntity(s);
    }

    @Override
    public List<RetInHis> unUploadVoucher(LocalDateTime syncDate) {
        String hsql = "select o from RetInHis o where o.intgUpdStatus is null and o.vouDate>= :syncDate";
        return createQuery(hsql).setParameter("syncDate", syncDate).getResultList();
    }

    @Override
    public List<RetInHis> unUpload(String syncDate) {
        String hsql = "select o from RetInHis o where (o.intgUpdStatus ='ACK' or o.intgUpdStatus is null) and date(o.vouDate) >= '" + syncDate + "'";
        List<RetInHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setListRD(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from ret_in_his";
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
    public List<RetInHis> search(String updatedDate, List<String> keys) {
        List<RetInHis> list = new ArrayList<>();
        if (keys != null) {
            for (String locCode : keys) {
                String hql = "select o from RetInHis o where o.locCode='" + locCode + "' and updatedDate > '" + updatedDate + "'";
                list.addAll(findHSQL(hql));
            }
        }
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setListRD(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void truncate(RetInHisKey key) {

    }
}
