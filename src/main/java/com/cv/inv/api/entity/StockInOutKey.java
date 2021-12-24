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
 * @author lenovo
 */
@Data
@Embeddable
public class StockInOutKey implements Serializable {

    @Column(name = "sd_code", unique = true, nullable = false)
    private String sdCode;
    @Column(name = "vou_no")
    private String vouNo;

    public StockInOutKey() {
    }

    public StockInOutKey(String sdCode, String vouNo) {
        this.sdCode = sdCode;
        this.vouNo = vouNo;
    }
}
