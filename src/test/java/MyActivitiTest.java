import com.activiti.model.MyTask;
import com.activiti.util.DateJsonValueProcessor;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivitiTest {

    public ProcessEngine test() {
        // 1.创建Activiti配置对象的实例
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration();
        // 2.设置数据库连接信息
        // 设置数据库的类型
        configuration.setDatabaseType("mysql");
        // 设置数据库驱动
        configuration.setJdbcDriver("com.mysql.jdbc.Driver");
        // 设置jdbcURL
        configuration.setJdbcUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=utf-8&amp;allowMultiQueries=true&amp;useSSL=false");
        // 设置用户名
        configuration.setJdbcUsername("root");
        // 设置密码
        configuration.setJdbcPassword("990117");
        // 设置数据库建表策略
        /**
         * DB_SCHEMA_UPDATE_TRUE：如果不存在表就创建表，存在就直接使用
         * DB_SCHEMA_UPDATE_FALSE：如果不存在表就抛出异常
         * DB_SCHEMA_UPDATE_CREATE_DROP：每次都先删除表，再创建新的表
         */
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        // 3.使用配置对象创建流程引擎实例（检查数据库连接等环境信息是否正确）
        ProcessEngine processEngine = configuration.buildProcessEngine();
        System.out.println(processEngine);
        return processEngine;
    }

    /**
     * 第二种创建方式 一般使用最多的就是这种创建方式
     */
//    @Test
//    public void initActiviti() {
//        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        System.out.println(processEngine);
//    }



    /**
     * 部署流程定义
     */
    @Test
    public void deployProcess() throws FileNotFoundException {
        ProcessEngine processEngine = test();
        File ccc = new File("E:/Project/ApprovalProcess/src/main/resources/activiti/approvalProcess1.0.bpmn20.xml");//bpmn所在位置
        File png = new File("E:/Project/ApprovalProcess/src/main/resources/activiti/approvalProcess1.0.bpmn20.png");
        InputStream fis = new FileInputStream(ccc);
        InputStream inputStreamPng = new FileInputStream(png);

        //得到流程部署的service
        Deployment deploy = processEngine.getRepositoryService().createDeployment().name("账单审批流程")
                .addInputStream("ApprovalProcess1.0.bpmn",fis)
                .addInputStream("ApprovalProcess1.0.png",inputStreamPng).deploy();
        System.out.println("部署成功，流程id:"+deploy.getId());
    }
    /**
     * 启动流程
     */
    @Test
    public void startProcess(){
        ProcessEngine processEngine = test();
        Map<String,Object> variables=new HashMap<String,Object>();
        variables.put("billId", "zhangsan");
        variables.put("user","xiaowang");
        //使用key启动流程
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("approvalProcess",variables);
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        System.out.println("流程实例ID:"+processInstance.getId());//流程实例ID    101
        System.out.println("流程定义ID:"+processInstance.getProcessDefinitionId());
        System.out.println("流程定义ID:"+processInstance.getActivityId());
        System.out.println("ID:"+task.getId()+",姓名:"+task.getName()+",接收人:"+task.getAssignee() + task.getCategory() +",开始时间:"+task.getCreateTime());
        System.out.println("流程启动成功");
        processEngine.getTaskService().complete(task.getId());
    }

    @Test
    public void queryProcdef(){
//        ProcessEngine processEngine = test();
//        RepositoryService repositoryService = processEngine.getRepositoryService();
//        //创建查询对象
//        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
//        //添加查询条件
//        query.processDefinitionKey("billProcess");//通过key获取
//        // .processDefinitionName("My process")//通过name获取
//        // .orderByProcessDefinitionId()//根据ID排序
//        //执行查询获取流程定义明细
//        List<ProcessDefinition> pds = query.list();
//        for (ProcessDefinition pd : pds) {
//            System.out.println("ID:"+pd.getId()+",NAME:"+pd.getName()+",KEY:"+pd.getKey()+",VERSION:"+pd.getVersion()+",RESOURCE_NAME:"+pd.getResourceName()+",DGRM_RESOURCE_NAME:"+pd.getDiagramResourceName());
//        }
    }

    @Test
    public void queryTask(){
        ProcessEngine processEngine = test();
        Map<String,Object> variables=new HashMap<String,Object>();
        //获取任务服务对象
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().taskCandidateGroup("caiwu").singleResult();
        //根据接受人获取该用户的任务
        variables.put("msg","通过");
//        variables.put("money",1000);
        taskService.complete("42525",variables);
    }

    @Test
    public void sheach(){
//        ProcessEngine processEngine = test();
//        Map<String, Comment> tables = new HashMap<String, Comment>();
//        Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
//        List<Comment> commentList = processEngine.getTaskService()
//                .getProcessInstanceComments("40001");
//        JsonConfig jsonConfig=new JsonConfig();
//        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
//        JSONArray jsonArray=JSONArray.fromObject(commentList,jsonConfig);
//        JSONObject jsonObject = jsonArray.getJSONObject(0);
//        System.out.println(jsonObject);
    }


}
