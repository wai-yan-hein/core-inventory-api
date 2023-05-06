/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

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
    private RetInKey key;
    @Column(name = "stock_code")
    private String stockCode;
    @Column(name = "qty", nullable = false)
    private Float qty;
    @Column(name = "avg_qty")
    private Float avgQty;
    @Column(name = "unit")
    private String unitCode;
    @Column(name = "price", nullable = false)
    private Float price;
    @Column(name = "amt", nullable = false)
    private Float amount;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "comp_code")
    private String compCode;
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
