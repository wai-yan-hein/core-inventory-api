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
import java.io.Serializable;
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
@Table(name = "stock_in_out_detail")
public class StockInOutDetail implements Serializable {

    @EmbeddedId
    private StockInOutKey ioKey;
    @ManyToOne
    @JoinColumns({@JoinColumn(name = "stock_code"), @JoinColumn(name = "comp_code"), @JoinColumn(name = "dept_id")})
    private Stock stock;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "in_qty")
    private Float inQty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {@JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")), @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")), @JoinColumnOrFormula(column = @JoinColumn(name = "in_unit"))})
    private StockUnit inUnit;
    @Column(name = "out_qty")
    private Float outQty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {@JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")), @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")), @JoinColumnOrFormula(column = @JoinColumn(name = "out_unit"))})
    private StockUnit outUnit;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "cost_price")
    private Float costPrice;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
