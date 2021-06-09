/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_role_menu")
public class VRoleMenu implements java.io.Serializable {

    private VRoleMenuKey key;
    private String menuClass;
    private String menuName;
    private String menuNameMM;
    private String menuUrl;
    private String parent;
    private String menuType;
    private Integer orderBy;
    private String soureAccCode;
    private Boolean isAllow;
    private List<VRoleMenu> child;

    public VRoleMenu() {
    }

    public VRoleMenu(String menuClass, String menuName, Boolean isAllow, List<VRoleMenu> child) {
        this.menuClass = menuClass;
        this.menuName = menuName;
        this.isAllow = isAllow;
        this.child = child;
        if (this.child == null) {
            this.child = Collections.emptyList();
        }
    }

    @EmbeddedId
    public VRoleMenuKey getKey() {
        return key;
    }

    public void setKey(VRoleMenuKey key) {
        this.key = key;
    }

    @Column(name = "menu_class")
    public String getMenuClass() {
        return menuClass;
    }

    public void setMenuClass(String menuClass) {
        this.menuClass = menuClass;
    }

    @Column(name = "menu_name")
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Column(name = "menu_name_mm")
    public String getMenuNameMM() {
        return menuNameMM;
    }

    public void setMenuNameMM(String menuNameMM) {
        this.menuNameMM = menuNameMM;
    }

    @Column(name = "menu_url")
    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    @Column(name = "parent_menu_id")
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Column(name = "menu_type")
    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    @Transient
    public List<VRoleMenu> getChild() {
        return child;
    }

    

    public void setChild(List<VRoleMenu> child) {
        this.child = child;
    }

    @Column(name = "order_by")
    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return menuName;
    }

    @Column(name = "source_acc_code")
    public String getSoureAccCode() {
        return soureAccCode;
    }

    public void setSoureAccCode(String soureAccCode) {
        this.soureAccCode = soureAccCode;
    }

    @Column(name = "allow")
    public Boolean getIsAllow() {
        return isAllow;
    }

    public void setIsAllow(Boolean isAllow) {
        this.isAllow = isAllow;
    }

    

}
