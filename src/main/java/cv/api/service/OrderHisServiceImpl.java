/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.dao.OrderHisDao;
import cv.api.dao.OrderHisDetailDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.OrderDetailKey;
import cv.api.entity.OrderHis;
import cv.api.entity.OrderHisDetail;
import cv.api.entity.OrderHisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderHisServiceImpl implements OrderHisService {

    @Autowired
    private OrderHisDao shDao;
    @Autowired
    private OrderHisDetailDao sdDao;
    @Autowired
    private SeqTableDao seqDao;
    private final DatabaseClient client;

    @Override
    public OrderHis save(OrderHis orderHis) {
        orderHis.setVouDate(Util1.toDateTime(orderHis.getVouDate()));
        if (Util1.isNullOrEmpty(orderHis.getKey().getVouNo())) {
            orderHis.getKey().setVouNo(getVoucherNo(orderHis.getDeptId(), orderHis.getMacId(), orderHis.getKey().getCompCode()));
        }
        List<OrderHisDetail> listSD = orderHis.getListSH();
        List<OrderDetailKey> listDel = orderHis.getListDel();
        String vouNo = orderHis.getKey().getVouNo();
        //backup
        if (listDel != null) {
            listDel.forEach(key -> sdDao.delete(key));
        }
        for (int i = 0; i < listSD.size(); i++) {
            OrderHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                OrderDetailKey key = new OrderDetailKey();
                key.setCompCode(orderHis.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setDeptId(orderHis.getDeptId());
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null || cSd.getDesign() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        OrderHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                sdDao.save(cSd);
            }
        }
        shDao.save(orderHis);
        orderHis.setListSH(listSD);
        return orderHis;
    }

    @Override
    public OrderHis update(OrderHis orderHis) {
        return shDao.save(orderHis);
    }


    @Override
    public OrderHis findById(OrderHisKey id) {
        return shDao.findById(id);
    }

    @Override
    public void delete(OrderHisKey key) throws Exception {
        shDao.delete(key);
    }

    @Override
    public void restore(OrderHisKey key) throws Exception {
        shDao.restore(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }


    @Override
    public List<OrderHis> unUploadVoucher(String syncDate) {
        return shDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<OrderHis> unUpload(String syncDate) {
        return shDao.unUpload(syncDate);
    }


    @Override
    public void truncate(OrderHisKey key) {
        shDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return shDao.getVoucherInfo(vouDate, compCode, depId);
    }

    @Override
    public Mono<Boolean> updateOrder(OrderHisKey key, boolean post) {
        String sql = """
                update order_his
                set post = :post
                where vou_no = :vouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }


}
