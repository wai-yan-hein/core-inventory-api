/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.entity.AppUser;
import com.cv.inv.api.service.UserService;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private ReturnObject ro;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/save")
    public ResponseEntity<ReturnObject> saveUser(@RequestBody AppUser user)
            throws URISyntaxException {
        return ResponseEntity.ok(ro);
    }
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ResponseEntity<AppUser> getUser(@RequestParam String username, @RequestParam String password) {
        AppUser user = userService.login(username, password);
        return ResponseEntity.ok(user);
        // code here
    }
    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public ResponseEntity<ReturnObject> test() {
        ro.setMeesage("Hello");
        return ResponseEntity.ok(ro);
        // code here
    }
}
