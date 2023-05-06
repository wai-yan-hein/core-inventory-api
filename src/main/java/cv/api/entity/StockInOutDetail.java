/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "stock_in_out_detail")
public class StockInOutDetail {

    @EmbeddedId
    private StockInOutKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "in_qty")
    private Float inQty;
    @Column(name = "in_unit")
    private String inUnitCode;
    @Column(name = "out_qty")
    private Float outQty;
    @Column(name = "out_unit")
    private String outUnitCode;
    @Column(name = "cost_price")
    private Float costPrice;
    @Transient
    private String userCode;
    @Transient
    private String stockName;
    @Transient
    private String groupName;
    @Transient
    private String brandName;
    @Transient
    private String catName;
    @Transient
    private String relName;
    @Transient
    private String locName;
}
