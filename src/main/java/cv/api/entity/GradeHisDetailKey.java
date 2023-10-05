/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cv.api.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author DELL
 */
@Data
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeHisDetailKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "comp_code")
    private String compCode;

}
