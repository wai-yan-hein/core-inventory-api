/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "transfer_detail_his")
public class TransferDetailHis implements java.io.Serializable {

    private Long tranDetailId;
    private Stock medicineId;
    private Date expireDate;
    private Float qty;
    private Float price;
    private StockUnit unit;
    private Integer uniqueId;
    private Float smallestQty;
    private String inHandQtyStr;
    private Float inHandQtySmall;
    private String balQtyStr;
    private Float amount;
    private String tranVouId;
    private Date updatedDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @ManyToOne
    @JoinColumn(name = "med_id")
    public Stock getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Stock medicineId) {
        this.medicineId = medicineId;
    }

    @Column(name = "tran_price")
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Column(name = "tran_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tran_detail_id", unique = true, nullable = false)
    public Long getTranDetailId() {
        return tranDetailId;
    }

    public void setTranDetailId(Long tranDetailId) {
        this.tranDetailId = tranDetailId;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public StockUnit getUnit() {
        return unit;
    }

    public void setUnit(StockUnit unit) {
        this.unit = unit;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "tran_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Transient
    public String getBalQtyStr() {
        return balQtyStr;
    }

    public void setBalQtyStr(String balQtyStr) {
        this.balQtyStr = balQtyStr;
    }

    @Transient
    public String getInHandQtyStr() {
        return inHandQtyStr;
    }

    public void setInHandQtyStr(String inHandQtyStr) {
        this.inHandQtyStr = inHandQtyStr;
    }

    @Transient
    public Float getInHandQtySmall() {
        return inHandQtySmall;
    }

    public void setInHandQtySmall(Float inHandQtySmall) {
        this.inHandQtySmall = inHandQtySmall;
    }

    @Column(name = "amount")
    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    @Column(name = "transfer_id")
    public String getTranVouId() {
        return tranVouId;
    }

    public void setTranVouId(String tranVouId) {
        this.tranVouId = tranVouId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}
