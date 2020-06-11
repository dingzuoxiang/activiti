package com.activiti.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.activiti.service.GroupService;
import com.activiti.service.UserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.activiti.model.Group;
import com.activiti.model.Bill;
import com.activiti.model.MyTask;
import com.activiti.model.PageInfo;
import com.activiti.model.User;
import com.activiti.service.BillService;
import com.activiti.util.DateJsonValueProcessor;
import com.activiti.util.ResponseUtil;

/**
 * 历史流程批注管理
 *
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/task")
public class TaskController {
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
    private UserService userService;

    @Resource
    private GroupService groupService;

    @Resource
    private HistoryService historyService;


    /**
     * 查询历史流程批注
     *
     * @param response
     * @param processInstanceId
     *            流程ID
     * @return
     * @throws Exception
     */
    @RequestMapping("/listHistoryCommentWithProcessInstanceId")
    public String listHistoryCommentWithProcessInstanceId(
            HttpServletResponse response, String processInstanceId) throws Exception {
        if (processInstanceId == null) {
            return null;
        }
        List<Comment> commentList = taskService
                .getProcessInstanceComments(processInstanceId);
        // 改变顺序，按原顺序的反向顺序返回list
        Collections.reverse(commentList); //集合元素反转
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class,
                //时间格式转换
                new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(commentList, jsonConfig);
        result.put("rows", jsonArray);
        ResponseUtil.write(response, result);
        return null;
    }

    /**
     * 重定向审核处理页面
     * @param
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/redirectPage")
    public String redirectPage(HttpServletResponse response)throws Exception{
        JSONObject result=new JSONObject();
        result.put("url", "audit_bz.jsp");
        ResponseUtil.write(response, result);
        return null;
    }


    /**
     * 待办流程分页查询
     * @param response
     * @param page 当前页数
     * @param rows 每页显示页数
     * @param s_name 流程名称
     * @param userName 审批人
     * @return
     * @throws Exception
     */
    @RequestMapping("/taskPage")
    public String taskPage(HttpServletResponse response,HttpSession session,String page,String rows,String s_name,String userName) throws Exception{
        if(s_name==null){
            s_name="";
        }
        PageInfo pageInfo=new PageInfo();
        Integer pageSize=Integer.parseInt(rows);
        pageInfo.setPageSize(pageSize);
        if(page==null||page.equals("")){
            page="1";
        }
        pageInfo.setPageIndex((Integer.parseInt(page)-1)*pageInfo.getPageSize());
        // 获取总记录数
        System.out.println("用户："+userName+"\n"+"名称:"+s_name);
        User user = (User) session.getAttribute("currentMemberShip");
        long total;
        List<Task> taskList = null;
        if(user.getGroup().equals("总经理")||user.getGroup().equals("财务")){
            total = taskService.createTaskQuery()
                    .taskCandidateGroup(user.getRoleAbb())
                    .count();
            taskList = taskService.createTaskQuery()
                    .taskCandidateGroup(user.getRoleAbb())
                    .listPage(pageInfo.getPageIndex(),pageInfo.getPageSize());
        }else {
            total=taskService.createTaskQuery()
                    .taskCandidateOrAssigned(userName)
                    .taskNameLike("%"+s_name+"%")
                    .count(); // 获取总记录数
            //有想法的话，可以去数据库观察  ACT_RU_TASK 的变化
            taskList=taskService.createTaskQuery()
                    // 根据用户id查询
                    .taskCandidateOrAssigned(userName)
                    // 根据任务名称查询
                    .taskNameLike("%"+s_name+"%")
                    // 返回带分页的结果集合
                    .listPage(pageInfo.getPageIndex(), pageInfo.getPageSize());
        }
        //这里需要使用一个工具类来转换一下主要是转成JSON格式
        List<Bill> bills=new ArrayList<Bill>();
        for(Task t:taskList){
            Bill bill = billService.getBillByTaskId(t.getProcessInstanceId());
            if(bill!=null){
                bill.setTaskId(t.getId());
                bills.add(bill);
            }
        }
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONObject result=new JSONObject();
        JSONArray jsonArray=JSONArray.fromObject(bills,jsonConfig);
        result.put("rows", jsonArray);
        result.put("total", total);
        ResponseUtil.write(response, result);
        return null;
    }
    /**
     * 查询历史批注
     * @param response
     * @param taskId 流程ID
     * @return
     * @throws Exception
     */
    @RequestMapping("/listHistoryComment")
    public String listHistoryComment(HttpServletResponse response,String taskId) throws Exception{
        if(taskId==null){
            taskId="";
        }
        HistoricTaskInstance hti=historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .singleResult();
        JsonConfig jsonConfig=new JsonConfig();
        JSONObject result=new JSONObject();
        List<Comment> commentList=null;
        if(hti!=null){
            commentList=taskService.getProcessInstanceComments(hti.getProcessInstanceId());
            // 集合元素反转
            Collections.reverse(commentList);

            //日期格式转换
            jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        }
        JSONArray jsonArray=JSONArray.fromObject(commentList,jsonConfig);
        result.put("rows", jsonArray);
        ResponseUtil.write(response, result);
        return null;
    }

    /**
     * 审批
     * @param taskId 任务id
     * @param billMoney 账单金额
     * @param comment 批注信息
     * @param state 审核状态 1 通过 2 驳回
     * @param response
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping("/audit_bz")
    public String audit_bz(String taskId,Integer billMoney,String comment,Integer state,HttpServletResponse response,HttpSession session)throws Exception{
        //首先根据ID查询任务
        Task task=taskService.createTaskQuery() // 创建任务查询
                .taskId(taskId) // 根据任务id查询
                .singleResult();
        Map<String,Object> variables=new HashMap<String,Object>();
        //取得角色用户登入的session对象
        User currentMemberShip=(User) session.getAttribute("currentMemberShip");
        //取出用户，角色信息
        int billId=(int) taskService.getVariable(taskId, "billId");
        Bill bill=billService.findById(billId);
        if(state==1){
            if(currentMemberShip.getGroup().equals("总经理")||currentMemberShip.getGroup().equals("财务")){
                bill.setState("审核通过");
                // 更新审核信息
                billService.updateBill(bill);
            }if(currentMemberShip.getGroup().equals("职员")||currentMemberShip.getGroup().equals("组长")) {
                User user = userService.findById(currentMemberShip.getLeaderId());
                variables.put("user",user.getUserName());
            }
            variables.put("msg", "通过");
        }else{
            bill.setState("审核未通过");
            // 更新审核信息
            billService.updateBill(bill);
            variables.put("msg", "未通过");
        }
        // 设置流程变量
        variables.put("money", billMoney);
        // 获取流程实例id
        String processInstanceId=task.getProcessInstanceId();
        // 设置用户id
        Authentication.setAuthenticatedUserId(currentMemberShip.getFirstName()+currentMemberShip.getLastName()+"["+currentMemberShip.getGroup()+"]");
        // 添加批注信息
        taskService.addComment(taskId, processInstanceId, comment);
        // 完成任务
        taskService.complete(taskId, variables);
        JSONObject result=new JSONObject();
        result.put("success", true);
        ResponseUtil.write(response, result);
        return null;
    }

    /**
     * 查詢流程正常走完的历史流程表 :  act_hi_actinst
     * @param response
     * @param rows
     * @param page
     * @param s_name
     * @param role
     * @return
     * @throws Exception
     */
    @RequestMapping("/finishedList")
    public String finishedList(HttpServletResponse response,String rows,String page,String s_name,String role) throws Exception{
        if(s_name==null){
            s_name="";
        }
        PageInfo pageInfo=new PageInfo();
        Integer pageSize=Integer.parseInt(rows);
        pageInfo.setPageSize(pageSize);
        if(page==null||page.equals("")){
            page="1";
        }
        Group group = groupService.findByName(role);
        pageInfo.setPageIndex((Integer.parseInt(page)-1)*pageSize);
        //创建流程历史实例查询
        List<HistoricTaskInstance> histList=historyService.createHistoricTaskInstanceQuery()
                .taskCandidateGroup(group.getAbb())//根据角色名称查询
                .taskNameLike("%"+s_name+"%")
                .listPage(pageInfo.getPageIndex(), pageInfo.getPageSize());

        long histCount=historyService.createHistoricTaskInstanceQuery()
                .taskCandidateGroup(group.getAbb())
                .taskNameLike("%"+s_name+"%")
                .count();
        List<Bill> bills=new ArrayList<Bill>();
        //这里递出没有用的字段，免得给前端页面做成加载压力
        for(HistoricTaskInstance hti:histList){
            Bill bill = billService.getBillByTaskId(hti.getProcessInstanceId());
            bill.setTaskId(hti.getId());
            bills.add(bill);
        }
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONObject result=new JSONObject();
        JSONArray jsonArray=JSONArray.fromObject(bills,jsonConfig);
        result.put("rows", jsonArray);
        result.put("total",histCount );
        ResponseUtil.write(response, result);
        return null;
    }
    /**
     * 查詢完成的任务 :  act_hi_actinst
     * @param response
     * @param rows
     * @param page
     * @param s_name
     * @param userName
     * @return
     * @throws Exception
     */
    @RequestMapping("/CompletedList")
    public String CompletedList(HttpServletResponse response,HttpSession session,String rows,String page,String s_name,String userName) throws Exception{
        if(s_name==null){
            s_name="";
        }
        PageInfo pageInfo=new PageInfo();
        Integer pageSize=Integer.parseInt(rows);
        pageInfo.setPageSize(pageSize);
        if(page==null||page.equals("")){
            page="1";
        }
        User user = (User) session.getAttribute("currentMemberShip");
        pageInfo.setPageIndex((Integer.parseInt(page)-1)*pageSize);
        List<HistoricTaskInstance> histList = null;
        long histCount;
        if(user.getGroup().equals("财务")||user.getGroup().equals("总经理")){
            histList=historyService.createHistoricTaskInstanceQuery()
                    .taskCandidateGroup(user.getRoleAbb())//根据角色名称查询
                    .taskNameLike("%"+s_name+"%")
                    .listPage(pageInfo.getPageIndex(), pageInfo.getPageSize());

            histCount=historyService.createHistoricTaskInstanceQuery()
                    .taskCandidateGroup(user.getRoleAbb())
                    .taskNameLike("%"+s_name+"%")
                    .count();
        }else {
            histList=historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userName)//根据角色名称查询
                    .taskNameLike("%"+s_name+"%")
                    .listPage(pageInfo.getPageIndex(), pageInfo.getPageSize());

            histCount=historyService.createHistoricTaskInstanceQuery()
                    .taskAssignee(userName)
                    .taskNameLike("%"+s_name+"%")
                    .count();
        }
        List<Bill> bills=new ArrayList<Bill>();
        //这里递出没有用的字段，免得给前端页面做成加载压力
        for(HistoricTaskInstance hti:histList){
            Bill bill = billService.getBillByTaskId(hti.getProcessInstanceId());
            if(bill!=null){
                bill.setTaskId(hti.getId());
                bills.add(bill);
            }
        }
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONObject result=new JSONObject();
        JSONArray jsonArray=JSONArray.fromObject(bills,jsonConfig);
        result.put("rows", jsonArray);
        result.put("total",histCount );
        ResponseUtil.write(response, result);
        return null;
    }
    /**
     * 根据任务id查询流程实例的具体执行过程
     * @param taskId
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/listAction")
    public String listAction(String taskId,HttpServletResponse response)throws Exception{
        if(taskId != null){
            HistoricTaskInstance hti=historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            String processInstanceId=hti.getProcessInstanceId(); // 获取流程实例id
            List<HistoricActivityInstance> haiList=historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            JsonConfig jsonConfig=new JsonConfig();
            jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
            JSONObject result=new JSONObject();
            JSONArray jsonArray=JSONArray.fromObject(haiList,jsonConfig);
            result.put("rows", jsonArray);
            ResponseUtil.write(response, result);
        }
        return null;
    }
    /**
     * 根据流程id查询流程实例的具体执行过程
     * @param processInstanceId
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/listActionWithProcessInstanceId")
    public String listActionWithProcessInstanceId(String processInstanceId,HttpServletResponse response)throws Exception{
        if(processInstanceId != null){
            List<HistoricActivityInstance> haiList=historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            JsonConfig jsonConfig=new JsonConfig();
            jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
            JSONObject result=new JSONObject();
            JSONArray jsonArray=JSONArray.fromObject(haiList,jsonConfig);
            result.put("rows", jsonArray);
            ResponseUtil.write(response, result);
        }
        return null;
    }
}
