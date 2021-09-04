/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Data
@Entity
@Table(name = "menu")
public class Menu implements java.io.Serializable {

    @Id
    @Column(name = "menu_code", unique = true, nullable = false)
    private String code; //menu_id
    @Column(name = "parent_menu_id")
    private String parent; //parent_menu_id
    @Column(name = "menu_name", length = 50)
    private String menuName; //menu_name
    @Column(name = "menu_name_mm", length = 255)
    private String menuNameMM;
    @Column(name = "menu_url", length = 255)
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
    @ManyToOne
    @JoinColumn(name = "updated_by")
    private AppUser updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @ManyToOne
    @JoinColumn(name = "created_by")
    private AppUser createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "comp_code")
    private String compCode;
    @Transient
    private List<Menu> child;
}
