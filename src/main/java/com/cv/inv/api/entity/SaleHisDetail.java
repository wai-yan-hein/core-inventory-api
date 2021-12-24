/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Data
@Entity
@Table(name = "sale_his_detail")
public class SaleHisDetail implements java.io.Serializable {

    @EmbeddedId
    private SaleDetailKey sdKey;
    @ManyToOne
    @JoinColumn(name = "stock_code", nullable = false)
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumn(name = "sale_unit", nullable = false)
    private StockUnit saleUnit;
    @Column(name = "sale_price", nullable = false)
    private Float price;
    @Column(name = "sale_amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "sale_wt")
    private Float saleWt;
}
