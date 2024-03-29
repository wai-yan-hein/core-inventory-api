/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class Stock {

    private StockKey key;
    private Boolean active;
    private String typeCode;
    private String brandCode;
    private String stockName;
    private String catCode;
    private String purUnitCode;
    private String saleUnitCode;
    private String createdBy;
    private String updatedBy;
    private String barcode;
    private String shortName;
    private Double purPrice;
    private LocalDate expireDate;
    private String remark;
    private Double salePriceN;
    private Double salePriceA;
    private Double salePriceB;
    private Double salePriceC;
    private Double salePriceD;
    private Double salePriceE;
    private LocalDateTime updatedDate;
    private LocalDateTime createdDate;
    private String migCode;
    private String userCode;
    private String relCode;
    private Integer macId;
    private Boolean calculate;
    private String intgUpdStatus;
    private Boolean explode;
    private String weightUnit;
    private Double weight;
    private Boolean favorite;
    private Integer deptId;
    private Boolean saleClosed;
    private Double saleQty;
    private Boolean deleted;
    private String formulaCode;
    private Double purAmt;
    private Double saleAmt;
    private Double purQty;
    private Double saleWt;
    private Double purWt;
    private String relName;
    private String groupName;
    private String brandName;
    private String catName;

}
