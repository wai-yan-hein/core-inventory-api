/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import java.util.Date;

/**
 *
 * @author winswe
 */
public class StockOutstanding {

    private String tranOption;
    private Date tranDate;
    private String cusId;
    private float balanceQty;
    private String invId;
    private String qtyStr;
    private Stock stock;

    public StockOutstanding() {
    }

    public StockOutstanding(String tranOption, Date tranDate, String cusId, float balanceQty,
            String invId, String qtyStr, Stock stock) {
        this.tranOption = tranOption;
        this.tranDate = tranDate;
        this.cusId = cusId;
        this.balanceQty = balanceQty;
        this.invId = invId;
        this.qtyStr = qtyStr;
        this.stock = stock;
    }

    public float getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(float balanceQty) {
        this.balanceQty = balanceQty;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    public String getInvId() {
        return invId;
    }

    public void setInvId(String invId) {
        this.invId = invId;
    }

    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
