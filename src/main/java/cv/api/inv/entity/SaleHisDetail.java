/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "sale_his_detail")
public class SaleHisDetail implements java.io.Serializable {

    @EmbeddedId
    private SaleDetailKey sdKey;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {@JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")), @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")), @JoinColumnOrFormula(column = @JoinColumn(name = "stock_code"))})
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {@JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")), @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")), @JoinColumnOrFormula(column = @JoinColumn(name = "sale_unit"))})
    private StockUnit saleUnit;
    @Column(name = "sale_price", nullable = false)
    private Float price;
    @Column(name = "sale_amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
