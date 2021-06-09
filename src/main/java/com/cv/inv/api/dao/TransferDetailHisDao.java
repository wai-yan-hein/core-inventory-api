/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.TransferDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
public interface TransferDetailHisDao {

    public TransferDetailHis save(TransferDetailHis sdh);

    public TransferDetailHis findById(Long id);

    public List<TransferDetailHis> search(String saleInvId);

    public int delete(String id);
}
