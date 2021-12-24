/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author Lenovo
 */
@Data
@Embeddable
public class StockBalanceKey implements Serializable {

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "loc_code")
    private String locCode;

    @Column(name = "mac_id")
    private Integer macId;
}
