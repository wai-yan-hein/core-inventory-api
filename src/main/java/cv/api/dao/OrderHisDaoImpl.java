/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.entity.OrderHis;
import cv.api.entity.OrderHisKey;
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
public class OrderHisDaoImpl extends AbstractDao<OrderHisKey, OrderHis> implements OrderHisDao {

    @Autowired
    private OrderHisDetailDao dao;

    @Override
    public OrderHis save(OrderHis sh) {
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public List<OrderHis> search(String fromDate, String toDate, String cusCode,
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
        String strSql = "select o from OrderHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate,o.vouNo";
        }

        return findHSQL(strSql);
    }

    @Override
    public OrderHis findById(OrderHisKey id) {
        OrderHis byKey = getByKey(id);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public void delete(OrderHisKey key) throws Exception {
        OrderHis obj = findById(key);
        obj.setDeleted(true);
        obj.setUpdatedDate(LocalDateTime.now());
        update(obj);
    }

    @Override
    public void restore(OrderHisKey key) {
        OrderHis obj = findById(key);
        obj.setDeleted(false);
        obj.setUpdatedDate(LocalDateTime.now());
        update(obj);
    }


    @Override
    public List<OrderHis> unUploadVoucher(String syncDate) {
        String hsql = "select o from OrderHis o where o.intgUpdStatus is null and date(o.vouDate) >= '" + syncDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<OrderHis> unUpload(String syncDate) {
        String hql = "select o from OrderHis o where (o.intgUpdStatus ='ACK' or o.intgUpdStatus is null) and date(o.vouDate) >= '" + syncDate + "'";
        List<OrderHis> list = findHSQL(hql);
        list.forEach(o -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getDeptId();
            o.setListSH(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from order_his";
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
    public void truncate(OrderHisKey key) {

    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer deptId) {
        General g = new General();
        String sql = "select count(*) vou_count,sum(paid) paid\n" +
                "from order_his\n" +
                "where deleted = false\n" +
                "and date(vou_date)='" + vouDate + "'\n" +
                "and comp_code='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")";
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
