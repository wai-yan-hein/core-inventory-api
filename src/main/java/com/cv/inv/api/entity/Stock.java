/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

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
@Table(name = "stock")
public class Stock implements java.io.Serializable {

    @Id
    @Column(name = "stock_code", unique = true, nullable = false, length = 15)
    private String stockCode;
    @Column(name = "active")
    private Boolean isActive;
    @ManyToOne
    @JoinColumn(name = "brand_code")
    private StockBrand brand;
    @Column(name = "stock_name")
    private String stockName;
    @ManyToOne
    @JoinColumn(name = "category_code")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "stock_type_code")
    private StockType stockType;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Column(name = "barcode")
    private String barcode;
    @Column(name = "short_name")
    private String shortName;
    @Column(name = "pur_wt")
    private Float purWeight;
    @Column(name = "pur_price")
    private Float purPrice;
    @ManyToOne
    @JoinColumn(name = "pur_unit")
    private StockUnit purUnit;
    @Column(name = "sale_wt")
    private Float saleWeight;
    @ManyToOne
    @JoinColumn(name = "sale_unit")
    private StockUnit saleUnit;
    @Temporal(TemporalType.DATE)
    @Column(name = "licene_exp_date")
    private Date expireDate;
    @Column(name = "remark")
    private String remark;
    @Column(name = "sale_price_n")
    private Float salePriceN;
    @Column(name = "sale_price_a")
    private Float salePriceA;
    @Column(name = "sale_price_b")
    private Float salePriceB;
    @Column(name = "sale_price_c")
    private Float salePriceC;
    @Column(name = "sale_price_d")
    private Float salePriceD;
    @Column(name = "cost_price_std")
    private Float sttCostPrice;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "mig_code")
    private String migCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "mac_id")
    private Integer macId;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public StockBrand getBrand() {
        return brand;
    }

    public void setBrand(StockBrand brand) {
        this.brand = brand;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public StockType getStockType() {
        return stockType;
    }

    public void setStockType(StockType stockType) {
        this.stockType = stockType;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    public AppUser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(AppUser updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Float getPurPrice() {
        return purPrice;
    }

    public void setPurPrice(Float purPrice) {
        this.purPrice = purPrice;
    }

    public StockUnit getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(StockUnit saleUnit) {
        this.saleUnit = saleUnit;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Float getSalePriceN() {
        return salePriceN;
    }

    public void setSalePriceN(Float salePriceN) {
        this.salePriceN = salePriceN;
    }

    public Float getSalePriceA() {
        return salePriceA;
    }

    public void setSalePriceA(Float salePriceA) {
        this.salePriceA = salePriceA;
    }

    public Float getSalePriceB() {
        return salePriceB;
    }

    public void setSalePriceB(Float salePriceB) {
        this.salePriceB = salePriceB;
    }

    public Float getSalePriceC() {
        return salePriceC;
    }

    public void setSalePriceC(Float salePriceC) {
        this.salePriceC = salePriceC;
    }

    public Float getSalePriceD() {
        return salePriceD;
    }

    public void setSalePriceD(Float salePriceD) {
        this.salePriceD = salePriceD;
    }

    public Float getSttCostPrice() {
        return sttCostPrice;
    }

    public void setSttCostPrice(Float sttCostPrice) {
        this.sttCostPrice = sttCostPrice;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public StockUnit getPurUnit() {
        return purUnit;
    }

    public void setPurUnit(StockUnit purUnit) {
        this.purUnit = purUnit;
    }

    public String getMigCode() {
        return migCode;
    }

    public void setMigCode(String migCode) {
        this.migCode = migCode;
    }

    public Float getPurWeight() {
        return purWeight;
    }

    public void setPurWeight(Float purWeight) {
        this.purWeight = purWeight;
    }

    public Float getSaleWeight() {
        return saleWeight;
    }

    public void setSaleWeight(Float saleWeight) {
        this.saleWeight = saleWeight;
    }

    public String getCompCode() {
        return compCode;
    }

    public void setCompCode(String compCode) {
        this.compCode = compCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getMacId() {
        return macId;
    }

    public void setMacId(Integer macId) {
        this.macId = macId;
    }

    @Override
    public String toString() {
        return "Stock{" + "stockName=" + stockName + '}';
    }

}
