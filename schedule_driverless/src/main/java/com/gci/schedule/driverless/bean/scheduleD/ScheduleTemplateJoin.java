package com.gci.schedule.driverless.bean.scheduleD;

//线路模板结合
public class ScheduleTemplateJoin {

    private Integer templateId;

    private String templateName;

    private String templateNameFull;

    private String applyDayJoin;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateNameFull() {
        return templateNameFull;
    }

    public void setTemplateNameFull(String templateNameFull) {
        this.templateNameFull = templateNameFull;
    }

    public String getApplyDayJoin() {
        return applyDayJoin;
    }

    public void setApplyDayJoin(String applyDayJoin) {
        this.applyDayJoin = applyDayJoin;
    }
}