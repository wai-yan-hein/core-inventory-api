/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "pur_his_detail")
public class PurHisDetail implements Serializable {

    @EmbeddedId
    private PurDetailKey pdKey;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "avg_qty", nullable = false)
    private Float avgQty;
    @Column(name = "pur_unit")
    private StockUnit unitCode;
    @Column(name = "pur_price", nullable = false)
    private Float price;
    @Column(name = "pur_amt", nullable = false)
    private Float amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
}
