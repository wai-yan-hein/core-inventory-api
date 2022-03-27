/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.controller;

import cv.api.common.ReturnObject;
import cv.api.inv.entity.*;
import cv.api.inv.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author wai yan
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
    @Autowired
    private PrivilegeService privilegeService;


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
    public ResponseEntity<ReturnObject> getRoleProperty(@RequestParam String roleCode) {
        List<RoleProperty> roleProperty = rolePropertyService.getRoleProperty(roleCode);
        ro.setList(Arrays.asList(roleProperty.toArray()));
        return ResponseEntity.ok(ro);
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
            ro.setData(property);
            ro.setMessage("Saved.");
        }
        return ResponseEntity.ok(ro);
    }

    @RequestMapping(path = "/get-role", method = RequestMethod.GET)
    public ResponseEntity<List<UserRole>> getRoles(@RequestParam String compCode) {
        List<UserRole> search = usrService.search(compCode);
        return ResponseEntity.ok(search);
    }

    @PostMapping(path = "/save-role")
    public ResponseEntity<UserRole> saveRole(@RequestBody UserRole ur) {
        UserRole save = usrService.save(ur);
        return ResponseEntity.ok(save);
    }


    @PostMapping(path = "/delete-role-property")
    public ResponseEntity<ReturnObject> deleteRoleProperty(@RequestBody RoleProperty p) {
        rolePropertyService.delete(p);
        ro.setMessage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @PostMapping(path = "/save-privilege")
    public ResponseEntity<ReturnObject> savePrivilege(@RequestBody Privilege p) {
        privilegeService.save(p);
        ro.setMessage("Saved.");
        ro.setData(p);
        return ResponseEntity.ok(ro);
    }

}
