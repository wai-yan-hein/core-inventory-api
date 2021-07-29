/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.common;

import com.cv.inv.api.entity.CompoundKey;
import com.cv.inv.api.entity.VouId;
import com.cv.inv.api.service.VouIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author lenovo
 */
public class GenVouNoImpl implements GenVouNo {

    static Logger log = LoggerFactory.getLogger(GenVouNoImpl.class.getName());
    private final String vouType;
    private String period;
    private String vouNo;
    private int lastVouNo;
    private String machineNo;
    private final int vouTotalDigit = 5;
    private final int machineTtlDigit = 2;
    private boolean isError = false;
    private final VouIdService vouIdService;

    public GenVouNoImpl(VouIdService vouIdService, String vouType, String period, String machineNo) {
        this.vouIdService = vouIdService;
        this.vouType = vouType;
        this.period = period;
        this.machineNo = machineNo;
        getLastVouNo();
    }

    @Override
    public String genVouNo() {
        generateVouNo();
        return vouNo;
    }

    @Override
    public void updateVouNo() {
        lastVouNo += 1;
        try {

            VouId vouId = (VouId) vouIdService.find(new CompoundKey(machineNo, vouType, period));
            vouId.setVouNo(lastVouNo);
            vouIdService.save(vouId);
        } catch (Exception ex) {
            isError = true;
        }
    }

    private void getLastVouNo() {
        try {
            Object objVouNo = vouIdService.getMax(machineNo, vouType, period);
            if (objVouNo == null) {
                //Need to insert new
                lastVouNo = 1;
                VouId vouId = new VouId(new CompoundKey(machineNo, vouType, period), lastVouNo);
                vouIdService.save(vouId);
            } else {
                lastVouNo = Integer.parseInt(objVouNo.toString());
            }

        } catch (Exception ex) {
            log.error("getLastVouNo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            isError = true;
        }
    }

    private void generateVouNo() {
        //machineNo+lastVouNo+period
        if (isError) {
            vouNo = "-";
            return;
        }
        if (machineNo.length() < machineTtlDigit) {
            int needToAdd = machineTtlDigit - machineNo.length();

            for (int i = 0; i < needToAdd; i++) {
                machineNo = "0" + machineNo;
            }
        }

        String strVouNo = Integer.toString(lastVouNo);
        if (strVouNo.length() < vouTotalDigit) {
            int needToAdd = vouTotalDigit - strVouNo.length();

            for (int i = 0; i < needToAdd; i++) {
                strVouNo = "0" + strVouNo;
            }
        }

        vouNo = machineNo + strVouNo + period;
    }

    public void setPeriod(String period) {
        this.period = period;
        getLastVouNo();
    }

}
