/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author winswe
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "menu")
public class Menu implements java.io.Serializable {

    @Id
    @Column(name = "menu_code", unique = true, nullable = false)
    private String code; //menu_id
    @Column(name = "parent_menu_id")
    private String parent; //parent_menu_id
    @Column(name = "menu_name")
    private String menuName; //menu_name
    @Column(name = "menu_name_mm")
    private String menuNameMM;
    @Column(name = "menu_url")
    private String menuUrl;
    @Column(name = "menu_type")
    private String menuType;
    @Column(name = "order_by")
    private Integer orderBy;
    @Column(name = "source_acc_code")
    private String sourceAccCode;
    @Column(name = "menu_class")
    private String menuClass;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "comp_code")
    private String compCode;
    @Transient
    private List<Menu> child;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Menu menu = (Menu) o;
        return code != null && Objects.equals(code, menu.code);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
