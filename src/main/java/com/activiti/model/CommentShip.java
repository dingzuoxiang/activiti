package com.activiti.model;

import java.util.Date;

public class CommentShip {
    private String userId;
    private String message;
    private Date time;

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public Date getTime(){
        return time;
    }
    public void setTime(Date time){
        this.time = time;
    }
}
