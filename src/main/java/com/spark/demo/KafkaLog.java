package com.spark.demo;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KafkaLog {
    private String system;
    private String method;
    private String pin;
    private String channel;
    private String businessKey;
    private String time;
    private String host;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static void main(String[] args) {
        KafkaLog log=new KafkaLog();
        log.setSystem("基础服务_basesoa");
        log.setMethod("查券_getQueryCoupon");
        log.setHost("127.0.0.1");
        log.setChannel("item");
        log.setBusinessKey("");
        log.setPin("asdfasdfasdf");
        log.setTime(String.valueOf(new Date().getTime()));
        System.out.println(JSON.toJSON(log));
    }
}
