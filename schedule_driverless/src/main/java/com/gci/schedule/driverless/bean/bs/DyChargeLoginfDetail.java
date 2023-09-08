package com.gci.schedule.driverless.bean.bs;

import java.util.Date;

public class DyChargeLoginfDetail{
    private Long id;

    private Long chargeLoginfId;

    private String remark;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChargeLoginfId() {
        return chargeLoginfId;
    }

    public void setChargeLoginfId(Long chargeLoginfId) {
        this.chargeLoginfId = chargeLoginfId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}