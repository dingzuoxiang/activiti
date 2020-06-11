package com.activiti.util;


import com.activiti.model.MyTask;
import com.activiti.model.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.activiti.engine.task.Comment;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        MyTask user = new MyTask();
        user.setId("dzx");
        List<Comment> comments = new ArrayList<Comment>();
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray jsonArray=JSONArray.fromObject(user,jsonConfig);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        System.out.println(jsonArray.getJSONObject(0).get("id"));
    }
}
