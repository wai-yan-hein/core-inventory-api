/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * @author wai yan
 */
@Data
@Embeddable
@Builder
public class SaleDetailKey {

    private String compCode;
    private Integer uniqueId;
    private String vouNo;
}
