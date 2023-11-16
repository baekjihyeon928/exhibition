package com.exhibition.modules.major;

import lombok.Data;

@Data
public class MajorForm {

    private String majorName;

    public String getCollageName(){
        return majorName.substring(0,majorName.indexOf("/"));
    }

    public String getDepartmentName(){
        return majorName.substring(majorName.indexOf("/") + 1);
    }

    public Major getMajor(){
        return Major.builder().collage(this.getCollageName())
                .department(this.getDepartmentName()).build();
    }
}
