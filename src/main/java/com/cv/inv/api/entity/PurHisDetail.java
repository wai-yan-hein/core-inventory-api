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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "pur_his_detail")
public class PurHisDetail implements Serializable {

    @EmbeddedId
    private PurDetailKey purDetailKey;
    @ManyToOne
    @JoinColumn(name = "stock_code", nullable = false)
    private Stock stock;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "std_wt", nullable = false)
    private Float stdWeight;
    @ManyToOne
    @JoinColumn(name = "item_unit_code", nullable = false)
    private StockUnit purUnit;
    @Column(name = "avg_wt")
    private Float avgWeight;
    @Column(name = "avg_price")
    private Float avgPrice;
    @Column(name = "pur_price", nullable = false)
    private Float purPrice;
    @Column(name = "pur_amt", nullable = false)
    private Float purAmt;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "gl_id")
    private Long glCode;
    @Column(name = "small_wt")
    private Float smallestWT;
    @Column(name = "small_unit")
    private String smallestUnit;
    @Column(name = "std_small_wt")
    private Float stdSmallWeight;

    public PurDetailKey getPurDetailKey() {
        return purDetailKey;
    }

    public void setPurDetailKey(PurDetailKey purDetailKey) {
        this.purDetailKey = purDetailKey;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public Float getStdWeight() {
        return stdWeight;
    }

    public void setStdWeight(Float stdWeight) {
        this.stdWeight = stdWeight;
    }

    public StockUnit getPurUnit() {
        return purUnit;
    }

    public void setPurUnit(StockUnit purUnit) {
        this.purUnit = purUnit;
    }

    public Float getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(Float avgWeight) {
        this.avgWeight = avgWeight;
    }

    public Float getPurPrice() {
        return purPrice;
    }

    public void setPurPrice(Float purPrice) {
        this.purPrice = purPrice;
    }

    public Float getPurAmt() {
        return purAmt;
    }

    public void setPurAmt(Float purAmt) {
        this.purAmt = purAmt;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getGlId() {
        return glCode;
    }

    public void setGlId(Long glCode) {
        this.glCode = glCode;
    }

    public Float getSmallestWT() {
        return smallestWT;
    }

    public void setSmallestWT(Float smallestWT) {
        this.smallestWT = smallestWT;
    }

    public String getSmallestUnit() {
        return smallestUnit;
    }

    public void setSmallestUnit(String smallestUnit) {
        this.smallestUnit = smallestUnit;
    }

    public Float getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Float avgPrice) {
        this.avgPrice = avgPrice;
    }

    public Float getStdSmallWeight() {
        return stdSmallWeight;
    }

    public void setStdSmallWeight(Float stdSmallWeight) {
        this.stdSmallWeight = stdSmallWeight;
    }

}
