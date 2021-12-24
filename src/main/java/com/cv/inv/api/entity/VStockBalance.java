/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lenovo
 */
@Data
public class VStockBalance implements Serializable {

    private String stockCode;
    private String stockName;
    private String locCode;
    private String locationName;
    private Float totalQty;
    private Float weight;
    private String unitName;

}
