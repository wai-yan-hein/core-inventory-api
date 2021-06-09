/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.config;

import com.cv.inv.api.common.ReturnObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Lenovo
 */
@Configuration
public class AppConfig {

    @Bean
    public ReturnObject ro() {
        return new ReturnObject();
    }
}
