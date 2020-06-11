package com.activiti.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.activiti.model.Group;
import com.activiti.model.PageInfo;
import com.activiti.model.User;
import com.activiti.service.GroupService;
import com.activiti.util.ResponseUtil;
/**
 * 角色管理
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/group")
public class GroupController {
    @Resource
    private GroupService groupService;
    /**
     * 填充角色下拉框
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/findGroup")
    public String findGroup(HttpServletResponse response) throws Exception{
        List<Group> list=groupService.findGroup();

        JSONArray jsonArray=new JSONArray();

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("trueName", "请选择...");
        //转为JSON格式的数据
        jsonArray.add(jsonObject);
        //将list转为JSON
        JSONArray rows=JSONArray.fromObject(list);
        jsonArray.addAll(rows);
        ResponseUtil.write(response, jsonArray);
        return null;
    }

    /**
     * 分页查询用户
     * @return
     * @throws Exception
     */
    @RequestMapping("/groupPage")
    public String groupPage(HttpServletResponse response,
                            @RequestParam(value = "page", required = false) String page,
                            @RequestParam(value = "rows", required = false) String rows,
                            User user) throws Exception{
        Map<String,Object> groupMap=new HashMap<String, Object>();
        groupMap.put("id", user.getId());

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
        int userCount=groupService.groupCount(groupMap);
        userPage.setCount(userCount);
        groupMap.put("pageIndex", userPage.getPageIndex());
        groupMap.put("pageSize", userPage.getPageSize());

        List<Group> cusDevPlanList = groupService.groupPage(groupMap);
        JSONObject json = new JSONObject();
        // 把List格式转换成JSON
        JSONArray jsonArray = JSONArray.fromObject(cusDevPlanList);
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
    @RequestMapping("/updateGroup")
    public String updateGroup(HttpServletResponse response,Group group) throws Exception{
        int result=groupService.updateGroup(group);
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
    @RequestMapping("/deleteGroup")
    public String deleteGroup(HttpServletResponse response,HttpServletRequest request) throws Exception{
        String id=request.getParameter("ids");
        JSONObject json=new JSONObject();
        List<Integer> list=new ArrayList<Integer>();
        String[] strs = id.split(",");
        for (String str : strs) {
            list.add(new Integer(str));
        }
        try {
            int userResult=groupService.deleteGroup(list);
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
    @RequestMapping("/groupSave")
    public String groupSave(HttpServletResponse response,Group group) throws Exception{
        int userResult=groupService.addGroup(group);
        JSONObject json=new JSONObject();
        if(userResult>0){
            json.put("success", true);
        }else{
            json.put("success", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }

    @RequestMapping("/listAllGroups")
    public String listAllGroups(HttpServletResponse response) throws Exception{
        List<Group> list=groupService.findGroup();
        List<Group> groups = new ArrayList<Group>();
        for(Group g:list){
            if(!g.getName().equals("管理员")){
                groups.add(g);
            }
        }
        JSONArray jsonArray=JSONArray.fromObject(groups);
        ResponseUtil.write(response, jsonArray);
        return null;
    }

    @RequestMapping("existGroupAbbName")
    public String existGroupAbbName(HttpServletResponse response,String abb,String name) throws Exception{
        Group group = groupService.findByAbb(abb);
        Group group1 = groupService.findByName(name);
        JSONObject json=new JSONObject();
        if(group != null || group1 != null){
            json.put("exist", true);
        }else {
            json.put("exist", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }

    @RequestMapping("existGroupName")
    public String existGroupName(HttpServletResponse response,String name) throws Exception{
        Group group = groupService.findByName(name);
        JSONObject json=new JSONObject();
        if(group != null){
            json.put("exist", true);
        }else {
            json.put("exist", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }
}
