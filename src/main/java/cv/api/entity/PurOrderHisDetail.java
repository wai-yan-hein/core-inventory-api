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
 * @author pann
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class PurOrderHisDetail {
    private PurOrderHisDetailKey key;
    private Integer deptId;
    private String stockCode;
    private Double wet;
    private Double bag;
    private Double qty;
    private Double weight;
    private Double rice;
    private Double price;
    private Double amount;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
}
