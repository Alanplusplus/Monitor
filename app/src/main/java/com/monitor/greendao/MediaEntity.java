package com.monitor.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MEDIA_ENTITY".
 */
public class MediaEntity {

    private Long modifyTime;
    private String name;

    public MediaEntity() {
    }

    public MediaEntity(String name) {
        this.name = name;
    }

    public MediaEntity(Long modifyTime, String name) {
        this.modifyTime = modifyTime;
        this.name = name;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
