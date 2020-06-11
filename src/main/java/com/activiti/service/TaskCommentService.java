package com.activiti.service;

import net.sf.json.JSONArray;

public interface TaskCommentService {

    public JSONArray getHistoryTaskByAssigned(String userName,String group);

    public JSONArray getCommentByProcessInstanceId(String processInstanceId);

    public JSONArray getRunTaskByAssigned(String userName,String group);
}
