package com.activiti.model;

public class Depart {
    private int id;
    private String name;
    private int parent_id;
    private String parent_name;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getParent_id(){
        return parent_id;
    }
    public void setParent_id(int parent_id){
        this.parent_id = parent_id;
    }
    public String getParent_name(){
        return parent_name;
    }
    public void setParent_name(String parent_name){
        this.parent_name = parent_name;
    }
}
