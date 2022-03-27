/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author wai yan
 */
@Entity
@Table(name = "stock_receive_detail_his")
public class StockReceiveDetailHis implements java.io.Serializable {
   private Long tranId;
  private String recOption; //Borrow or Purchase outstand
  private String refVou;
  private Stock orderMed;
  private Stock recMed;
  private Date expDate;
  private Float unitQty;
  private StockUnit unit;
  private Float smallestQty;
  private boolean fullReceive;
  private Integer uniqueId;
  private String traderId;
  private String strOutstanding;
  private Float outsBalance; //Outstanding balance
  private String balance;
  private Double costPrice;

  @Temporal(TemporalType.DATE)
  @Column(name = "expire_date")
  public Date getExpDate() {
    return expDate;
  }

  public void setExpDate(Date expDate) {
    this.expDate = expDate;
  }

  @Column(name = "full_receive")
  public boolean isFullReceive() {
    return fullReceive;
  }

  public void setFullReceive(boolean fullReceive) {
    this.fullReceive = fullReceive;
  }

  @ManyToOne
  @JoinColumn(name = "order_med_id")
  public Stock getOrderMed() {
    return orderMed;
  }

  public void setOrderMed(Stock orderMed) {
    this.orderMed = orderMed;
  }

  @ManyToOne
  @JoinColumn(name = "rec_med_id")
  public Stock getRecMed() {
    return recMed;
  }

  public void setRecMed(Stock recMed) {
    this.recMed = recMed;
  }

  @Column(name = "rec_option")
  public String getRecOption() {
    return recOption;
  }

  public void setRecOption(String recOption) {
    this.recOption = recOption;
  }

  @Column(name = "ref_vou", length = 15)
  public String getRefVou() {
    return refVou;
  }

  public void setRefVou(String refVou) {
    this.refVou = refVou;
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
  @Column(name = "rec_detail_id", unique = true, nullable = false)
  public Long getTranId() {
    return tranId;
  }

  public void setTranId(Long tranId) {
    this.tranId = tranId;
  }

  @ManyToOne
  @JoinColumn(name = "item_unit")
  public StockUnit getUnit() {
    return unit;
  }

  public void setUnit(StockUnit unit) {
    this.unit = unit;
  }

  @Column(name = "unit_qty")
  public Float getUnitQty() {
    return unitQty;
  }

  public void setUnitQty(Float unitQty) {
    this.unitQty = unitQty;
  }

  @Column(name = "unique_id")
  public Integer getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(Integer uniqueId) {
    this.uniqueId = uniqueId;
  }

  @Column(name = "cus_id")
  public String getTraderId() {
    return traderId;
  }

  public void setTraderId(String traderId) {
    this.traderId = traderId;
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

   @Column(name = "cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
   
}
