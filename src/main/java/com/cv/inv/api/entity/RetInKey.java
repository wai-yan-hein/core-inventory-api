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
public class RetInKey implements Serializable {

    @Column(name = "rd_code", unique = true, nullable = false)
    private String rdCode;
    @Column(name = "vou_no")
    private String vouNo;

    public RetInKey() {
    }

    public RetInKey(String rdCode, String vouNo) {
        this.rdCode = rdCode;
        this.vouNo = vouNo;
    }
}
