/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.config;

import com.cv.inv.api.common.Util1;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 *
 * @author Lenovo
 */
public class ActiveMqCondition implements Condition {

    @Override
    public boolean matches(ConditionContext cc, AnnotatedTypeMetadata atm) {
        String useActiveMq = cc.getEnvironment().getProperty("use.activemq");
        return Util1.getBoolean(useActiveMq);
    }
}
