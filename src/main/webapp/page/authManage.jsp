<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>组管理</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript">
		function deleteGroup(){
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
					$.post("${pageContext.request.contextPath}/depart/deleteDepart.action",{ids:ids},function(result){
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


		function openGroupAddDiglog(){
			$("#dlg").dialog("open").dialog("setTitle","添加角色信息");
			$("#flag").val(1);
		}

		function openGroupModifyDiglog(){
			var selectRows=$("#dg").datagrid("getSelections");
			if(selectRows.length!=1){
				$.messager.alert("系统提示","请选择一条要编辑的数据！");
				return;
			}
			var row=selectRows[0];
			$("#dlg").dialog("open").dialog("setTitle","编辑角色信息");
			$("#fm").form("load",row);
			$("#flag").val(2);
		}


		function checkData(){
			if($("#name").val()===''){
				$.messager.alert("系统系统","请输入组名！");
				$("#name").focus();
				return;
			}
			if($("#parent_id").combobox("getValue")=="请选择"){
				$.messager.alert("系统系统","请选择上级部门！");
				$("#name").focus();
				return;
			}
			var flag=$("#flag").val();
			$.post("${pageContext.request.contextPath}/depart/getDepartByName.do",{name:$("#name").val(),parentId:$("#parent_id").combobox("getValue")},function(result){
				if(result.exist){
					$.messager.alert("系统系统","该组已存在，请更换下！");
					$("#name").focus();
				}else{
					if(flag == 1){
						$("#id").val(0);
						saveGroup();
					}else {
						updateGroup();
					}
				}
			},"json");

		}

		function saveGroup(){
			$("#fm").form("submit",{
				url:'${pageContext.request.contextPath}/depart/departSave.action',
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

		function updateGroup(){
			$("#fm").form("submit",{
				url:'${pageContext.request.contextPath}/depart/updateDepart.action',
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
			$("#name").val("");
			$("#parent_id").val("");
		}

		function closeGroupDialog(){
			$("#dlg").dialog("close");
			resetValue();
		}

	</script>
</head>
<body style="margin: 1px">
<table id="dg" title="组管理" class="easyui-datagrid"
	   fitColumns="true" pagination="true" rownumbers="true"
	   url="${pageContext.request.contextPath}/depart/getGroup.action?" fit="true" toolbar="#tb">
	<thead>
	<tr>
		<th field="cb" checkbox="true" align="center"></th>
		<th field="id" width="80" align="center">组编号</th>
		<th field="name" width="80" align="center">组名</th>
		<th field="parent_name" width="80" align="center">上级部门</th>
	</tr>
	</thead>
</table>
<div id="tb">
	<div>
		<a href="javascript:openGroupAddDiglog()" class="easyui-linkbutton" iconCls="icon-add" plain="true">添加</a>
		<a href="javascript:openGroupModifyDiglog()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
		<a href="javascript:deleteGroup()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
	</div>
</div>

<div id="dlg" class="easyui-dialog" style="width: 600px;height: 150px;padding: 10px 20px" closed="true" buttons="#dlg-buttons">

	<form id="fm" method="post">
		<table cellpadding="8px">
			<tr>
				<td>组名：</td>
				<td>
					<input type="hidden" id="id" name="id"/>
					<input type="text" id="name" name="name" class="easyui-validatebox" required="true"/>
				</td>
				<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td>上级部门：</td>
				<td>
					<input  id="parent_id" name="parent_id" class="easyui-combobox" data-options="panelHeight:'auto',valueField:'id',textField:'name',url:'${pageContext.request.contextPath}/depart/getDepart.action'" value="请选择"/>
					<input type="hidden" id="flag" name="flag"/>
				</td>
			</tr>
		</table>
	</form>

</div>

<div id="dlg-buttons">
	<a href="javascript:checkData()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
	<a href="javascript:closeGroupDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>
</body>
</html>