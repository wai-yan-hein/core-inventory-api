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
import cv.api.model.VOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        sh.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(sh, sh.getKey());
        return sh;
    }

    @Override
    public void update(OrderHis oh) {
        updateEntity(oh);
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
    public void truncate(OrderHisKey key) {

    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer deptId) {
        General g = General.builder().build();
        String sql = "select count(*) vou_count,sum(paid) paid\n" +
                "from order_his\n" +
                "where deleted = false\n" +
                "and date(vou_date)='" + vouDate + "'\n" +
                "and comp_code='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")";
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
    @Override
    public List<VOrder> getOrderHistory(String fromDate, String toDate, String traderCode, String saleManCode,
                                        String vouNo, String remark, String reference,
                                        String userCode, String stockCode, String locCode,
                                        String compCode, Integer deptId, String deleted,
                                        String nullBatch, String batchNo, String projectNo, String curCode, String orderStauts) {
        List<VOrder> saleList = new ArrayList<>();
        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no = '" + vouNo + "'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like '" + reference + "%'\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and created_by = '" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code = '" + stockCode + "'\n";
        }
        if (!saleManCode.equals("-")) {
            filter += "and saleman_code = '" + saleManCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and loc_code = '" + locCode + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = '" + projectNo + "'\n";
        }
        if (!curCode.equals("-")) {
            filter += "and cur_code = '" + curCode + "'\n";
        }
        if (!orderStauts.equals("-")) {
            filter += "and order_status = '" + orderStauts + "'\n";
        }
        String sql = "select a.*,t.trader_name,t.user_code,os.description order_status_name\n" +
                "from (\n" +
                "select  vou_no,vou_date,remark,reference,created_by,vou_total,deleted,trader_code,loc_code," +
                "comp_code,dept_id,order_status,post\n" +
                "from v_order s \n" +
                "where comp_code = '" + compCode + "'\n" +
                "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = " + deleted + "\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + filter + "\n" +
                "group by vou_no\n" + ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "left join order_status os on a.order_status = os.code\n" +
                "and a.comp_code = os.comp_code\n" +
                "order by vou_date desc";
        try {
            ResultSet rs = getResult(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VOrder s = new VOrder();
                    s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setTraderCode(rs.getString("user_code"));
                    s.setTraderName(rs.getString("trader_name"));
                    s.setRemark(rs.getString("remark"));
                    s.setReference(rs.getString("reference"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setVouTotal(rs.getDouble("vou_total"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setOrderStatus(rs.getString("order_status"));
                    s.setOrderStatusName(rs.getString("order_status_name"));
                    s.setPost(rs.getBoolean("post"));
                    saleList.add(s);
                }
            }
        } catch (Exception e) {
            log.error("getSaleHistory : " + e.getMessage());
        }
        return saleList;
    }
}
