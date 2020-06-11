package com.activiti.dao;

import com.activiti.model.Group;

import java.util.List;
import java.util.Map;

public interface GroupDao {
    /**
     * 查询所有角色填充下拉框
     * @return
     */
    public List<Group> findGroup();

    /**
     * 分页查询
     * @param map
     * @return
     */
    public List<Group> groupPage(Map<String,Object> map);
    /**
     * 统计数量
     * @param map
     * @return
     */
    public int groupCount(Map<String,Object> map);

    public Group findById(int id);

    public Group findByName(String name);

    public Group findByAbb(String abb);

    public int updateGroup(Group group);

    public int addGroup(Group group);

    public int deleteGroup(List<Integer> ids);
}
