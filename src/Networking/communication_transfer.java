/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class communication_transfer implements Serializable{
    private static final long serialVersionUID = 1L;
    private String From_Name;
    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public communication_transfer(String From_Name, Object object, String From_ID, String To_ID, String message, String purpose) {
        this.From_Name = From_Name;
        this.object = object;
        this.From_ID = From_ID;
        this.To_ID = To_ID;
        this.message = message;
        this.purpose = purpose;
    }

    public String getFrom_Name() {
        return From_Name;
    }

    public void setFrom_Name(String From_Name) {
        this.From_Name = From_Name;
    }
    private String From_ID;
    private String To_ID;
    private String message;

    public communication_transfer(String From_Name, String From_ID, String To_ID, String message, String purpose) {
        this.From_Name = From_Name;
        this.From_ID = From_ID;
        this.To_ID = To_ID;
        this.message = message;
        this.purpose = purpose;
    }
    private String purpose;

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public communication_transfer() {
    }

    public String getFrom_ID() {
        return From_ID;
    }

    public void setFrom_ID(String From_ID) {
        this.From_ID = From_ID;
    }

    public String getTo_ID() {
        return To_ID;
    }

    public void setTo_ID(String To_ID) {
        this.To_ID = To_ID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public communication_transfer(String From_ID, String To_ID, String message) {
        this.From_ID = From_ID;
        this.To_ID = To_ID;
        this.message = message;
    }
}
