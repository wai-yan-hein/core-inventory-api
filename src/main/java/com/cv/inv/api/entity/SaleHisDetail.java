/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Table(name = "sale_his_detail")
public class SaleHisDetail implements java.io.Serializable {

    private SaleDetailKey saleDetailKey;
    private Stock stock;
    private Date expDate;
    private Float quantity;
    private Float saleSmallestQty;
    private StockUnit saleUnit;
    private Float price;
    private Float amount;
    private Location location;
    private Integer uniqueId;
    private Float stdWeight;
    private Long glCode;
    private Float smallestWT;
    private String smallestUnit;
    private Float stdSmallWeight;

    @EmbeddedId
    public SaleDetailKey getSaleDetailKey() {
        return saleDetailKey;
    }

    public void setSaleDetailKey(SaleDetailKey saleDetailKey) {
        this.saleDetailKey = saleDetailKey;
    }

    @ManyToOne
    @JoinColumn(name = "stock_code", nullable = false)
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name = "qty", nullable = false)
    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    @Column(name = "sale_smallest_qty")
    public Float getSaleSmallestQty() {
        return saleSmallestQty;
    }

    public void setSaleSmallestQty(Float saleSmallestQty) {
        this.saleSmallestQty = saleSmallestQty;
    }

    @ManyToOne
    @JoinColumn(name = "sale_unit", nullable = false)
    public StockUnit getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(StockUnit saleUnit) {
        this.saleUnit = saleUnit;
    }

    @Column(name = "sale_price", nullable = false)
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Column(name = "sale_amount", nullable = false)
    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "loc_code")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "std_weight", nullable = false)
    public Float getStdWeight() {
        return stdWeight;
    }

    public void setStdWeight(Float stdWeight) {
        this.stdWeight = stdWeight;
    }

    @Column(name = "gl_id")
    public Long getGlId() {
        return glCode;
    }

    public void setGlId(Long glCode) {
        this.glCode = glCode;
    }

    @Column(name = "small_wt")
    public Float getSmallestWT() {
        return smallestWT;
    }

    public void setSmallestWT(Float smallestWT) {
        this.smallestWT = smallestWT;
    }

    @Column(name = "small_unit")
    public String getSmallestUnit() {
        return smallestUnit;
    }

    public void setSmallestUnit(String smallestUnit) {
        this.smallestUnit = smallestUnit;
    }

    @Column(name = "std_small_wt")
    public Float getStdSmallWeight() {
        return stdSmallWeight;
    }

    public void setStdSmallWeight(Float stdSmallWeight) {
        this.stdSmallWeight = stdSmallWeight;
    }

}
