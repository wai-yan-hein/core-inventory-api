/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dao.SeqTableDao;
import cv.api.dao.StockInOutDao;
import cv.api.dao.StockInOutDetailDao;
import cv.api.entity.*;
import cv.api.model.VStockIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StockInOutServiceImpl implements StockInOutService {

    private final StockInOutDao ioDao;
    private final StockInOutDetailDao iodDao;
    private final SeqTableDao seqDao;
    private final DatabaseClient client;

    @Override
    public StockInOut save(StockInOut io) {
        io.setVouDate(Util1.toDateTime(io.getVouDate()));
        if (Util1.isNullOrEmpty(io.getKey().getVouNo())) {
            io.getKey().setVouNo(getVoucherNo(io.getDeptId(), io.getMacId(), io.getKey().getCompCode()));
        }

        List<StockInOutDetail> listSD = io.getListSH();
        List<StockInOutKey> listDel = io.getListDel();
        String vouNo = io.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(iodDao::delete);
        }
        for (int i = 0; i < listSD.size(); i++) {
            StockInOutDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                StockInOutKey key = new StockInOutKey();
                key.setCompCode(io.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setDeptId(io.getDeptId());
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        StockInOutDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                iodDao.save(cSd);
            }
        }
        ioDao.save(io);
        io.setListSH(listSD);
        return io;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp, String vouNo, String userCode, String vouStatus) {
        return ioDao.search(fromDate, toDate, remark, desp, vouNo, userCode, vouStatus);
    }

    @Override
    public StockInOut findById(StockIOKey id) {
        return ioDao.findById(id);
    }

    @Override
    public void delete(StockIOKey key) throws Exception {
        ioDao.delete(key);
    }

    @Override
    public void restore(StockIOKey key) throws Exception {
        ioDao.restore(key);
    }

    @Override
    public List<StockInOut> unUpload(String syncDate) {
        return ioDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return ioDao.getMaxDate();
    }

    @Override
    public List<StockInOut> search(String updatedDate, List<LocationKey> keys) {
        return ioDao.search(updatedDate, keys);
    }

    @Override
    public Flux<VStockIO> getStockIOHistory(FilterObject filterObject) {
        String fromDate = Util1.isNull(filterObject.getFromDate(), "-");
        String toDate = Util1.isNull(filterObject.getToDate(), "-");
        String vouStatus = Util1.isNull(filterObject.getVouStatus(), "-");
        String vouNo = Util1.isNull(filterObject.getVouNo(), "-");
        String remark = Util1.isNull(filterObject.getRemark(), "-");
        String desp = Util1.isNull(filterObject.getDescription(), "-");
        String userCode = Util1.isNull(filterObject.getUserCode(), "-");
        String stockCode = Util1.isNull(filterObject.getStockCode(), "-");
        String locCode = Util1.isNull(filterObject.getLocCode(), "-");
        String compCode = filterObject.getCompCode();
        Integer deptId = filterObject.getDeptId();
        String deleted = String.valueOf(filterObject.isDeleted());
        String traderCode = Util1.isNull(filterObject.getTraderCode(), "-");
        String jobNo = Util1.isNull(filterObject.getJobNo(), "-");
        String sql = """
                select a.*,v.description vou_status_name
                from (
                select vou_date,vou_no,description,remark,vou_status,created_by,
                deleted,comp_code,dept_id,sum(ifnull(in_qty,0)) in_qty,sum(ifnull(out_qty,0)) out_qty,
                sum(ifnull(in_bag,0)) in_bag,sum(ifnull(out_bag,0)) out_bag
                from v_stock_io
                where comp_code = :compCode
                and deleted = :deleted
                and (dept_id = :deptId or 0 = :deptId)
                and date(vou_date) between :fromDate and :toDate
                and (vou_no = :vouNo or '-' = :vouNo)
                and (remark REGEXP :remark or '-' = :remark)
                and (description REGEXP :desp or '-' = :desp)
                and (vou_status = :vouStatus or '-' = :vouStatus)
                and (created_by = :userCode or '-' = :userCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (loc_code = :locCode or '-' = :locCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                and (job_code = :jobNo or '-' = :jobNo)
                group by vou_no)a
                join vou_status v on a.vou_status = v.code
                and a.comp_code = v.comp_code
                order by vou_date desc""";

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("desp", desp)
                .bind("vouStatus", vouStatus)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .bind("traderCode", traderCode)
                .bind("jobNo", jobNo)
                .map((row) -> VStockIO.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDateTime.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
                        .description(row.get("description", String.class))
                        .remark(row.get("remark", String.class))
                        .vouTypeName(row.get("vou_status_name", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .inQty(row.get("in_qty", Double.class))
                        .outQty(row.get("out_qty", Double.class))
                        .inBag(row.get("in_bag", Double.class))
                        .outBag(row.get("out_bag", Double.class))
                        .build()
                ).all();
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "STOCKIO", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }

}
