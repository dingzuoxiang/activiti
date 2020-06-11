package com.activiti.controller;

import com.activiti.model.Depart;
import com.activiti.model.Group;
import com.activiti.model.PageInfo;
import com.activiti.model.User;
import com.activiti.service.DepartService;
import com.activiti.service.GroupService;
import com.activiti.service.UserService;
import com.activiti.util.DateJsonValueProcessor;
import com.activiti.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 用户管理
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private GroupService groupService;

    @Resource
    private DepartService departService;
    /**
     *
     * 登入
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/userLogin")
    public String userLogin(HttpServletResponse response, HttpServletRequest request) throws Exception{
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("username", request.getParameter("userName"));
        map.put("password", request.getParameter("password"));
        String role = request.getParameter("groupId");
        Group group = groupService.findById(new Integer(role));
        map.put("role",group.getName());
        User user = userService.userLogin(map);
        System.out.println(user);
        JSONObject result=new JSONObject();
        if(user==null){
            result.put("success", false);
            result.put("errorInfo", "用户名或者密码错误！");
        }else{
            Depart depart = departService.findById(user.getDept_parentId());
            if (depart != null) {
                user.setParentName(depart.getName());
            }
            result.put("success", true);
            request.getSession().setAttribute("currentMemberShip", user);
        }
        ResponseUtil.write(response, result);
        return null;
    }

    /**
     * 分页查询用户
     * @return
     * @throws Exception
     */
    @RequestMapping("/userPage")
    public String userPage(HttpServletResponse response,
                           @RequestParam(value = "page", required = false) String page,
                           @RequestParam(value = "rows", required = false) String rows,
                           User user) throws Exception{
        Map<String,Object> userMap=new HashMap<String, Object>();
        userMap.put("username", user.getUserName());

        PageInfo<User> userPage = new PageInfo<User>();
        Integer pageSize=Integer.parseInt(rows);
        userPage.setPageSize(pageSize);

        // 第几页
        String pageIndex = page;
        if (pageIndex == null || pageIndex == "") {
            pageIndex = "1";
        }
        userPage.setPageIndex((Integer.parseInt(pageIndex) - 1)
                * pageSize);
        // 取得总页数
        int userCount=userService.userCount(userMap);
        userPage.setCount(userCount);
        userMap.put("pageIndex", userPage.getPageIndex());
        userMap.put("pageSize", userPage.getPageSize());

        List<User> cusDevPlanList = userService.userPage(userMap);
        JSONObject json = new JSONObject();
        // 把List格式转换成JSON
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray jsonArray = JSONArray.fromObject(cusDevPlanList,jsonConfig);
        json.put("rows", jsonArray);
        json.put("total", userCount);
        ResponseUtil.write(response, json);
        return null;
    }
    /**
     * 修改用户
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateUser")
    public String updateUser(HttpServletResponse response,User user) throws Exception{
        Map<String,Object> map = new HashMap<String, Object>();
        Group group = groupService.findById(new Integer(user.getGroup()));
        user.setGroup(group.getName());
        map.put("role",group.getName());
        if(user.getDeptId() > 0){
            map.put("deptId",user.getDeptId());
        }else{
            user.setDeptId(user.getDept_parentId());
            map.put("deptId",user.getDept_parentId());
        }
        if(group.getName().equals("职员") || group.getName().equals("财务")){
            map.put("name",user.getUserName());
        }
        boolean exist = existUserName(map);
        JSONObject json=new JSONObject();
        if(!exist){
            setUser(user);
            int result=userService.updateUser(user);
            if(result>0){
                Map<String,Object> map1 = new HashMap<String, Object>();
                if(user.getGroup().equals("组长")){
                    map1.put("role","职员");
                    map1.put("deptId",user.getDeptId());
                    List<User> users = userService.getUserByRoleAndDeptId(map1);
                    for (User u:users){
                        u.setLeaderId(user.getId());
                        int effected = userService.updateUser(u);
                    }
                }
                if(user.getGroup().equals("经理")){
                    map1.put("parent_id",user.getDeptId());
                    List<Depart> departs = departService.getDepartByParentId(map1);
                    List<Integer> deptIds = new ArrayList<Integer>();
                    for (Depart depart:departs){
                        deptIds.add(depart.getId());
                    }
                    List<User> users = userService.getUserByDeptIds(deptIds);
                    for (User u:users){
                        u.setLeaderId(user.getId());
                        int effected = userService.updateUser(u);
                    }
                }
                json.put("success", true);
            }else{
                json.put("success", false);
            }
        }else {
            json.put("exist",true);
        }
        ResponseUtil.write(response, json);
        return null;
    }

    @RequestMapping("/modifyPassword")
    public String modifyPassword(HttpServletResponse response,HttpServletRequest request,String id) throws Exception{
        Map<String,Object> userMap=new HashMap<String, Object>();
        userMap.put("id", id);
        userMap.put("password",request.getParameter("newPassword"));
        int result = userService.modifyPassword(userMap);
        JSONObject json=new JSONObject();
        if(result>0){
            json.put("success", true);
        }else{
            json.put("success", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }
    /**
     * 批量刪除用戶
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteUser")
    public String deleteUser(HttpServletResponse response,HttpServletRequest request) throws Exception{
        String id=request.getParameter("ids");
        JSONObject json=new JSONObject();
        List<Integer> list=new ArrayList<Integer>();
        String[] strs = id.split(",");
        for (String str : strs) {
            list.add(new Integer(str));
        }
        try {
            int userResult=userService.deleteUser(list);
            if(userResult>0){
                json.put("success", true);
            }else{
                json.put("success", false);
            }
        } catch (Exception e) {
            json.put("success", false);
            e.printStackTrace();
        }
        ResponseUtil.write(response, json);
        return null;
    }

    /**
     * 新增用戶
     * @return
     * @throws Exception
     */
    @RequestMapping("/userSave")
    public String userSave(HttpServletResponse response,User user) throws Exception{
        System.out.println(user.getId()+user.getFirstName()+user.getLastName()+user.getPassword()+user.getEmail());
        Map<String ,Object> map = new HashMap<String, Object>();
        int role = new Integer(user.getGroup());
        Group group = groupService.findById(role);
        map.put("role",group.getName());
        user.setGroup(group.getName());
        if(user.getDeptId() > 0){
            map.put("deptId",user.getDeptId());
        }else{
            user.setDeptId(user.getDept_parentId());
            map.put("deptId",user.getDept_parentId());
        }
        if(group.getName().equals("职员") || group.getName().equals("财务")){
            map.put("name",user.getUserName());
        }
        boolean exist = existUserName(map);
        JSONObject json=new JSONObject();
        if(!exist){
            setUser(user);
            user.setCreateTime(new Date());
            int userResult=userService.addUser(user);
            if(userResult>0){
                Map<String,Object> m = new HashMap<String,Object>();
                m.put("userName",user.getUserName());
                m.put("role",user.getGroup());
                m.put("deptId",user.getDeptId());
                User us = userService.getUserByRoleDeptId(m);
                System.out.println(us);
                Map<String,Object> map1 = new HashMap<String, Object>();
                if(user.getGroup().equals("组长")){
                    map1.put("role","职员");
                    map1.put("deptId",user.getDeptId());
                    List<User> users = userService.getUserByRoleAndDeptId(map1);
                    for (User u:users){
                        u.setLeaderId(us.getId());
                        int effected = userService.updateUser(u);
                    }
                }
                if(user.getGroup().equals("经理")){
                    map1.put("parent_id",user.getDeptId());
                    List<Depart> departs = departService.getDepartByParentId(map1);
                    List<Integer> deptIds = new ArrayList<Integer>();
                    for (Depart depart:departs){
                        deptIds.add(depart.getId());
                    }
                    List<User> users = userService.getUserByDeptIds(deptIds);
                    for (User u:users){
                        u.setLeaderId(user.getId());
                        int effected = userService.updateUser(u);
                    }
                }
                json.put("success", true);
            }else{
                json.put("success", false);
            }
        }else {
            json.put("exist",true);
        }
        ResponseUtil.write(response, json);
        return null;
    }

    private void setUser(User user){
        Map<String,Object> map = new HashMap<String, Object>();
        if(user.getGroup().equals("职员")){
            map.put("role","组长");
            map.put("deptId",user.getDeptId());
        }
        if (user.getGroup().equals("组长")){
            map.put("role","经理");
            map.put("deptId",user.getDept_parentId());
        }
        User user1 = userService.getUserByRoleDeptId(map);
        if(user1 != null){
            user.setLeaderId(user1.getId());
        }
    }

    private boolean existUserName(Map<String,Object> map) throws Exception{
        User user = userService.getUserByRoleDeptId(map);
        if(user == null){
            return false;
        }else{
            return true;
        }
    }

    @RequestMapping("/logout")
    public void logout(HttpServletResponse response,HttpServletRequest request) throws Exception{
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect(request.getContextPath()+"/index.jsp");
    }
}
