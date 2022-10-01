/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "ret_in_his_detail")
public class RetInHisDetail implements java.io.Serializable {

    @EmbeddedId
    private RetInKey riKey;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "stock_code", referencedColumnName = "stock_code"),
            @JoinColumn(name = "comp_code", referencedColumnName = "comp_code")
    })
    private Stock stock;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @ManyToOne
    @JoinColumn(name = "unit", nullable = false)
    private StockUnit unit;
    @Column(name = "cost_price")
    private Float costPrice;
    @Column(name = "price", nullable = false)
    private Float price;
    @Column(name = "amt", nullable = false)
    private Float amount;
    @ManyToOne
    @JoinColumn(name = "loc_code")
    private Location location;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "wt")
    private Float wt;
    @Column(name = "comp_code", insertable = false, updatable = false)
    private String compCode;
}
