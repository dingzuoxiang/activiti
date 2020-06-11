package com.activiti.controller;

import com.activiti.model.*;
import com.activiti.service.*;
import com.activiti.util.DateJsonValueProcessor;
import com.activiti.util.ExcelStyleUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 导出数据
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/export")
public class ExportController {

    @Resource
    private TaskCommentService taskCommentService;

    @Resource
    private BillService billService;

    @Resource
    private UserService userService;

    @Resource
    private GroupService groupService;

    @RequestMapping("/taskExcel")
    public String taskExcelXLSX(HttpServletResponse response, HttpSession session, String userName, String name, int status) throws Exception{
        String[] tableHeader = {"任务Id","申请人","申请详情","申请金额","创建时间","批注人","任务批注","批注时间","批注人","任务批注","批注时间","批注人","任务批注","批注时间"};
        JSONArray taskJsonArray = null;
        User user = (User) session.getAttribute("currentMemberShip");
        if(status == 1){
            taskJsonArray = taskCommentService.getRunTaskByAssigned(userName,user.getRoleAbb());
        }else{
            taskJsonArray = taskCommentService.getHistoryTaskByAssigned(userName,user.getRoleAbb());
        }
        short cellNumber=(short)tableHeader.length;//表的列数
        HSSFWorkbook workbook = new HSSFWorkbook(); //创建一个Excel
        CellStyle style = workbook.createCellStyle(); //设置表头的类型
        style.setAlignment(HorizontalAlignment.CENTER);
        CellStyle style1 = workbook.createCellStyle(); //设置数据类型
        ExcelStyleUtil.center(style1);
        HSSFFont font = workbook.createFont(); //设置字体
        HSSFSheet sheet = workbook.createSheet("sheet1"); //创建一个sheet
        HSSFHeader header = sheet.getHeader();//设置sheet的头

        try{
            if(taskJsonArray == null || taskJsonArray.size() < 1 ){
                header.setCenter("查无资料");
            }else {
                header.setCenter(name);
                setHeader(sheet,style1,font,cellNumber,tableHeader);
                /**
                 * 列 数据 在这里插入数据
                 */
                Row bodyRow;
                Cell bodyCell;
                for (int i = 0; i < taskJsonArray.size(); i++) {
                    JSONObject myTask = taskJsonArray.getJSONObject(i);
                    bodyRow = sheet.createRow((i + 1));
                    bodyCell = bodyRow.createCell((short) 0);//创建第i+1行第0列
                    bodyCell.setCellValue((String)myTask.get("taskId"));//设置第i+1行第0列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 1);//创建第i+1行第1列
                    bodyCell.setCellValue((String) myTask.get("userName"));//设置第i+1行第1列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 2);//创建第i+1行第2列
                    bodyCell.setCellValue((String)myTask.get("billDetail"));//设置第i+1行第2列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 3);//创建第i+1行第3列
                    bodyCell.setCellValue((Integer) myTask.get("billMoney"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 4);//创建第i+1行第3列
                    bodyCell.setCellValue((String) myTask.get("billDate"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    JSONArray commentJsonArray = taskCommentService.getCommentByProcessInstanceId((String) myTask.get("processInstanceId"));
                    for(int j = 0;j < commentJsonArray.size(); j++){
                        JSONObject comment = commentJsonArray.getJSONObject(j);
                        int k = j * 3;
                        bodyCell = bodyRow.createCell((short) 5 + k);//创建第i+1行第4列
                        bodyCell.setCellValue((String) comment.get("userId"));//设置第i+1行第4列的值
                        bodyCell.setCellStyle(style);//设置风格

                        bodyCell = bodyRow.createCell((short) 6 + k);//创建第i+1行第5列
                        bodyCell.setCellValue((String) comment.get("message"));//设置第i+1行第5列的值
                        bodyCell.setCellStyle(style);//设置风格

                        bodyCell = bodyRow.createCell((short) 7 + k);//创建第i+1行第6列
                        bodyCell.setCellValue((String) comment.get("time"));//设置第i+1行第6列的值
                        bodyCell.setCellStyle(style);//设置风格
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String fileName = name + ".xls";
        outputSetting(fileName,workbook,response);
        return null;
    }

    @RequestMapping("/billExcel")
    public String billExcelXLSX(HttpServletResponse response,String userName) throws Exception{
        String[] tableHeader = {"编号","申请人","申请日期","账单金额","账单用处","审核状态","流程实例Id","批注人","任务批注","批注时间","批注人","任务批注","批注时间","批注人","任务批注","批注时间"};
        String name;
        Map<String, Object> map = new HashMap<String, Object>();
        if(userName == null || userName.equals("")){
            name = "账单表";
            map.put("username", null);
        }else{
            name = "个人账单表";
            map.put("username", userName);
        }
        List<Bill> billsList = billService.billPage(map);
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray billJsonArray=JSONArray.fromObject(billsList,jsonConfig);
        short cellNumber=(short)tableHeader.length;//表的列数
        HSSFWorkbook workbook = new HSSFWorkbook(); //创建一个Excel
        CellStyle style = workbook.createCellStyle(); //设置表头的类型
        style.setAlignment(HorizontalAlignment.CENTER);
        CellStyle style1 = workbook.createCellStyle(); //设置数据类型
        ExcelStyleUtil.center(style1);
        HSSFFont font = workbook.createFont(); //设置字体
        HSSFSheet sheet = workbook.createSheet("sheet1"); //创建一个sheet
        HSSFHeader header = sheet.getHeader();//设置sheet的头

        try{
            if(billJsonArray == null || billJsonArray.size() < 1 ){
                header.setCenter("查无资料");
            }else {
                header.setCenter(name);
                setHeader(sheet,style1,font,cellNumber,tableHeader);
                /**
                 * 列 数据 在这里插入数据
                 */
                Row bodyRow;
                Cell bodyCell;
                for (int i = 0; i < billJsonArray.size(); i++) {
                    JSONObject myTask = billJsonArray.getJSONObject(i);
                    System.out.println(myTask.getJSONObject("user").get("id"));
                    bodyRow = sheet.createRow((i + 1));
                    bodyCell = bodyRow.createCell((short) 0);//创建第i+1行第0列
                    bodyCell.setCellValue((int)myTask.get("id"));//设置第i+1行第0列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 1);//创建第i+1行第1列
                    bodyCell.setCellValue((String) myTask.get("userName"));//设置第i+1行第1列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 2);//创建第i+1行第1列
                    bodyCell.setCellValue((String) myTask.get("billDate"));//设置第i+1行第1列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 3);//创建第i+1行第2列
                    bodyCell.setCellValue((int)myTask.get("billMoney"));//设置第i+1行第2列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 4);//创建第i+1行第3列
                    bodyCell.setCellValue((String) myTask.get("billDetail"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 5);//创建第i+1行第3列
                    bodyCell.setCellValue((String) myTask.get("state"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 6);//创建第i+1行第3列
                    bodyCell.setCellValue((String) myTask.get("processInstanceId"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    JSONArray commentJsonArray = taskCommentService.getCommentByProcessInstanceId((String) myTask.get("processInstanceId"));
                    for(int j = 0;j < commentJsonArray.size(); j++){
                        JSONObject comment = commentJsonArray.getJSONObject(j);
                        int k = j * 3;
                        bodyCell = bodyRow.createCell((short) 7 + k);//创建第i+1行第4列
                        bodyCell.setCellValue((String) comment.get("userId"));//设置第i+1行第4列的值
                        bodyCell.setCellStyle(style);//设置风格

                        bodyCell = bodyRow.createCell((short) 8 + k);//创建第i+1行第5列
                        bodyCell.setCellValue((String) comment.get("message"));//设置第i+1行第5列的值
                        bodyCell.setCellStyle(style);//设置风格

                        bodyCell = bodyRow.createCell((short) 9 + k);//创建第i+1行第6列
                        bodyCell.setCellValue((String) comment.get("time"));//设置第i+1行第6列的值
                        bodyCell.setCellStyle(style);//设置风格
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String fileName = name + ".xls";
        outputSetting(fileName,workbook,response);

        return null;
    }

    @RequestMapping("/userExcel")
    public String userExcelXLSX(HttpServletResponse response) throws Exception{
        String[] tableHeader = {"用户Id","用户名","密码","姓名","角色","电话","邮箱","上级领导Id","所属部门","注册时间"};
        Map<String,Object> userMap=new HashMap<String, Object>();
        userMap.put("username", null);
        List<User> userList = userService.userPage(userMap);
        JsonConfig jsonConfig=new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        JSONArray userJsonArray=JSONArray.fromObject(userList,jsonConfig);
        String name = "用户表";
        short cellNumber=(short)tableHeader.length;//表的列数
        HSSFWorkbook workbook = new HSSFWorkbook(); //创建一个Excel
        CellStyle style = workbook.createCellStyle(); //设置表头的类型
        style.setAlignment(HorizontalAlignment.CENTER);
        CellStyle style1 = workbook.createCellStyle(); //设置数据类型
        ExcelStyleUtil.center(style1);
        HSSFFont font = workbook.createFont(); //设置字体
        HSSFSheet sheet = workbook.createSheet("sheet1"); //创建一个sheet
        HSSFHeader header = sheet.getHeader();//设置sheet的头

        try{
            if(userJsonArray == null || userJsonArray.size() < 1 ){
                header.setCenter("查无资料");
            }else {
                header.setCenter(name);
                setHeader(sheet,style1,font,cellNumber,tableHeader);
                /**
                 * 列 数据 在这里插入数据
                 */
                Row bodyRow;
                Cell bodyCell;
                for (int i = 0; i < userJsonArray.size(); i++) {
                    JSONObject user = userJsonArray.getJSONObject(i);
                    System.out.println("——————————————————————");
                    System.out.println(user);
                    bodyRow = sheet.createRow((i + 1));
                    bodyCell = bodyRow.createCell((short) 0);//创建第i+1行第0列
                    bodyCell.setCellValue((Integer)user.get("id"));//设置第i+1行第0列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 1);//创建第i+1行第1列
                    bodyCell.setCellValue((String) user.get("userName"));//设置第i+1行第1列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 2);//创建第i+1行第1列
                    bodyCell.setCellValue((String) user.get("password"));//设置第i+1行第1列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 3);//创建第i+1行第2列
                    bodyCell.setCellValue((String) user.get("firstName") + (String) user.get("lastName"));//设置第i+1行第2列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 4);//创建第i+1行第3列
                    bodyCell.setCellValue((String) user.get("group"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 5);//创建第i+1行第3列
                    bodyCell.setCellValue((String) user.get("call"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 6);//创建第i+1行第3列
                    bodyCell.setCellValue((String) user.get("email"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 7);//创建第i+1行第3列
                    bodyCell.setCellValue((int) user.get("leaderId"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 8);//创建第i+1行第3列
                    bodyCell.setCellValue((String) user.get("deptName"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格

                    bodyCell = bodyRow.createCell((short) 9);//创建第i+1行第3列
                    bodyCell.setCellValue((String) user.get("time"));//设置第i+1行第3列的值
                    bodyCell.setCellStyle(style);//设置风格
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String fileName = name + ".xls";
        outputSetting(fileName,workbook,response);
        return null;
    }

    private void outputSetting(String fileName,HSSFWorkbook workbook,HttpServletResponse response) {
        OutputStream out = null;//创建一个输出流对象
        try {
            out = response.getOutputStream();// 得到输出流
            response.setHeader("Content-disposition","attachment; filename="+new String(fileName.getBytes(),"ISO-8859-1"));//filename是下载的xls的名
            response.setContentType("application/msexcel;charset=UTF-8");//设置类型
            response.setHeader("Pragma","No-cache");//设置头
            response.setHeader("Cache-Control","no-cache");//设置头
            response.setDateHeader("Expires", 0);//设置日期头
            workbook.write(out);
            out.flush();
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try{
                if(out!=null){
                    out.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void setHeader(HSSFSheet sheet,CellStyle style,HSSFFont font,int cellNumber,String[] tableHeader){
        Row row = sheet.createRow(0);
        row.setHeight((short)400);
        //表头
        for(int k = 0;k < cellNumber;k++){
            Cell cell = row.createCell((short) k);//创建第0行第k列
            cell.setCellValue(tableHeader[k]);//设置第0行第k列的值
            sheet.setColumnWidth((short)k,(short)8000);//设置列的宽度
            font.setColor(HSSFFont.COLOR_NORMAL); // 设置单元格字体的颜色.
            font.setFontHeight((short)350); //设置单元字体高度
            style.setFont(font);//设置字体风格
            cell.setCellStyle(style);
        }
    }
}
