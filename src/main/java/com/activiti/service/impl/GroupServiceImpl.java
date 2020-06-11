package com.activiti.service.impl;

import com.activiti.dao.GroupDao;
import com.activiti.model.Group;
import com.activiti.service.GroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("groupService")
public class GroupServiceImpl implements GroupService {
    @Resource
    private GroupDao groupDao;

    @Override
    public Group findById(int id) {
        return groupDao.findById(id);
    }

    /**
     * 查询所有角色填充下拉框
     * @return List<Group>
     */
    @Override
    public List<Group> findGroup() {
        return groupDao.findGroup();
    }

    /**
     * 分页查询
     * @param map Map<String, Object>
     * @return List<Group>
     */
    @Override
    public List<Group> groupPage(Map<String, Object> map) {
        return groupDao.groupPage(map);
    }

    /**
     * 统计数量
     * @param map Map<String, Object>
     * @return int
     */
    @Override
    public int groupCount(Map<String, Object> map) {
        return groupDao.groupCount(map);
    }

    @Override
    public Group findByName(String name) {
        return groupDao.findByName(name);
    }

    @Override
    public Group findByAbb(String abb) {
        return groupDao.findByAbb(abb);
    }

    @Override
    public int updateGroup(Group group) {
        return groupDao.updateGroup(group);
    }

    @Override
    public int addGroup(Group group) {
        return groupDao.addGroup(group);
    }

    @Override
    public int deleteGroup(List<Integer> ids) {
        return groupDao.deleteGroup(ids);
    }
}
