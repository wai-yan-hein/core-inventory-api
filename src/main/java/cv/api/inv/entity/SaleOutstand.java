/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "sale_outstanding")
public class SaleOutstand implements java.io.Serializable {

    private Integer outsId;
    private Stock stock;
    private String qtyStr;
    private Float outsQtySmall;
    private String outsOption;
    private Date updatedDate;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "outstanding_id", unique = true, nullable = false)
    public Integer getOutsId() {
        return outsId;
    }

    public void setOutsId(Integer outsId) {
        this.outsId = outsId;
    }

    @ManyToOne
    @JoinColumn(name = "stock_id")
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    @Column(name = "qty_str", length = 45)
    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }

    @Column(name = "outs_qty_small")
    public Float getOutsQtySmall() {
        return outsQtySmall;
    }

    public void setOutsQtySmall(Float outsQtySmall) {
        this.outsQtySmall = outsQtySmall;
    }

    @Column(name = "outs_option", length = 20)
    public String getOutsOption() {
        return outsOption;
    }

    public void setOutsOption(String outsOption) {
        this.outsOption = outsOption;
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
