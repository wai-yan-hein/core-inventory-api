/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.TransferDetailHis;
import com.cv.inv.api.entity.TransferHis;
import java.util.List;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
public interface TransferHisService {

    public TransferHis save(TransferHis sdh);

    public List<TransferHis> search(String from, String to, String location,
            String remark, String vouNo);

    public void save(TransferHis sdh, List<TransferDetailHis> listTransferDetail, String vouStatus, List<String> delList);

    public TransferHis findById(String id);

    public int delete(String vouNo);
}
