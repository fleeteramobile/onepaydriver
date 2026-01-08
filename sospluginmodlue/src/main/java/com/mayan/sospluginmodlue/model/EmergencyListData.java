package com.mayan.sospluginmodlue.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EmergencyListData {

    private String message;
    private ArrayList<ContactsData> detail = null;
    private Integer status;
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ContactsData> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<ContactsData> detail) {
        this.detail = detail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}