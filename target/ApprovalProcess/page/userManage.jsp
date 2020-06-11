<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户管理</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">

    $(function() {
        // 下拉框选择控件，下拉框的内容是动态查询数据库信息
        $('#group').combobox({
            url:'${pageContext.request.contextPath}/group/listAllGroups.action',
            panelHeight: 'auto',
            valueField:'id',
            textField:'name',

            onHidePanel: function(){
                $("#dept_parentId").combobox("setValue",'');
                $("#deptId").combobox("setValue",'');
                var groupId = $('#group').combobox('getValue');
                if(groupId == 1 || groupId == 2){
					document.getElementById("td").text = "小组";
                	var div = document.getElementById("div");
                	div.style.display = "block";
				}
                console.info("groupId："+groupId);
                $.ajax({
                    type: "POST",
                    url: '${pageContext.request.contextPath}/depart/getDepartByGroupId.action',
                    data: {"groupId":groupId},
                    cache: false,
                    dataType : "json",
                    success: function(data){
                        $("#dept_parentId").combobox("loadData",data);
                    }
                });
            }
        });

        $("#dept_parentId").combobox({
            panelHeight: 'auto',
            valueField:'id',
            textField:'name',

            onHidePanel: function(){
                $("#deptId").combobox("setValue",'');
                var groupId = $('#group').combobox('getValue');
                var parentId = $('#dept_parentId').combobox('getValue');
				console.info("levelName："+groupId+parentId);
                if(groupId == "1" || groupId == "2"){
					$.ajax({
						type: "POST",
						url: '${pageContext.request.contextPath}/depart/getDepartByParentId.action',
						data: {"parentId":parentId},
						cache: false,
						dataType : "json",
						success: function(data){
							$("#deptId").combobox("loadData",data);
						}
					});
				}else {
                	$("#deptId").combobox("setValue",0);
				}
            }
        });

        $("#deptId").combobox({
            panelHeight: 'auto',
            valueField:'id',
            textField:'name',
        });
    });


	function searchUser(){
		$("#dg").datagrid('load',{
			"userName":$("#s_id").val()
		});
	}
	
	function deleteUser(){
		var selectRows=$("#dg").datagrid("getSelections");
		if(selectRows.length==0){
			$.messager.alert("系统提示","请选择要删除的数据！");
			return;
		}
		var strIds=[];
		for(var i=0;i<selectRows.length;i++){
			strIds.push(selectRows[i].id);
		}
		var ids=strIds.join(",");
		$.messager.confirm("系统提示","您确定要删除这<font color=red>"+selectRows.length+"</font>条数据吗?",function(r){
			if(r){
				$.post("${pageContext.request.contextPath}/user/deleteUser.action",{ids:ids},function(result){
					if(result.success){
						$.messager.alert("系统提示","数据已经成功删除！");
						$("#dg").datagrid("reload");
					}else{
						$.messager.alert("系统提示","数据删除失败，请联系管理员！");
					}
				},"json");
			}
		});
	}
	
	function openUserAddDiglog(){
		$("#dlg").dialog("open").dialog("setTitle","添加用户信息");
		$("#flag").val(1);
		$("#userName").attr("readonly",false);
	}
	
	function openUserModifyDiglog(){
		var selectRows=$("#dg").datagrid("getSelections");
		if(selectRows.length!=1){
			$.messager.alert("系统提示","请选择一条要编辑的数据！");
			return;
		}
		var row=selectRows[0];
		$("#dlg").dialog("open").dialog("setTitle","编辑用户信息");
		$("#fm").form("load",row);
		$("#flag").val(2);
		if(row.group == "职员" || row.group == "组长"){
			document.getElementById("td").text = "小组：";
			document.getElementById("div").style.display = "block";
		}
		$("#userName").attr("readonly",true);
	}
	
	
	function checkData(){
		//是否包含数字
		var ptr_digit = /^.*[0-9]+.*$/;
		//是否包含小写字母
		var ptr_lowcase = /^.*[a-z]+.*$/;
		//是否包含大写字母
		var ptr_upcase = /^.*[A-Z]+.*$/;
		//是否包含特殊字符（非数字、字母的字符）
		var ptr_special = /((?=[\x21-\x7e]+)[^A-Za-z0-9])/;

        var reg = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;

		var password = $("#password").val();

		var flag=$("#flag").val();

		if($("#name").val()==''){
			$.messager.alert("系统系统","请输入用户名！");
			$("#name").focus();
			return;
		}
		if(password.length < 6){
			$.messager.alert("系统系统","密码的长度不能小于6位！");
			$("#password").focus();
			return;
		}else if((ptr_digit.test(password) && ptr_lowcase.test(password) && ptr_upcase.test(password) && ptr_special.test(password))
				|| (!ptr_digit.test(password) && ptr_lowcase.test(password) && ptr_upcase.test(password) && ptr_special.test(password))
				|| (ptr_digit.test(password) && !ptr_lowcase.test(password) && ptr_upcase.test(password) && ptr_special.test(password))
				|| (ptr_digit.test(password) && ptr_lowcase.test(password) && !ptr_upcase.test(password) && ptr_special.test(password))
				|| (ptr_digit.test(password) && ptr_lowcase.test(password) && ptr_upcase.test(password) && !ptr_special.test(password))){
		    if($("#firstName").val() != "" && $("#lastName").val() != ""){
		        var email = $("#email").val();
		        if(reg.test(email)){
                    var phone = $("#call").val();
                    if((/^1[3456789]\d{9}$/.test(phone))){
                        if($("#group").combobox("getValue") != ""){
                            if($("#dept_parentId").combobox("getValue") != ""){
								if(flag==1){
									$("#id").val(0);
									saveUser();
								}else{
									updateUser();
								}
                            }else {
                                $.messager.alert("系统系统","请选择部门！");
                                $("#dept_parentId").focus();
                                return;
                            }
                        }else {
                            $.messager.alert("系统系统","请选择职位！");
                            $("#group").focus();
                            return;
                        }
                    }else {
                        $.messager.alert("系统系统","电话号码不符合规范！");
                        $("#call").focus();
                        return;
                    }
                }else {
                    $.messager.alert("系统系统","邮箱不符合规范！");
                    $("#email").focus();
                    return;
                }
            }else {
                $.messager.alert("系统系统","姓名不能为空！");
                $("#firstName").focus();
                return;
            }
		}else{
			$.messager.alert("系统系统","密码至少包含大写字母、小写字母、数字、特殊字符中的三类字符！");
			$("#password").focus();
			return;
		}

	}

	function updateUser() {
		$("#fm").form("submit",{
			url:'${pageContext.request.contextPath}/user/updateUser.action',
			onSubmit:function () {
				return $(this).form("validate");
			},
			success:function (result) {
				var result = eval('('+result+')');
				if(result.exist){
					$.messager.alert("系统系统","用户名已存在，请重新输入！");
					$("#userName").focus();
				}else{
					if(result.success){
						$.messager.alert("系统系统","修改成功！");
						resetValue();
						$("#dlg").dialog("close");
						$("#dg").datagrid("reload");
					}else{
						$.messager.alert("系统系统","修改失败！");
						return;
					}
				}
			}
		});
	}
	
	function saveUser(){
		$("#fm").form("submit",{
			url:'${pageContext.request.contextPath}/user/userSave.action',
			onSubmit:function(){
				return $(this).form("validate");
			},
			success:function(result){
				var result=eval('('+result+')');
				if(result.exist){
					$.messager.alert("系统系统","用户名已存在，请重新输入！");
					$("#userName").focus();
				}else {
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
			}
		});
	}
	
	function resetValue(){
		$("#userName").val("");
		$("#password").val("");
		$("#firstName").val("");
		$("#lastName").val("");
		$("#email").val("");
		$("#call").val("");
		$("#group").combobox("setValue","");
		$("#dept_parentId").combobox("setValue","");
		$("#deptId").combobox("setValue","");
		document.getElementById("div").style.display = "none";
	}
	
	function closeUserDialog(){
		$("#dlg").dialog("close");
		resetValue();
	}

</script>
</head>
<body style="margin: 1px">
<table id="dg" title="用户管理" class="easyui-datagrid"
  fitColumns="true" pagination="true" rownumbers="true"
  url="${pageContext.request.contextPath}/user/userPage.action" fit="true" toolbar="#tb">
 <thead>
 	<tr>
 		<th field="cb" checkbox="true" align="center"></th>
 		<th field="id" width="30" align="center">用户编号</th>
		<th field="userName" width="80" align="center">用户名</th>
 		<th field="password" width="80" align="center">密码</th>
 		<th field="firstName" width="50" align="center">姓</th>
 		<th field="lastName" width="50" align="center">名</th>
		<th field="call" width="80" align="center">电话</th>
 		<th field="email" width="80" align="center">邮箱</th>
		<th field="group" width="80" align="center">角色</th>
		<th field="deptName" width="80" align="center">所属部门</th>
		<th field="time" width="80" align="center">注册时间</th>
 	</tr>
 </thead>
</table>
<div id="tb">
 <div>
	<a href="javascript:openUserAddDiglog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
	<a href="javascript:openUserModifyDiglog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
	<a href="javascript:deleteUser()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
 </div>
 <div>
 	&nbsp;用户名&nbsp;<input type="text" id="s_id" size="20" onkeydown="if(event.keyCode==13) searchUser()"/>
 	<a href="javascript:searchUser()" class="easyui-linkbutton" iconCls="icon-search" plain="true">搜索</a>
 </div>
</div>

<div id="dlg" class="easyui-dialog" style="width: 620px;height: 350px;padding: 10px 20px" closed="true" buttons="#dlg-buttons">
 
 	<form id="fm" method="post">
 		<table cellpadding="8px">
 			<tr>
 				<td>用户名：</td>
 				<td>
                    <input type="hidden" id="id" name="id"/>
 					<input type="text" id="userName" name="userName" class="easyui-validatebox" required="true"/>
 				</td>
 				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 				<td>密码：</td>
 				<td>
 					<input type="text" id="password" name="password" class="easyui-validatebox" required="true"/>
 				</td>
 			</tr>
 			<tr>
 				<td>姓：</td>
 				<td>
 					<input type="text" id="firstName" name="firstName" class="easyui-validatebox" required="true"/>
 				</td>
 				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
 				<td>名：</td>
 				<td>
 					<input type="text" id="lastName" name="lastName" class="easyui-validatebox" required="true"/>
 				</td>
 			</tr>
 			<tr>
 				<td>邮箱：</td>
				<td>
					<input type="text" id="email" name="email" class="easyui-validatebox" validType="email" required="true"/>
				</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>电话：</td>
				<td>
					<input type="text" id="call" name="call" class="easyui-validatebox" validType="call" required="true"/>
				</td>
 				<td colspan="4">
 					<input type="hidden" id="flag" name="flag"/>
 				</td>
 			</tr>
			<tr>
				<td>职位：</td>
				<td>
					<input  id="group" name="group" class="easyui-combobox"  required="true"/>
				</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>部门：</td>
				<td>
					<input  id="dept_parentId" name="dept_parentId" class="easyui-combobox"  required="false"/>
				</td>
			</tr>
			<tr>
				<td id="td"></td>
				<td>
					<div id="div" style="width: auto; display: none;" >
						<input  id="deptId" name="deptId" class="easyui-combobox" data-options="prompt:'请选择部门'" required="false"/>
					</div>
				</td>
			</tr>
 		</table>
 	</form>
 
</div>

<div id="dlg-buttons">
	<a href="javascript:checkData()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
	<a href="javascript:closeUserDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>
</body>
</html>