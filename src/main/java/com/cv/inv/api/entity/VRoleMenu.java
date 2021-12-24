/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

/**
 * @author winswe
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "v_role_menu")
public class VRoleMenu implements java.io.Serializable {
    @EmbeddedId
    private VRoleMenuKey key;
    @Column(name = "menu_class")
    private String menuClass;
    @Column(name = "menu_name")
    private String menuName;
    @Column(name = "menu_name_mm")
    private String menuNameMM;
    @Column(name = "menu_url")
    private String menuUrl;
    @Column(name = "parent_menu_id")
    private String parent;
    @Column(name = "menu_type")
    private String menuType;
    @Column(name = "order_by")
    private Integer orderBy;
    @Column(name = "allow")
    private Boolean isAllow;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "api_url")
    private String apiUrl;
    @Transient
    private List<VRoleMenu> child;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VRoleMenu vRoleMenu = (VRoleMenu) o;
        return key != null && Objects.equals(key, vRoleMenu.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
