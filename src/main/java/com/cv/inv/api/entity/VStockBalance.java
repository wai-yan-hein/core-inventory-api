/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "v_tmp_stock_balance")
public class VStockBalance implements Serializable {

    @EmbeddedId
    private StockBalanceKey key;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "qty")
    private Float qty;

    @Column(name = "wt")
    private Float wt;

    @Column(name = "unit")
    private String unitCode;

    @Column(name = "unit_name")
    private String uniName;

    @Column(name = "loc_name")
    private String locName;

}
