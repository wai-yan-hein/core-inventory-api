/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.common.RoleDefault;
import com.cv.inv.api.entity.AppUser;
import com.cv.inv.api.entity.RoleProperty;
import com.cv.inv.api.entity.UserRole;
import com.cv.inv.api.entity.VUsrCompAssign;
import com.cv.inv.api.service.RolePropertyService;
import com.cv.inv.api.service.UserRoleService;
import com.cv.inv.api.service.UserService;
import com.cv.inv.api.service.UsrCompRoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author Lenovo
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private ReturnObject ro;
    @Autowired
    private UserService userService;
    @Autowired
    private UsrCompRoleService compService;
    @Autowired
    private RolePropertyService rolePropertyService;
    @Autowired
    private UserRoleService usrService;


    @PostMapping(value = "/save")
    public ResponseEntity<ReturnObject> saveUser(@RequestBody AppUser user) {
        userService.save(user);
        return ResponseEntity.ok(ro);
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ResponseEntity<AppUser> getUser(@RequestParam String username, @RequestParam String password) {
        AppUser user = userService.login(username, password);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(path = "/get-assign-company", method = RequestMethod.GET)
    public ResponseEntity<List<VUsrCompAssign>> getAssignCompany(@RequestParam String userCode) {
        List<VUsrCompAssign> listComp = compService.getAssignCompany(userCode);
        return ResponseEntity.ok(listComp);
    }

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public ResponseEntity<ReturnObject> test() {
        ro.setMessage("Hello");
        return ResponseEntity.ok(ro);
    }

    @RequestMapping(path = "/get-role-property", method = RequestMethod.GET)
    public ResponseEntity<List<RoleProperty>> getRoleProperty(@RequestParam String roleCode) {
        List<RoleProperty> roleProperty = rolePropertyService.getRoleProperty(roleCode);
        return ResponseEntity.ok(roleProperty);
    }

    @PostMapping(path = "/save-role-property")
    public ResponseEntity<ReturnObject> saveRoleProperty(@RequestBody RoleProperty rp) {
        log.info("/save-role-property");
        if (Objects.isNull(rp.getKey()) || Objects.isNull(rp.getKey().getPropKey())) {
            ro.setMessage("Invalid Property Key.");
        } else if (Objects.isNull(rp.getCompCode())) {
            ro.setMessage("Invalid Company Code");
        } else if (Objects.isNull(rp.getKey().getRoleCode())) {
            ro.setMessage("Invalid Role Code");
        } else {
            RoleProperty property = rolePropertyService.save(rp);
            ro.setObj(property);
            ro.setMessage("Saved.");
        }
        return ResponseEntity.ok(ro);
    }

    @RequestMapping(path = "/get-role", method = RequestMethod.GET)
    public ResponseEntity<List<UserRole>> getRoles(@RequestParam String compCode) {
        List<UserRole> search = usrService.search("-", compCode);
        return ResponseEntity.ok(search);
    }

    @PostMapping(path = "/save-role")
    public ResponseEntity<UserRole> saveRole(@RequestBody UserRole ur) {
        UserRole save = usrService.save(ur);
        return ResponseEntity.ok(save);
    }

    @RequestMapping(value = "/role-default", method = RequestMethod.GET)
    public ResponseEntity<RoleDefault> getRoleDefault(@RequestParam String roleCode) {
        RoleDefault roleDefault = rolePropertyService.getRoleDefault(roleCode);
        return ResponseEntity.ok(roleDefault);
    }
}
