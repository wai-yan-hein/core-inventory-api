/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "ret_in_his_detail")
public class RetInHisDetail implements java.io.Serializable {

    @EmbeddedId
    private RetInCompoundKey retInKey;
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expireDate;
    @Column(name = "qty")
    private Float qty;
    @Column(name = "price")
    private Float price;
    @ManyToOne
    @JoinColumn(name = "stock_unit")
    private StockUnit stockUnit;
    @Column(name = "amount")
    private Float amount;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "std_wt")
    private Float stdWt;
    @Column(name = "small_wt")
    private Float smallWeight;
    @ManyToOne
    @JoinColumn(name = "small_unit")
    private StockUnit smallUnit;

    public RetInCompoundKey getRetInKey() {
        return retInKey;
    }

    public void setRetInKey(RetInCompoundKey retInKey) {
        this.retInKey = retInKey;
    }

    
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public StockUnit getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(StockUnit stockUnit) {
        this.stockUnit = stockUnit;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Float getStdWt() {
        return stdWt;
    }

    public void setStdWt(Float stdWt) {
        this.stdWt = stdWt;
    }

    public Float getSmallWeight() {
        return smallWeight;
    }

    public void setSmallWeight(Float smallWeight) {
        this.smallWeight = smallWeight;
    }

    public StockUnit getSmallUnit() {
        return smallUnit;
    }

    public void setSmallUnit(StockUnit smallUnit) {
        this.smallUnit = smallUnit;
    }

}
