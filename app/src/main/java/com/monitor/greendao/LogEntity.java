package com.monitor.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "LOG_ENTITY".
 */
public class LogEntity {

    private Long time;
    private String message;

    public LogEntity() {
    }

    public LogEntity(Long time, String message) {
        this.time = time;
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
