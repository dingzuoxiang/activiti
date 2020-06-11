<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>员工账单审批流程系统-主页面</title>
    <%
        // 权限验证
        if(session.getAttribute("currentMemberShip")==null){
            response.sendRedirect("index.jsp");
            return;
        }
    %>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript">
        var url;
        function openTab(text,url,iconCls){
            if($("#tabs").tabs("exists",text)){
                $("#tabs").tabs("select",text);
            }else{
                var content="<iframe frameborder=0 scrolling='auto' style='width:100%;height:100%' src='${pageContext.request.contextPath}/page/"+url+"'></iframe>";
                $("#tabs").tabs("add",{
                    title:text,
                    iconCls:iconCls,
                    closable:true,
                    content:content
                });
            }
        }

        function openPasswordModifyDialog(){
            url="${pageContext.request.contextPath}/user/modifyPassword.action?id=${currentMemberShip.id}";
            $("#dlg").dialog("open").dialog("setTitle","修改密码");
        }

        function modifyPassword(){
            //是否包含数字
            var ptr_digit = /^.*[0-9]+.*$/;
            //是否包含小写字母
            var ptr_lowcase = /^.*[a-z]+.*$/;
            //是否包含大写字母
            var ptr_upcase = /^.*[A-Z]+.*$/;
            //是否包含特殊字符（非数字、字母的字符）
            var ptr_special = /((?=[\x21-\x7e]+)[^A-Za-z0-9])/;

            $("#fm").form("submit",{
                url:url,
                onSubmit:function(){
                    var oldPassword=$("#oldPassword").val();
                    var newPassword=$("#newPassword").val();
                    var newPassword2=$("#newPassword2").val();
                    if(!$(this).form("validate")){
                        return false;
                    }
                    if(oldPassword!='${currentMemberShip.password}'){
                        $.messager.alert("系统系统","用户原密码输入错误！");
                        return false;
                    }
                    if(newPassword!=newPassword2){
                        $.messager.alert("系统系统","确认密码输入错误！");
                        return false;
                    }
                    if(newPassword.length < 6){
                        $.messager.alert("系统系统","密码的长度不能小于6位！");
                        return false;
                    }else if((ptr_digit.exec(newPassword) && ptr_lowcase.exec(newPassword) && ptr_upcase.exec(newPassword) && ptr_special.exec(newPassword))
                        || (!ptr_digit.exec(newPassword) && ptr_lowcase.exec(newPassword) && ptr_upcase.exec(newPassword) && ptr_special.exec(newPassword))
                        || (ptr_digit.exec(newPassword) && !ptr_lowcase.exec(newPassword) && ptr_upcase.exec(newPassword) && ptr_special.exec(newPassword))
                        || (ptr_digit.exec(newPassword) && ptr_lowcase.exec(newPassword) && !ptr_upcase.exec(newPassword) && ptr_special.exec(newPassword))
                        || (ptr_digit.exec(newPassword) && ptr_lowcase.exec(newPassword) && ptr_upcase.exec(newPassword) && !ptr_special.exec(newPassword))){
                        return true;
                    }else{
                        $.messager.alert("系统系统","密码至少包含大写字母、小写字母、数字、特殊字符中的三类字符！");
                        return false;
                    }
                },
                success:function(result){
                    var result=eval('('+result+')');
                    if(result.success){
                        $.messager.alert("系统系统","密码修改成功，下一次登录生效！");
                        resetValue();
                        $("#dlg").dialog("close");
                    }else{
                        $.messager.alert("系统系统","密码修改失败，请联系管理员！");
                        return;
                    }
                }
            });
        }

        function resetValue(){
            $("#oldPassword").val("");
            $("#newPassword").val("");
            $("#newPassword2").val("");
        }

        function closePasswordModifyDialog(){
            resetValue();
            $("#dlg").dialog("close");
        }

        function logout(){
            $.messager.confirm("系统提示","您确定要退出系统吗?",function(r){
                if(r){
                    window.location.href='${pageContext.request.contextPath}/user/logout.action';
                }
            });
        }

        function exportData(val){
            if (val==1){
                window.location.href = '${pageContext.request.contextPath}/export/userExcel.action';
            }else{
                window.location.href = '${pageContext.request.contextPath}/export/billExcel.action?userId=';
            }
        }

    </script>
</head>
<body class="easyui-layout">
<div region="north" style="height: 78px;background-color: #E0ECFF">
    <table style="padding: 5px;" width="100%">
        <tr>
            <td width="50%">
                <img src=""/>
            </td>
            <td valign="bottom" align="right" width="50%">
                <font size="3">&nbsp;&nbsp;<strong>欢迎：</strong>(${currentMemberShip.deptName})${currentMemberShip.firstName }${currentMemberShip.lastName } 【${currentMemberShip.group}】</font>
            </td>
        </tr>
    </table>
</div>
<div region="center" >
    <div class="easyui-tabs" fit="true" border="false" id="tabs">
        <div title="首页" data-options="iconCls:'icon-home'">
            <div align="center" style="padding-top: 100px;"><font color="red" size="10">欢迎使用</font></div>
        </div>
    </div>
</div>
<div region="west" style="width: 200px;" title="导航菜单" split="true">
    <div class="easyui-accordion" data-options="fit:true,border:false">
        <c:if test="${currentMemberShip.group=='管理员' }">
            <div title="基础数据管理" data-options="selected:true,iconCls:'icon-item'" style="padding: 10px">
                <a href="javascript:openTab('用户管理','userManage.jsp','icon-user')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-user'" style="width: 150px">用户管理</a>
                <a href="javascript:openTab('角色管理','groupManage.jsp','icon-role')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-role'" style="width: 150px">角色管理</a>
                <a href="javascript:openTab('组管理','authManage.jsp','icon-power')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-power'" style="width: 150px">组管理</a>
                <a href="javascript:exportData(1)" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-power'" style="width: 150px">导出用户</a>
            </div>
            <div title="流程管理"  data-options="iconCls:'icon-flow'" style="padding:10px;">
                <a href="javascript:openTab('流程部署管理','deployManage.jsp','icon-deploy')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-deploy'" style="width: 150px;">流程部署管理</a>
                <a href="javascript:openTab('流程定义管理','processDefinitionManage.jsp','icon-definition')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-definition'" style="width: 150px;">流程定义管理</a>
            </div>
            <div title="任务管理" data-options="iconCls:'icon-task'" style="padding:10px">
                <a href="javascript:openTab('任务管理','lishiManage.jsp','icon-lishi')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-lishi'" style="width: 150px;">任务管理</a>
                <a href="javascript:exportData(2)" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-power'" style="width: 150px;">导出任务</a>
            </div>
        </c:if>
        <c:if test="${currentMemberShip.group!='职员'&& currentMemberShip.group!='管理员'}">
            <div title="任务管理" data-options="iconCls:'icon-task'" style="padding:10px">
                <a href="javascript:openTab('待办任务管理','daibanManage.jsp','icon-daiban')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-daiban'" style="width: 150px;">待办任务管理</a>
                <a href="javascript:openTab('已办任务管理','yibanManage.jsp','icon-lishi')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-lishi'" style="width: 150px;">已办任务管理</a>
            </div>
        </c:if>
        <c:if test="${currentMemberShip.group=='职员'}">
            <div title="业务管理"  data-options="iconCls:'icon-yewu'" style="padding:10px">
                <a href="javascript:openTab('账单审批','billManage.jsp','icon-apply')" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-apply'" style="width: 150px">账单审批</a>
            </div>
        </c:if>
        <div title="系统管理"  data-options="iconCls:'icon-system'" style="padding:10px">
            <a href="javascript:openPasswordModifyDialog()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-modifyPassword'" style="width: 150px;">修改密码</a>
            <a href="javascript:logout()" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-exit'" style="width: 150px;">安全退出</a>
        </div>
    </div>
</div>
<div region="south" style="height: 26px;padding: 5px" align="center">
    Copyright @ 2020 丁作祥 版权所有
</div>

<div id="dlg" class="easyui-dialog" style="width: 400px;height: 250px;padding: 10px 20px" closed="true" buttons="#dlg-buttons">

    <form id="fm" method="post">
        <table cellpadding="8px">
            <tr>
                <td>用户名：</td>
                <td>
                    <input type="text" id="userName" name="userName" readonly="readonly" value="${currentMemberShip.userName }" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>原密码：</td>
                <td>
                    <input type="password" id="oldPassword" name="oldPassword" class="easyui-validatebox" required="true" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>新密码：</td>
                <td>
                    <input type="password" id="newPassword" name="newPassword" class="easyui-validatebox" required="true" style="width: 200px"/>
                </td>
            </tr>
            <tr>
                <td>确认密码：</td>
                <td>
                    <input type="password" id="newPassword2" name="newPassword2" class="easyui-validatebox" required="true" style="width: 200px"/>
                </td>
            </tr>
        </table>
    </form>

</div>

<div id="dlg-buttons">
    <a href="javascript:modifyPassword()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
    <a href="javascript:closePasswordModifyDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>

</body>
</html>