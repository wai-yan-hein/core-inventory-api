/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.cloud.CloudMQSender;
import cv.api.common.FilterObject;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.*;
import cv.api.model.VOrder;
import cv.api.model.VSale;
import cv.api.repo.AccountRepo;
import cv.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wai yan
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    private final ReturnObject ro = new ReturnObject();
    @Autowired
    private OrderHisService ohService;
    @Autowired
    private OrderDetailService odService;
    @Autowired
    private ReportService reportService;
//    @Autowired
//    private BackupService backupService;
//    @Autowired
//    private AccountRepo accountRepo;
    @Autowired(required = false)
    private CloudMQSender cloudMQSender;

    @PostMapping(path = "/save-order")
    public Mono<?> saveOrder(@RequestBody OrderHis order) {
        order.setUpdatedDate(Util1.getTodayDate());
        //if change location
//        if (cloudMQSender != null) cloudMQSender.checkLocationAndTruncate(sale);
        if (isValidOrder(order, ro)) {
//            backupService.backup(sale);
            order = ohService.save(order);
        } else {
            return Mono.justOrEmpty(ro);
        }
        //for account
//        accountRepo.sendSale(order);
        //for cloud
//        if (cloudMQSender != null) cloudMQSender.send(Order);
        return Mono.justOrEmpty(order);
    }

    private boolean isValidOrder(OrderHis order, ReturnObject ro) {
        boolean status = true;
        List<OrderHisDetail> listSH = order.getListSH();
        if (Util1.isNullOrEmpty(order.getTraderCode())) {
            status = false;
            ro.setMessage("Invalid Trader.");
        } else if (Util1.isNullOrEmpty(order.getVouDate())) {
            status = false;
            ro.setMessage("Invalid Voucher Date.");
        } else if (Util1.isNullOrEmpty(order.getCurCode())) {
            status = false;
            ro.setMessage("Invalid Currency.");
        } else if (Util1.getFloat(order.getVouTotal()) <= 0) {
            status = false;
            ro.setMessage("Invalid Voucher Total.");
        } else if (Util1.isNullOrEmpty(order.getLocCode())) {
            status = false;
            ro.setMessage("Invalid Location.");
        } else if (Util1.isNullOrEmpty(order.getCreatedBy())) {
            status = false;
            ro.setMessage("Invalid Created User.");
        } else if (Util1.isNullOrEmpty(order.getCreatedDate())) {
            status = false;
            ro.setMessage("Invalid Created Date.");
        }
        return status;
    }

    @PostMapping(path = "/get-order")
    public Flux<?> getOrder(@RequestBody FilterObject filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getCusCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String saleManCode = Util1.isNull(filter.getSaleManCode(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String nullBatch = String.valueOf(filter.isNullBatch());
        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
        List<VOrder> orderList = reportService.getOrderHistory(fromDate, toDate, cusCode, saleManCode, vouNo, remark,
                reference, userCode, stockCode, locCode, compCode, deptId, deleted, nullBatch, batchNo);
        return Flux.fromIterable(orderList);
    }

    @PostMapping(path = "/delete-order")
    public Mono<?> deleteSale(@RequestBody OrderHisKey key) throws Exception {
        ohService.delete(key);
        //delete in account
//        accountRepo.deleteInvVoucher(key);
        //delete in cloud
//        if (cloudMQSender != null) cloudMQSender.delete(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/restore-order")
    public Mono<?> restoreSale(@RequestBody OrderHisKey key) throws Exception {
        ohService.restore(key);
//        if (cloudMQSender != null) cloudMQSender.restore(key);
        return Mono.just(true);
    }

    @PostMapping(path = "/find-order")
    public Mono<OrderHis> findSale(@RequestBody OrderHisKey key) {
        OrderHis sh = ohService.findById(key);
        return Mono.justOrEmpty(sh);
    }

    @GetMapping(path = "/get-order-detail")
    public Flux<?> getSaleDetail(@RequestParam String vouNo,
                                 @RequestParam String compCode,
                                 @RequestParam Integer deptId) {
        return Flux.fromIterable(odService.search(vouNo, compCode, deptId));
    }

    @GetMapping(path = "/get-order-voucher-info")
    public Mono<?> getSaleVoucherCount(@RequestParam String vouDate,
                                                 @RequestParam String compCode,
                                                 @RequestParam Integer deptId) {
        return Mono.justOrEmpty(ohService.getVoucherInfo(vouDate, compCode, deptId));
    }

//    @GetMapping(path = "/get-sale-by-batch")
//    public ResponseEntity<?> getSaleByBatch(@RequestParam String batchNo,
//                                            @RequestParam String compCode,
//                                            @RequestParam Integer deptId,
//                                            @RequestParam boolean detail) {
//        if (detail) {
//            return ResponseEntity.ok(sdService.getSaleByBatchDetail(batchNo, compCode, deptId));
//        }
//        return ResponseEntity.ok(sdService.getSaleByBatch(batchNo, compCode, deptId));
//    }
}
