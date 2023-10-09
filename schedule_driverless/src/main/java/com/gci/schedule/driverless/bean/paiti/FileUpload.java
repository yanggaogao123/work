package com.gci.schedule.driverless.bean.paiti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public class FileUpload {
    @JsonIgnore
    private MultipartFile file;

    private Date checkMonth;

    private String id ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Date getCheckMonth() {
        return checkMonth;
    }

    public void setCheckMonth(Date checkMonth) {
        this.checkMonth = checkMonth;
    }
}
