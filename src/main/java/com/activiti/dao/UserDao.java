package com.activiti.dao;

import com.activiti.model.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    public User findById(int id);

    public User findByUserName(String userName);
    /**
     * 登入
     *
     * @return
     */
    public User userLogin(Map<String ,Object> map);

    /**'
     * 分页查询用户
     * @param map
     * @return
     */
    public List<User> userPage(Map<String, Object> map);

    public User getUserByRoleDeptId(Map<String ,Object> map);

    public List<User> getUserByRoleAndDeptId(Map<String,Object> map);

    public List<User> getUserByDeptIds(List<Integer> deptIds);

    public int userCount(Map<String, Object> map);

    /**
     * 批量删除用户
     * @param id
     * @return
     */
    public int deleteUser(List<Integer> id);

    /**
     * 修改用户
     * @param user
     * @return
     */
    public int updateUser(User user);

    /**
     * 新增用户
     * @param user
     * @return
     */
    public int addUser(User user);

    public int modifyPassword(Map<String, Object> map);
}
