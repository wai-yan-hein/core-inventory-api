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

    public SaleDetailKey getSdKey() {
        return sdKey;
    }

    public void setSdKey(SaleDetailKey sdKey) {
        this.sdKey = sdKey;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public StockUnit getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(StockUnit saleUnit) {
        this.saleUnit = saleUnit;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
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

    public Float getSaleWt() {
        return saleWt;
    }

    public void setSaleWt(Float saleWt) {
        this.saleWt = saleWt;
    }

}
