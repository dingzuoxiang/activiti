package com.activiti.model;

/**
 * 角色实体扩展
 * @author user
 *
 */
public class Group {
    private int id; // 主键 角色名
    private String abb;
    private String name; // 名称

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAbb(){
        return abb;
    }
    public void setAbb(String abb){
        this.abb = abb;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
