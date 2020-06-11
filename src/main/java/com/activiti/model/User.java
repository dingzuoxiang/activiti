package com.activiti.model;

import java.util.Date;

/**
 * 用户扩展实体
 * @author user
 *
 */
public class User {

    private int id; // 主键 用户名
    private String userName;
    private String firstName;  // 姓
    private String lastName; // 名
    private String email; // 邮箱
    private String call;
    private String password; // 密码
    private String group;
    private int leaderId;
    private int deptId;
    private String roleAbb;
    private String deptName;
    private int dept_parentId;
    private String parentName;
    private Date createTime;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }
    public String getCall(){
        return call;
    }
    public void setCall(String call){
        this.call = call;
    }
    public int getLeaderId(){
        return leaderId;
    }
    public void setLeaderId(int leaderId){
        this.leaderId = leaderId;
    }
    public int getDeptId(){
        return deptId;
    }
    public void setDeptId(int deptId){
        this.deptId = deptId;
    }
    public Date getTime(){
        return createTime;
    }
    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    public String getDeptName(){
        return deptName;
    }
    public void setDeptName(String deptName){
        this.deptName = deptName;
    }
    public int getDept_parentId(){
        return dept_parentId;
    }
    public void setDept_parentId(int dept_parentId){
        this.dept_parentId = dept_parentId;
    }
    public String getRoleAbb(){
        return roleAbb;
    }
    public void setRoleAbb(String roleAbb){
        this.roleAbb = roleAbb;
    }
    public String getParentName(){
        return parentName;
    }
    public void setParentName(String parentName){
        this.parentName = parentName;
    }
}
