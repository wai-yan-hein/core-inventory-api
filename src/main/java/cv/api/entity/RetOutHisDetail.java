/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class RetOutHisDetail {

    private RetOutKey key;
    private Integer deptId;
    private String stockCode;
    private Double qty;
    private String unitCode;
    private Double price;
    private Double amount;
    private String locCode;
    private Double weight;
    private String weightUnit;
    private Double totalWeight;
    private Double wet;
    private Double rice;
    private Double bag;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String relCode;
    private String locName;
    private String unit;
    private String remark;
    private String vouDate;
    private Double vouTotal;
    private Double paid;
    private Double vouBalance;
    private String vouNo;
    private String traderName;
}


