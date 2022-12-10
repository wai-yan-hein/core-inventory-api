/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class Gender implements java.io.Serializable {
    private String genderId;
    private String description;

    @Id
    @Column(name = "gender_id", unique = true, nullable = false, length = 2)
    public String getGenderId() {
        return genderId;
    }

    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    @Column(name = "description", unique = true, nullable = false,
            length = 10)
    public String getDescription() {
        return description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

    @Override
    public String toString() {
        return description;
    }
}
