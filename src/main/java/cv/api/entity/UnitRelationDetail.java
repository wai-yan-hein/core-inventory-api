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

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class UnitRelationDetail {

    private UnitRelationDetailKey key;
    private Double qty;
    private String unit;
    private Double smallestQty;
    private Integer deptId;
}
