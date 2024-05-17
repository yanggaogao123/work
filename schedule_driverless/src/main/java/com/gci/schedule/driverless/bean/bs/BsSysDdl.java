package com.gci.schedule.driverless.bean.bs;

public class BsSysDdl {
    private Long sysDdlId;

    private Long version;

    private String description;

    private String displayValue;

    private String keyWord;

    private String remark;

    private String spareValue;

    private String sysType;

    private String sysValue;

    public Long getSysDdlId() {
        return sysDdlId;
    }

    public void setSysDdlId(Long sysDdlId) {
        this.sysDdlId = sysDdlId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue == null ? null : displayValue.trim();
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord == null ? null : keyWord.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getSpareValue() {
        return spareValue;
    }

    public void setSpareValue(String spareValue) {
        this.spareValue = spareValue == null ? null : spareValue.trim();
    }

    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType == null ? null : sysType.trim();
    }

    public String getSysValue() {
        return sysValue;
    }

    public void setSysValue(String sysValue) {
        this.sysValue = sysValue == null ? null : sysValue.trim();
    }
}