/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @author wai yan
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VReturnIn {

    private String rdCode;
    private String vouNo;
    private String traderCode;
    private String vouDate;
    private ZonedDateTime vouDateTime;
    private String curCode;
    private String remark;
    private Double vouTotal;
    private Double discount;
    private Double discountPrice;
    private String createdBy;
    private Boolean deleted;
    private Double paid;
    private Double vouBalance;
    private String compCode;
    private Integer macId;
    private String stockCode;
    private Double qty;
    private Double wt;
    private String unit;
    private Double price;
    private Double amount;
    private String locCode;
    private Integer uniqueId;
    private String traderName;
    private String stockName;
    private String locationName;
    private Integer deptId;
}
