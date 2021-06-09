/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Lenovo
 */
@Entity
@Table(name = "stock_in_out_detail")
public class StockInOutDetail implements Serializable {

    @Id
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    @ManyToOne
    @JoinColumn(name = "stock_code")
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "in_qty")
    private Float inQty;
    @Column(name = "in_weight")
    private Float inWeight;
    @ManyToOne
    @JoinColumn(name = "in_unit")
    private StockUnit inUnit;
    @Column(name = "out_qty")
    private Float outQty;
    @Column(name = "out_weight")
    private Float outWeight;
    @ManyToOne
    @JoinColumn(name = "out_unit")
    private StockUnit outUnit;
    @Column(name = "desp")
    private String description;
    @Column(name = "remark")
    private String remark;
    @Column(name = "small_in_weight")
    private Float smallInWeight;
    @ManyToOne
    @JoinColumn(name = "small_in_unit")
    private StockUnit smallInUnit;
    @Column(name = "small_out_weight")
    private Float smallOutWeight;
    @ManyToOne
    @JoinColumn(name = "small_out_unit")
    private StockUnit smallOutUnit;
    @Column(name = "batch_code")
    private String batchCode;
    @Column(name = "unique_id")
    private Integer uniqueId;

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Float getInQty() {
        return inQty;
    }

    public void setInQty(Float inQty) {
        this.inQty = inQty;
    }

    public Float getInWeight() {
        return inWeight;
    }

    public void setInWeight(Float inWeight) {
        this.inWeight = inWeight;
    }

    public StockUnit getInUnit() {
        return inUnit;
    }

    public void setInUnit(StockUnit inUnit) {
        this.inUnit = inUnit;
    }

    public Float getOutQty() {
        return outQty;
    }

    public void setOutQty(Float outQty) {
        this.outQty = outQty;
    }

    public Float getOutWeight() {
        return outWeight;
    }

    public void setOutWeight(Float outWeight) {
        this.outWeight = outWeight;
    }

    public StockUnit getOutUnit() {
        return outUnit;
    }

    public void setOutUnit(StockUnit outUnit) {
        this.outUnit = outUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Float getSmallInWeight() {
        return smallInWeight;
    }

    public void setSmallInWeight(Float smallInWeight) {
        this.smallInWeight = smallInWeight;
    }

    public StockUnit getSmallInUnit() {
        return smallInUnit;
    }

    public void setSmallInUnit(StockUnit smallInUnit) {
        this.smallInUnit = smallInUnit;
    }

    public Float getSmallOutWeight() {
        return smallOutWeight;
    }

    public void setSmallOutWeight(Float smallOutWeight) {
        this.smallOutWeight = smallOutWeight;
    }

    public StockUnit getSmallOutUnit() {
        return smallOutUnit;
    }

    public void setSmallOutUnit(StockUnit smallOutUnit) {
        this.smallOutUnit = smallOutUnit;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

}
