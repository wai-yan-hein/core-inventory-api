/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Thandar
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "gender")
@Data
public class Gender implements java.io.Serializable {
    @Id
    @Column(name = "gender_id")
    private String genderId;
    @Column(name = "description")
    private String description;
}
