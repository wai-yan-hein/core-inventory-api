/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "stock_issue_detail_his")
public class StockIssueDetailHis implements java.io.Serializable {

    private Long tranId;
    private String issueId;
    private Stock issueStock;
    private Float unitQty;
    private StockUnit itemUnit;
    private Float smallestQty;
    private Integer uniqueId;
    private String issueOpt; //Sale, FOC or Borrow
    private String refVou;
    private Trader trader;
    private String strOutstanding;
    private Float outsBalance; //Outstanding balance
    private String balance;

    @ManyToOne
    @JoinColumn(name = "med_id")
    public Stock getIssueStock() {
        return issueStock;
    }

    public void setIssueStock(Stock issueStock) {
        this.issueStock = issueStock;
    }

    @Column(name = "smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "issue_detail_id", unique = true, nullable = false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public StockUnit getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(StockUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    @Column(name = "unit_qty")
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @Column(name = "issue_opt")
    public String getIssueOpt() {
        return issueOpt;
    }

    public void setIssueOpt(String issueOpt) {
        this.issueOpt = issueOpt;
    }

    @Column(name = "ref_vou", length = 15)
    public String getRefVou() {
        return refVou;
    }

    public void setRefVou(String refVou) {
        this.refVou = refVou;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @Transient
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Transient
    public Float getOutsBalance() {
        return outsBalance;
    }

    public void setOutsBalance(Float outsBalance) {
        this.outsBalance = outsBalance;
    }

    @Transient
    public String getStrOutstanding() {
        return strOutstanding;
    }

    public void setStrOutstanding(String strOutstanding) {
        this.strOutstanding = strOutstanding;
    }

    @Column(name = "issue_id", length = 15)
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

}
