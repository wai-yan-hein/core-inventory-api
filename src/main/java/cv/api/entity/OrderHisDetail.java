/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import jakarta.persistence.*;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "order_his_detail")
public class OrderHisDetail {

    @EmbeddedId
    private OrderDetailKey key;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "order_qty")
    private double orderQty;
    @Column(name = "qty", nullable = false)
    private Double qty;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "price", nullable = false)
    private double price;
    @Column(name = "amt", nullable = false)
    private double amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "weight_unit")
    private String weightUnit;
    @Column(name = "design")
    private String design;
    @Column(name = "size")
    private String size;
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
    @Transient
    private String traderName;
}
