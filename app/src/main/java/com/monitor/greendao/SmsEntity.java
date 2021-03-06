package com.monitor.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SMS_ENTITY".
 */
public class SmsEntity {

    private String id;
    private String address;
    private String person;
    private Long date;
    /** Not-null value. */
    private String body;
    private Integer type;
    private Integer retryCount;

    public SmsEntity() {
    }

    public SmsEntity(Long date) {
        this.date = date;
    }

    public SmsEntity(String id, String address, String person, Long date, String body, Integer type, Integer retryCount) {
        this.id = id;
        this.address = address;
        this.person = person;
        this.date = date;
        this.body = body;
        this.type = type;
        this.retryCount = retryCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    /** Not-null value. */
    public String getBody() {
        return body;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setBody(String body) {
        this.body = body;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

}
