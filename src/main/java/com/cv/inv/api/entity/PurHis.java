/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "pur_his")
public class PurHis implements java.io.Serializable {

    private String vouNo;
    private Trader trader;
    private Date purDate;
    private Date dueDate;
    private Location location;
    private Boolean deleted;
    private Float vouTotal;
    private Float paid;
    private Float discount;
    private Float balance;
    private AppUser createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String remark;
    private String refNo;
    private VouStatus vouStatus;
    private Float expenseTotal;
    private Integer session;
    private Currency currency;
    private Float discP;
    private Float taxP;
    private Float taxAmt;
    private boolean cashOut; //for purchase expense
    private Float exRateP; //for parent currency
    private String migId;
    private String promoDesp;
    private Date promoStartDate;
    private Date promoEndDate;
    private Float promoGivePercent;
    private Float promoGetPercent;
    private boolean promoGetComplete;
    private String intgUpdStatus;
    private Integer macId;

    public PurHis() {
    }

    @Id
    @Column(name = "vou_no", unique = true, nullable = false, length = 15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name = "balance")
    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    @ManyToOne
    @JoinColumn(name = "created_by")
    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JoinColumn(name = "trader_code")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @Column(name = "deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "discount")
    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @ManyToOne
    @JoinColumn(name = "loc_code")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "paid")
    public Float getPaid() {
        return paid;
    }

    public void setPaid(Float paid) {
        this.paid = paid;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pur_date", nullable = false)
    public Date getPurDate() {
        return purDate;
    }

    public void setPurDate(Date purDate) {
        this.purDate = purDate;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "vou_total")
    public Float getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Float vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name = "ref_no")
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @ManyToOne
    @JoinColumn(name = "vou_status")
    public VouStatus getVouStatus() {
        return vouStatus;
    }

    public void setVouStatus(VouStatus vouStatus) {
        this.vouStatus = vouStatus;
    }

    @Column(name = "pur_exp_total")
    public Float getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(Float expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    @Column(name = "session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @ManyToOne
    @JoinColumn(name = "cur_code")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "disc_p")
    public Float getDiscP() {
        return discP;
    }

    public void setDiscP(Float discP) {
        this.discP = discP;
    }

    @Column(name = "tax_p")
    public Float getTaxP() {
        return taxP;
    }

    public void setTaxP(Float taxP) {
        this.taxP = taxP;
    }

    @Column(name = "tax_amt")
    public Float getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Float taxAmt) {
        this.taxAmt = taxAmt;
    }

    @Column(name = "cash_out")
    public boolean isCashOut() {
        return cashOut;
    }

    public void setCashOut(boolean cashOut) {
        this.cashOut = cashOut;
    }

    @Column(name = "exchange_rate_p")
    public Float getExRateP() {
        return exRateP;
    }

    public void setExRateP(Float exRateP) {
        this.exRateP = exRateP;
    }

    @Column(name = "mig_id", length = 25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @Column(name = "promo_desp", length = 500)
    public String getPromoDesp() {
        return promoDesp;
    }

    public void setPromoDesp(String promoDesp) {
        this.promoDesp = promoDesp;
    }

    @Column(name = "promo_start_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(Date promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    @Column(name = "promo_end_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(Date promoEndDate) {
        this.promoEndDate = promoEndDate;
    }

    @Column(name = "promo_give_percent")
    public Float getPromoGivePercent() {
        return promoGivePercent;
    }

    public void setPromoGivePercent(Float promoGivePercent) {
        this.promoGivePercent = promoGivePercent;
    }

    @Column(name = "promo_get_percent")
    public Float getPromoGetPercent() {
        return promoGetPercent;
    }

    public void setPromoGetPercent(Float promoGetPercent) {
        this.promoGetPercent = promoGetPercent;
    }

    @Column(name = "promo_get_complete")
    public boolean isPromoGetComplete() {
        return promoGetComplete;
    }

    public void setPromoGetComplete(boolean promoGetComplete) {
        this.promoGetComplete = promoGetComplete;
    }

    @Column(name = "intg_upd_status")
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }

    @Column(name = "mac_id")
    public Integer getMacId() {
        return macId;
    }

    public void setMacId(Integer macId) {
        this.macId = macId;
    }
}
