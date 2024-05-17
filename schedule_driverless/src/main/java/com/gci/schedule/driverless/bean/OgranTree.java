package com.gci.schedule.driverless.bean;

import java.util.List;

//机构树
public class OgranTree {
    private Long id;
    private String text;
    private List<OgranTree> children;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<OgranTree> getChildren() {
        return children;
    }

    public void setChildren(List<OgranTree> children) {
        this.children = children;
    }


}
