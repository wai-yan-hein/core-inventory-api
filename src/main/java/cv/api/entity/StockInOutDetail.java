/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "in_qty")
    private Double inQty;
    @Column(name = "in_unit")
    private String inUnitCode;
    @Column(name = "out_qty")
    private Double outQty;
    @Column(name = "out_unit")
    private String outUnitCode;
    @Column(name = "cost_price")
    private Double costPrice;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "total_weight")
    private Double totalWeight;
    @Column(name = "wet")
    private Double wet;
    @Column(name = "rice")
    private Double rice;
    @Column(name = "in_bag")
    private Double inBag;
    @Column(name = "out_bag")
    private Double outBag;
    @Column(name = "amount")
    private Double amount;
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
