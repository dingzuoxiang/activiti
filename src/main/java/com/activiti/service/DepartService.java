package com.activiti.service;

import com.activiti.model.Depart;

import java.util.List;
import java.util.Map;

public interface DepartService {
    public List<Depart> departPage(Map<String , Object> map);

    public int departCount(Map<String ,Object> map);

    public int addDepart(Depart depart);

    public Depart findById(int id);

    public List<Depart> getDepartByParentId(Map<String,Object> map);

    public List<Depart> getGroupByParentId(List<Integer> parentIds);

    public int updateDepart(Depart depart);

    public List<Depart> getDepart();

    public List<Depart> getGroup();

    public int deleteDepart(List<Integer> ids);

    public Depart getDepartByName(String name);
}
