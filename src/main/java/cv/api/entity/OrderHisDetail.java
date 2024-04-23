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
public class OrderHisDetail {

    private OrderDetailKey key;
    private Integer deptId;
    private String stockCode;
    private Double orderQty;
    private Double qty;
    private String unitCode;
    private Double price;
    private Double amount;
    private String locCode;
    private Double weight;
    private String weightUnit;
    private String design;
    private String size;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private String traderName;
    private String phoneNo;
    private String address;
    private String rfId;
    private String vouDateStr;
    private String traderCode;
    private String remark;
    private Double salePrice;
    private Double saleAmount;
    private String saleUnit;
    private String locationName;
    private String createdBy;
    private String orderStatusName;
}
