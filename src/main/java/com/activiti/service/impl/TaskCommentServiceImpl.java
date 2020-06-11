package com.activiti.service.impl;

import com.activiti.model.Bill;
import com.activiti.model.MyTask;
import com.activiti.service.BillService;
import com.activiti.service.TaskCommentService;
import com.activiti.util.DateJsonValueProcessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("taskCommentService")
public class TaskCommentServiceImpl implements TaskCommentService {

    // 引入activiti自带的Service接口
    @Resource
    private TaskService taskService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private FormService formService;

    @Resource
    private BillService billService;

    @Resource
    private HistoryService historyService;

    @Override
    public JSONArray getHistoryTaskByAssigned(String userName,String group) {
        List<HistoricTaskInstance> histList = new ArrayList<HistoricTaskInstance>();
        if(group.equals("caiwu")||group.equals("zjl")){
            histList=historyService.createHistoricTaskInstanceQuery()
                    .taskCandidateGroup(group)//根据角色名称查询
                    .list();
        }else {
            histList=historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userName)//根据角色名称查询
                    .list();
        }
        List<Bill> bills=new ArrayList<Bill>();
        for(HistoricTaskInstance t:histList){
            Bill bill = billService.getBillByTaskId(t.getProcessInstanceId());
            if(bill != null){
                bill.setTaskId(t.getId());
                bills.add(bill);
            }
        }
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray jsonArray=JSONArray.fromObject(bills,jsonConfig);
        return jsonArray;
    }

    @Override
    public JSONArray getCommentByProcessInstanceId(String processInstanceId) {
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
        Collections.reverse(comments);

        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray jsonArray=JSONArray.fromObject(comments,jsonConfig);
        return jsonArray;
    }

    @Override
    public JSONArray getRunTaskByAssigned(String userName,String group) {
        List<Task> taskList = new ArrayList<Task>();
        if(group.equals("caiwu")||group.equals("zjl")) {
            taskList = taskService.createTaskQuery()
                    .taskCandidateGroup(group)
                    .list();
        }else{
            taskList=taskService.createTaskQuery()
                .taskCandidateOrAssigned(userName)
                .list();
        }
        List<Bill> bills=new ArrayList<Bill>();
        for(Task t:taskList){
            Bill bill = billService.getBillByTaskId(t.getProcessInstanceId());
            if(bill != null){
                bill.setTaskId(t.getId());
                bills.add(bill);
            }
        }
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray jsonArray=JSONArray.fromObject(bills,jsonConfig);
        return jsonArray;
    }
}
