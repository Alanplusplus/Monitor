package com.os.logger.track;

import android.provider.Telephony;
import android.telephony.SmsMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alan on 16/5/9.
 * _id => 短消息序号 如100
 thread_id => 对话的序号 如100
 address => 发件人地址，手机号.如+8613811810000
 person => 发件人，返回一个数字就是联系人列表里的序号，陌生人为null
 date => 日期  long型。如1256539465022
 protocol => 协议 0 SMS_RPOTO, 1 MMS_PROTO
 read => 是否阅读 0未读， 1已读
 status => 状态 -1接收，0 complete, 64 pending, 128 failed
 type => 类型 1是接收到的，2是已发出
 body => 短消息内容
 service_center => 短信服务中心号码编号。如+8613800755500
 */
public class SmsItem {
    private String id;
    private String address;
    private String person;
    private long date;
    private String body;
    private int type;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SmsItem{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", persion='" + person + '\'' +
                ", date=" + date +
                ", body='" + body + '\'' +
                ", type=" + type +
                '}';
    }
}
