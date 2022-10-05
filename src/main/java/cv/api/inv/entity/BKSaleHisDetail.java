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
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "bk_sale_his_detail")
public class BKSaleHisDetail implements java.io.Serializable {
    @Id
    @Column(name = "tran_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tranId;
    @Column(name = "log_id")
    private Long logId;
    private SaleDetailKey sdKey;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code"),
            @JoinColumn(name = "comp_code"),
            @JoinColumn(name = "dept_id")

    })
    private Stock stock;
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    private Date expDate;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumnsOrFormulas(value = {
            @JoinColumnOrFormula(formula = @JoinFormula(value = "comp_code")),
            @JoinColumnOrFormula(formula = @JoinFormula(value = "dept_id")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "sale_unit"))
    })
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
