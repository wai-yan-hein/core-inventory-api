/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class PurHisDetail {

    private PurDetailKey key;
    private Integer deptId;
    private String stockCode;
    private Double qty;
    private Double weightLoss;
    private String unitCode;
    private Double orgPrice;
    private Double price;
    private Double amount;
    private String locCode;
    private Double weight;
    private String weightUnit;
    private Double stdWeight;
    private Double length;
    private Double width;
    private Double totalWeight;
    private String mPercent;
    private Double wet;
    private Double rice;
    private Double bag;
    private Double avgQty;
    private Double avgPrice;
    private LocalDate expDate;
    private String userCode;
    private String stockName;
    private Boolean calculate;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
}
