/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHisDetail;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public interface PurchaseDetailDao {

    public PurHisDetail save(PurHisDetail pd);

    public List<PurHisDetail> search(String glCode);

    public int delete(String id) throws Exception;

}
