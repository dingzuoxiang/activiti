<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>账单管理</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript">
        function formatAction(val,row){
            if(row.state=='未提交'){
                return "<a href='javascript:startApply("+row.id+")'>提交申请</a>";
            }else if(row.state=='审核通过' || row.state=='审核未通过'){
                return "<a href='javascript:openListCommentDialog("+row.processInstanceId+")'>查看历史批注</a>";
            }
        }

        function openListCommentDialog(processInstanceId){
            $("#dg2").datagrid("load",{
                processInstanceId:processInstanceId
            });
            $("#dlg2").dialog("open").dialog("setTitle","查看历史批注");
        }

        function openBillAddDialog(){
            $("#dlg").dialog("open").dialog("setTitle","添加请假单信息");
        }

        function save(){
            var money = $("#billMoney").val();
            if(money > 0){
                if($("#billDetail").val() != ""){
                    saveBill();
                }else {
                    $.messager.alert("系统系统","账单用处不能为空！");
                    $("#billDetail").focus();
                    return;
                }
            }else{
                $.messager.alert("系统系统","账单金额不能小于零！");
                $("#billMoney").focus();
                return;
            }
        }

        function saveBill(){
            $("#fm").form("submit",{
                url:'${pageContext.request.contextPath}/bill/save.action',
                onSubmit:function(){
                    return $(this).form("validate");
                },
                success:function(result){
                    var result=eval('('+result+')');
                    if(result.success){
                        $.messager.alert("系统系统","保存成功！");
                        resetValue();
                        $("#dlg").dialog("close");
                        $("#dg").datagrid("reload");
                    }else{
                        $.messager.alert("系统系统","保存失败！");
                        return;
                    }
                }
            });
        }


        function resetValue(){
            $("#billMoney").val("");
            $("#billDetail").val("");
        }

        function closeBillDialog(){
            $("#dlg").dialog("close");
            resetValue();
        }
        //提交账单流程申请
        function startApply(billId){
            $.post("${pageContext.request.contextPath}/bill/startApply.action",{billId:billId},function(result){
                if(result.success){
                    $.messager.alert("系统系统","账单申请提交成功，目前审核中，请耐心等待！");
                    $("#dg").datagrid("reload");
                }else{
                    $.messager.alert("系统系统","账单申请提交失败，请联系管理员！");
                }
            },"json");
        }

    </script>
</head>
<body style="margin: 1px">
<table id="dg" title="账单管理" class="easyui-datagrid"
       fitColumns="true" pagination="true" rownumbers="true"
       url="${pageContext.request.contextPath}/bill/billPage.action?username=${currentMemberShip.userName}" fit="true" toolbar="#tb">
    <thead>
    <tr>
        <th field="cb" checkbox="true" align="center"></th>
        <th field="id" width="30" align="center">编号</th>
        <th field="billDate" width="80" align="center">申请日期</th>
        <th field="billMoney" width="50" align="center">账单金额</th>
        <th field="billDetail" width="200" align="center">账单用处</th>
        <th field="state" width="30" align="center">审核状态</th>
        <th field="processInstanceId" width="30" hidden="true" align="center">流程实例Id</th>
        <th field="action" width="50" align="center" formatter="formatAction">操作</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <div>
        <a href="javascript:openBillAddDialog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">新增帐单</a>
        <a href="${pageContext.request.contextPath}/export/billExcel.action?userName=${currentMemberShip.userName}" class="tooltip-right">导出</a>
    </div>
</div>

<div id="dlg" class="easyui-dialog" style="width: 620px;height: 280px;padding: 10px 20px" closed="true" buttons="#dlg-buttons">

    <form id="fm" method="post">
        <table cellpadding="8px">
            <tr>
                <td>账单金额：</td>
                <td>
                    <input type="text" id="billMoney" name="billMoney" class="easyui-numberbox" required="true"/>
                </td>
            </tr>
            <tr>
                <td valign="top">账单用处：</td>
                <td>
                    <input type="hidden" name="userName" value="${currentMemberShip.userName}"/>
                    <input type="hidden" name="state" value="未提交"/>
                    <textarea type="text" id="billDetail" name="billDetail"  rows="5" cols="49" class="easyui-validatebox" required="true"></textarea>
                </td>
            </tr>
        </table>
    </form>

</div>

<div id="dlg-buttons">
    <a href="javascript:save()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
    <a href="javascript:closeBillDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>


<div id="dlg2" class="easyui-dialog" style="width: 750px;height: 250px;padding: 10px 20px" closed="true" >

    <table id="dg2" title="批注列表" class="easyui-datagrid"
           fitColumns="true"
           url="${pageContext.request.contextPath}/task/listHistoryCommentWithProcessInstanceId.action" style="width: 700px;height: 200px;">
        <thead>
        <tr>
            <th field="time" width="120" align="center">批注时间</th>
            <th field="userId" width="100" align="center">批注人</th>
            <th field="message" width="200" align="center">批注信息</th>
        </tr>
        </thead>
    </table>

</div>


</body>
</html>