/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "ret_out_his_detail")
public class RetOutHisDetail implements java.io.Serializable {

    @EmbeddedId
    private RetOutKey roKey;
    @ManyToOne
    @JoinColumns({@JoinColumn(name = "stock_code"), @JoinColumn(name = "comp_code"), @JoinColumn(name = "dept_id")})
    private Stock stock;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {@JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")), @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")), @JoinColumnOrFormula(column = @JoinColumn(name = "unit"))})
    private StockUnit unit;
    @Column(name = "price", nullable = false)
    private Float price;
    @Column(name = "amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;

}
