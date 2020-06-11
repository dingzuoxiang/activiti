package com.activiti.service.impl;

import com.activiti.dao.UserDao;
import com.activiti.model.User;
import com.activiti.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public User findById(int id){
        return userDao.findById(id);
    }

    @Override
    public User findByUserName(String userName) {
        return userDao.findByUserName(userName);
    }

    /**
     * 登入
     * @return User
     */
    @Override
    public User userLogin(Map<String ,Object> map) {
        return userDao.userLogin(map);
    }

    /**'
     * 分页查询用户
     * @param map Map<String, Object>
     * @return List<User>
     */
    @Override
    public List<User> userPage(Map<String, Object> map) {
        return userDao.userPage(map);
    }

    @Override
    public User getUserByRoleDeptId(Map<String, Object> map) {
        return userDao.getUserByRoleDeptId(map);
    }

    @Override
    public List<User> getUserByRoleAndDeptId(Map<String, Object> map) {
        return userDao.getUserByRoleAndDeptId(map);
    }

    @Override
    public List<User> getUserByDeptIds(List<Integer> deptIds) {
        return userDao.getUserByDeptIds(deptIds);
    }

    @Override
    public int userCount(Map<String, Object> map) {
        return userDao.userCount(map);
    }

    /**
     * 批量删除用户
     * @param id List<String>
     * @return int
     */
    @Override
    public int deleteUser(List<Integer> id) {
        return userDao.deleteUser(id);
    }

    /**
     * 修改用户
     * @param user User
     * @return int
     */
    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user);
    }

    /**
     * 新增用户
     * @param user User
     * @return int
     */
    @Override
    public int addUser(User user) {
        return userDao.addUser(user);
    }

    @Override
    public int modifyPassword(Map<String, Object> map){
        return userDao.modifyPassword(map);
    }
}
