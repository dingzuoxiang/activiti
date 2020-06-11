package com.activiti.service.impl;

import com.activiti.dao.DepartDao;
import com.activiti.model.Depart;
import com.activiti.service.DepartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("departService")
public class DepartServiceImpl implements DepartService {

    @Resource
    private DepartDao departDao;

    @Override
    public List<Depart> departPage(Map<String, Object> map) {
        return departDao.departPage(map);
    }

    @Override
    public int departCount(Map<String, Object> map) {
        return departDao.departCount(map);
    }

    @Override
    public int addDepart(Depart depart) {
        return departDao.addDepart(depart);
    }

    @Override
    public Depart findById(int id) {
        return departDao.findById(id);
    }

    @Override
    public List<Depart> getDepartByParentId(Map<String,Object> map) {
        return departDao.getDepartByParentId(map);
    }

    @Override
    public List<Depart> getGroupByParentId(List<Integer> parentIds) {
        return departDao.getGroupByParentId(parentIds);
    }

    @Override
    public int updateDepart(Depart depart) {
        return departDao.updateDepart(depart);
    }

    @Override
    public List<Depart> getDepart() {
        return departDao.getDepart();
    }

    @Override
    public List<Depart> getGroup() {
        return departDao.getGroup();
    }

    @Override
    public int deleteDepart(List<Integer> ids) {
        return departDao.deleteDepart(ids);
    }

    @Override
    public Depart getDepartByName(String name) {
        return departDao.getDepartByName(name);
    }
}
