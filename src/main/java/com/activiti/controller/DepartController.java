package com.activiti.controller;

import com.activiti.model.Depart;
import com.activiti.model.Group;
import com.activiti.service.DepartService;
import com.activiti.service.GroupService;
import com.activiti.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/depart")
public class DepartController {
    @Resource
    private DepartService departService;

    @Resource
    private GroupService groupService;

    @RequestMapping("/getDepart")
    public String getDepart(HttpServletResponse response)throws Exception{
        List<Depart> departs = departService.getDepart();
        JSONArray jsonArray=new JSONArray();

        JSONArray rows=JSONArray.fromObject(departs);
        jsonArray.addAll(rows);
        ResponseUtil.write(response, jsonArray);
        return null;
    }

    @RequestMapping("/getDepartByGroupId")
    public String getDepartByGroupId(HttpServletResponse response,String groupId)throws Exception{
        Group group;
        List<Depart> departs = departService.getDepart();
        List<Depart> list = new ArrayList<Depart>();
        JSONArray jsonArray=new JSONArray();
        if(groupId != null){
            int id = new Integer(groupId);
            group = groupService.findById(id);
            for(Depart depart:departs){
                if(group.getName().equals("财务")){
                    if(depart.getName().equals("财务部")){
                        list.add(depart);
                    }
                }else if(group.getName().equals("总经理")){
                    if(depart.getName().equals("董事部")){
                        list.add(depart);
                    }
                }else {
                    if(!depart.getName().equals("财务部")&&!depart.getName().equals("董事部")){
                        list.add(depart);
                    }
                }
            }
            JSONArray rows=JSONArray.fromObject(list);
            jsonArray.addAll(rows);
        }else{
            JSONArray rows=JSONArray.fromObject(departs);
            jsonArray.addAll(rows);
        }
        ResponseUtil.write(response, jsonArray);
        return null;
    }

    @RequestMapping("/getDepartByName")
    public String getDepartByName(HttpServletResponse response, String name,String parentId)throws Exception{
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("parent_id",new Integer(parentId));
        List<Depart> depart = departService.getDepartByParentId(map);
        JSONObject json=new JSONObject();
        if(depart.size() > 0){
            json.put("exist", true);
        }else{
            json.put("exist", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }

    @RequestMapping("/getGroup")
    public String getGroup(HttpServletResponse response)throws Exception{
        List<Depart> departs = departService.getDepart();
        List<Integer> parentIds = new ArrayList<Integer>();
        for(Depart d:departs){
            parentIds.add(d.getId());
        }
        List<Depart> departList = departService.getGroupByParentId(parentIds);
        for(Depart depart:departList){
            Depart d = departService.findById(depart.getParent_id());
            depart.setParent_name(d.getName());
        }
        JSONArray jsonArray=new JSONArray();

        //将list转为JSON
        JSONArray rows=JSONArray.fromObject(departList);
        jsonArray.addAll(rows);
        ResponseUtil.write(response, jsonArray);
        return null;
    }

    @RequestMapping("/getDepartByParentId")
    public String getDepartByParentId(HttpServletResponse response,int parentId)throws Exception{
        Map<String ,Object> map = new HashMap<String, Object>();
        map.put("parent_id",parentId);
        List<Depart> departs = departService.getDepartByParentId(map);
        JSONArray jsonArray=new JSONArray();

        //将list转为JSON
        JSONArray rows=JSONArray.fromObject(departs);
        jsonArray.addAll(rows);
        ResponseUtil.write(response, jsonArray);
        return null;
    }

    @RequestMapping("departSave")
    public String addDepart(HttpServletResponse response,Depart depart)throws Exception{
        int result=departService.addDepart(depart);
        JSONObject json=new JSONObject();
        if(result>0){
            json.put("success", true);
        }else{
            json.put("success", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }

    @RequestMapping("/deleteDepart")
    public String deleteDepart(HttpServletResponse response, HttpServletRequest request) throws Exception{
        String id=request.getParameter("ids");
        JSONObject json=new JSONObject();
        List<Integer> list=new ArrayList<Integer>();
        String[] strs = id.split(",");
        for (String str : strs) {
            list.add(new Integer(str));
        }
        try {
            int userResult=departService.deleteDepart(list);
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

    @RequestMapping("/updateDepart")
    public String updateDepart(HttpServletResponse response,Depart depart) throws Exception{
        int result=departService.updateDepart(depart);
        JSONObject json=new JSONObject();
        if(result>0){
            json.put("success", true);
        }else{
            json.put("success", false);
        }
        ResponseUtil.write(response, json);
        return null;
    }

}
