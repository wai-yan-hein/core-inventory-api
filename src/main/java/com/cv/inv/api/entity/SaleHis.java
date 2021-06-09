/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Entity
@Table(name = "sale_his")
public class SaleHis implements java.io.Serializable {

    @Id
    @Column(name = "voucher_no", unique = true, nullable = false, length = 20)
    private String vouNo;
    @ManyToOne
    @JoinColumn(name = "cus_code")
    private Trader traderId;
    @ManyToOne
    @JoinColumn(name = "saleman_code")
    private SaleMan saleMan;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sale_date")
    private Date saleDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "credit_term")
    private Date creditTerm;
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "from_cur_id"),
        @JoinColumn(name = "comp_code")
    })
    private Currency currency;
    @ManyToOne
    @JoinColumn(name = "vou_status_code")
    private VouStatus vouStatus;
    @Column(name = "remark", length = 500)
    private String remark;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "discount")
    private Float discount;
    @Column(name = "disc_p")
    private Float discP;
    @Column(name = "tax_amt")
    private Float taxAmt;
    @Column(name = "tax_p")
    private Float taxP;
    @Column(name = "deleted")
    private Boolean deleted;
    @Column(name = "grand_total")
    private Float grandTotal;
    @Column(name = "paid")
    private Float paid;
    @Column(name = "vou_balance")
    private Float vouBalance;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @Column(name = "session_id")
    private Integer session;
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "address")
    private String address;
    @Column(name = "order_code")
    private String orderCode;
    @ManyToOne
    @JoinColumn(name = "reg_code")
    private Region region;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "mac_id")
    private Integer macId;

    public SaleHis() {
    }

    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public Trader getTraderId() {
        return traderId;
    }

    public void setTraderId(Trader traderId) {
        this.traderId = traderId;
    }

    public SaleMan getSaleMan() {
        return saleMan;
    }

    public void setSaleMan(SaleMan saleMan) {
        this.saleMan = saleMan;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public Date getCreditTerm() {
        return creditTerm;
    }

    public void setCreditTerm(Date creditTerm) {
        this.creditTerm = creditTerm;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Float getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Float vouTotal) {
        this.vouTotal = vouTotal;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Float getDiscP() {
        return discP;
    }

    public void setDiscP(Float discP) {
        this.discP = discP;
    }

    public Float getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Float taxAmt) {
        this.taxAmt = taxAmt;
    }

    public Float getTaxP() {
        return taxP;
    }

    public void setTaxP(Float taxP) {
        this.taxP = taxP;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Float getPaid() {
        return paid;
    }

    public void setPaid(Float paid) {
        this.paid = paid;
    }

    public Float getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Float vouBalance) {
        this.vouBalance = vouBalance;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public AppUser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(AppUser updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getMacId() {
        return macId;
    }

    public void setMacId(Integer macId) {
        this.macId = macId;
    }

    public VouStatus getVouStatus() {
        return vouStatus;
    }

    public void setVouStatus(VouStatus vouStatus) {
        this.vouStatus = vouStatus;
    }

}
