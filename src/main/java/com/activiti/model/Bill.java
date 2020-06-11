package com.activiti.model;

import java.util.Date;

/**
 * 帐单实体
 * @author user
 *
 */
public class Bill {
    private Integer id; // 编号
    private User user; // 账单申请人
    private Date billDate;// 申请日期
    private Integer billMoney; // 申请金额
    private String billDetail; // 账单用处
    private String state; // 审核状态  未提交  审核中 审核通过 审核未通过
    private String processInstanceId; // 流程实例Id
    private String taskId;
    private String userName;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Date getBillDate() {
        return billDate;
    }
    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }
    public Integer getBillMoney() {
        return billMoney;
    }
    public void setBillMoney(Integer billMoney) {
        this.billMoney = billMoney;
    }
    public String getBillDetail() {
        return billDetail;
    }
    public void setBillDetail(String billDetail) {
        this.billDetail = billDetail;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    public String getTaskId(){
        return taskId;
    }
    public void setTaskId(String taskId){
        this.taskId = taskId;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
}
