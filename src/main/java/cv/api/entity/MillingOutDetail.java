/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
@Builder
public class MillingOutDetail {

    private MillingOutDetailKey key;
    private String stockCode;
    private Integer deptId;
    private Double qty;
    private String unitCode;
    private Double price;
    private Double amount;
    private String locCode;
    private Double weight;
    private String weightUnit;
    private Double percent;
    private Double totalWeight;
    private Double percentQty;
    private Integer sortId;
    private String userCode;
    private String stockName;
    private String groupName;
    private String brandName;
    private String catName;
    private String relName;
    private String locName;
    private String traderName;
    private Stock stock;
    private String unitName;
    private String weightUnitName;
}
