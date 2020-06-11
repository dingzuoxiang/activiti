package com.activiti.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.activiti.service.UserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.activiti.model.Bill;
import com.activiti.model.PageInfo;
import com.activiti.model.User;
import com.activiti.service.BillService;
import com.activiti.util.DateJsonValueProcessor;
import com.activiti.util.ResponseUtil;

/**
 * 业务处理
 *
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/bill")
public class BillController {
    @Resource(name = "billService" ,type = com.activiti.service.impl.BillServiceImpl.class)
    private BillService billService;
    @Resource
    private UserService userService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    /**
     * 分页查询业务
     * @param response
     * @param rows
     * @param page
     * @param username
     * @return
     * @throws Exception
     */
    @RequestMapping("/billPage")
    public String billPage(HttpServletResponse response, String rows,
                            String page, String username,String s_name) throws Exception {
        if(s_name==null){
            s_name="";
        }else {
            username = s_name;
        }
        PageInfo<Bill> leavePage = new PageInfo<Bill>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        Integer pageSize = Integer.parseInt(rows);
        leavePage.setPageSize(pageSize);
        if (page == null || page.equals("")) {
            page = "1";
        }
        leavePage.setPageIndex((Integer.parseInt(page) - 1) * pageSize);
        map.put("pageIndex", leavePage.getPageIndex());
        map.put("pageSize", leavePage.getPageSize());
        int leaveCount = billService.billCount(map);
        List<Bill> billsList = billService.billPage(map);
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONObject result=new JSONObject();
        JSONArray jsonArray=JSONArray.fromObject(billsList,jsonConfig);
        result.put("rows", jsonArray);
        result.put("total", leaveCount);
        ResponseUtil.write(response, result);
        return null;
    }

    /**
     * 添加账单
     * @param bill
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/save")
    public String save(Bill bill,HttpServletResponse response,String userName)throws Exception{
        System.out.println("这里面是什么鬼："+userName);
        User user=new User();
        user.setUserName(userName);
        int resultTotal=0;
        bill.setBillDate(new Date());
        //添加用户对象
        bill.setUser(user);
        resultTotal=billService.addBill(bill);
        JSONObject result=new JSONObject();
        if(resultTotal>0){
            result.put("success", true);
        }else{
            result.put("success", false);
        }
        ResponseUtil.write(response, result);
        return null;
    }
    /**
     * 提交账单流程申請
     * @return
     * @throws Exception
     */
    @RequestMapping("/startApply")
    public String startApply(HttpServletResponse response, int billId, HttpSession session) throws Exception{
        Map<String,Object> variables=new HashMap<String,Object>();
        variables.put("billId", billId);
        // 启动流程
        ProcessInstance pi= runtimeService.startProcessInstanceByKey("approvalProcess",variables);
        // 根据流程实例Id查询任务
        Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
        User currentMemberShip=(User) session.getAttribute("currentMemberShip");
        User user = userService.findById(currentMemberShip.getLeaderId());
        variables.put("user",user.getUserName());
        // 完成 员工填写账单申请
        taskService.complete(task.getId(),variables);
        Bill bill=billService.findById(billId);
        //修改状态
        bill.setState("审核中");
        bill.setProcessInstanceId(pi.getProcessInstanceId());
        // 修改请假单状态
        billService.updateBill(bill);
        JSONObject result=new JSONObject();
        result.put("success", true);
        ResponseUtil.write(response, result);
        return null;
    }
    /**
     * 查询流程信息
     * @param response
     * @param taskId  流程实例ID
     * @return
     * @throws Exception
     */
    @RequestMapping("/getBillByTaskId")
    public String getBillByTaskId(HttpServletResponse response,String taskId) throws Exception{
        //先根据流程ID查询
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        Bill bill=billService.getBillByTaskId(task.getProcessInstanceId());
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class,
                //时间格式转换
                new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONObject result=new JSONObject();
        result.put("bill", JSONObject.fromObject(bill,jsonConfig));
        ResponseUtil.write(response, result);
        return null;
    }
}
