/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
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
@Table(name = "sale_detail_his")
public class SaleDetailHis1 implements java.io.Serializable {

    private Integer saleDetailId;
    private String vouNo;
    private Stock stock;
    private Date expDate;
    private Float quantity;
    private Float saleSmallestQty;
    private StockUnit itemUnit;
    private Float price;
    private Float discount;
    private String discType;
    private Float amount;
    private Location location;
    private Integer uniqueId;
    private Float stdWeight;
    private Long glCode;
    private Float smallestWT;
    private String smallestUnit;
    private String chargeType;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "sale_detail_id", unique = true, nullable = false)
    public Integer getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(Integer saleDetailId) {
        this.saleDetailId = saleDetailId;
    }

    @Column(name = "voucher_id", nullable = false)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
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

    @Column(name = "sale_qty", nullable = false)
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
    @JoinColumn(name = "item_unit", nullable = false)
    public StockUnit getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(StockUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    @Column(name = "sale_price", nullable = false)
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Column(name = "item_discount")
    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    @Column(name = "disc_type")
    public String getDiscType() {
        return discType;
    }

    public void setDiscType(String discType) {
        this.discType = discType;
    }

    @Column(name = "sale_amount", nullable = false)
    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "location_id")
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

    @Column(name = "smallest_wt")
    public Float getSmallestWT() {
        return smallestWT;
    }

    public void setSmallestWT(Float smallestWT) {
        this.smallestWT = smallestWT;
    }

    @Column(name = "smallest_unit")
    public String getSmallestUnit() {
        return smallestUnit;
    }

    public void setSmallestUnit(String smallestUnit) {
        this.smallestUnit = smallestUnit;
    }

    @Column(name = "charge_type")
    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }
}
