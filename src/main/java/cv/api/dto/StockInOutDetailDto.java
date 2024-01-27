/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author wai yan
 */
@Data
@Builder
public class StockInOutDetailDto {

    private StockInOutKeyDto key;
    private Integer deptId;
    private String stockCode;
    private String locCode;
    private Double inQty;
    private String inUnitCode;
    private Double outQty;
    private String outUnitCode;
    private Double costPrice;
    private Double weight;
    private String weightUnit;
    private Double totalWeight;
    private Double wet;
    private Double rice;
    private Double inBag;
    private Double outBag;
    private Double amount;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;

}
