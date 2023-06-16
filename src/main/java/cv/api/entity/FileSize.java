/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author SAI
 */
@Entity
@Table(name = "file_size")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileSize implements Serializable {
    @Id
    @Column(name = "queue")
    private String queue;
    @Column(name = "size")
    private Double fileSize;


}
