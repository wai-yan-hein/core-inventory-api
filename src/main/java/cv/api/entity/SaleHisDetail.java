/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class SaleHisDetail {

    private SaleDetailKey key;
    private Integer deptId;
    private String stockCode;
    private Date expDate;
    private Double qty;
    private String unitCode;
    private Double price;
    private Double amount;
    private String locCode;
    private String batchNo;
    private Double weight;
    private String weightUnit;
    private Double stdWeight;
    private Double totalWeight;
    private Double orgPrice;
    private Double weightLoss;
    private Double wet;
    private Double rice;
    private Double bag;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private String traderName;
    private Boolean calculate;
    private String design;
    private String size;
}
