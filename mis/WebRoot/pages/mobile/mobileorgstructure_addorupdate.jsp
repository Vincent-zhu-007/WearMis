<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String companyId=(String)request.getParameter("companyid"); %>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		<ul id="orgStructureTree"></ul>
		
	    <div id="mm" class="easyui-menu" style="width: 120px;">
	        <div onclick="openAppendOrgStructure()" iconcls="icon-add">添加节点</div>
	        <div onclick="removeOrgStructure()" iconcls="icon-remove">删除节点</div>
	        <div onclick="openUpdateOrgStructure()" iconcls="icon-edit">修改节点</div>
	    </div>
	    
	</div>
	
	<div data-options="region:'south',border:false" style="padding-right: 5px;padding-bottom: 5px;text-align: right;">
	</div>
</div>

<div id="orgStructureModalWindowContain" modal="true" shadow="false" minimizable="false" cache="false" maximizable="false" collapsible="false" resizable="false" style="margin: 0px;padding: 0px;overflow: auto;">
</div>

<script type="text/javascript">
	var operate = "";
	
	$(function() {
		$("#cancelBtnId").linkbutton({
			plain:false
		});
		
		initTreeData();
	});
	
	function initTreeData() {
		$('#orgStructureTree').tree({
			url:'${ctx}/mobileOrgStructure.do?method=inittreedata&companyid=<%=companyId%>',
			checkbox:false,
			onClick:function(node){
				$(this).tree('toggle', node.target);
			},
			onContextMenu: function(e, node){  
				e.preventDefault();  
				$('#orgStructureTree').tree('select', node.target);  
				$('#mm').menu('show', {  
					left: e.pageX,  
					top: e.pageY  
				});  
			}  
		});
	}
	
	function openAppendOrgStructure(){
		operate = "add";
	
		var node = $('#orgStructureTree').tree('getSelected');
		
		var id = node.attributes.id;
		
		$("#orgStructureModalWindowContain").html("<div id=\"orgStructureModalWindowContainEdit\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		$("#orgStructureModalWindowContainEdit").window({
			title : '新增',
			href : "${ctx}/pages/mobile/mobileorgstructurenode_addorupdate.jsp",
			width : 300,
			height : 100,
			onClose: function () {
				window.parent.$("#orgStructureModalWindowContainEdit").window('destroy');
			},
			onLoad : function() {
				$.ajax({
					type : "post",
					url : "${ctx}/mobileOrgStructure.do?method=initnode",
					data : {
						"id" : id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						window.parent.$('#tableForm').form('load', {
							id:data.id,
							orgStructureId:data.orgStructureId,
							codeName:data.codeName,
							description:data.description,
							hasChildren:data.hasChildren,
							displaySort:data.displaySort,
							levelNum:data.levelNum,
							parentCode:data.parentCode,
							status:data.status
						});
					},
					error : function(XMLHttpRequest, data, errorThrown) {
						 actionFailure();
					}
				});
			}
		});
	}
	
	function appendOrgStructure(id, text, attributes) {
		var node = $('#orgStructureTree').tree('getSelected');
		
        $('#orgStructureTree').tree('append',{
            parent: (node?node.target:null),
            data:[{
            	checked:false,
            	id:id,
	            text:text,
	            attributes:attributes
			}]
        });
	}
	
	function openUpdateOrgStructure(){
		operate = "update";
		
		var node = $('#orgStructureTree').tree('getSelected');
		var id = node.attributes.id;
		
		var levelNum = node.attributes.levelNum;
		
		if(levelNum == 1){
			window.parent.$.messager.alert("错误信息","该节点不允许编辑",'error');
			return false;
		}
		
		$("#orgStructureModalWindowContain").html("<div id=\"orgStructureModalWindowContainEdit\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		$("#orgStructureModalWindowContainEdit").window({
			title : '编辑',
			href : "${ctx}/pages/mobile/mobileorgstructurenode_addorupdate.jsp",
			width : 300,
			height : 100,
			onClose: function () {
				window.parent.$("#orgStructureModalWindowContainEdit").window('destroy');
			},
			onLoad : function() {
				$.ajax({
					type : "post",
					url : "${ctx}/mobileOrgStructure.do?method=initupdatenode",
					data : {
						"id" : id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						window.parent.$('#tableForm').form('load', {
							id:data.id,
							orgStructureId:data.orgStructureId,
							codeName:data.codeName,
							description:data.description,
							hasChildren:data.hasChildren,
							displaySort:data.displaySort,
							levelNum:data.levelNum,
							parentCode:data.parentCode,
							status:data.status
						});
					},
					error : function(XMLHttpRequest, data, errorThrown) {
						 window.parent.$.messager.alert("错误信息","操作失败！请与系统管理员联系。",'error');
					}
				});
			}
		});
	}
	
	function updateOrgStructure(text, attributes) {
		var node = $('#orgStructureTree').tree('getSelected');
		
		if (node) {
			node.text = text;
			node.attributes = attributes;
            $('#orgStructureTree').tree('update', node);
        }
	}
	
	function addOrUpdateOrgStructureNode() {
		var url = "";
		if (operate == "add") {
			url = "${ctx}/mobileOrgStructure.do?method=addnode";
		} else if (operate == "update") {
			url = "${ctx}/mobileOrgStructure.do?method=updatenode";
		}
		
		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				window.parent.$.messager.show( {
					title : "提示信息",
					msg : "操作成功！",
					timeout : 3000,
					showType : 'show'
				});
				
				$("#orgStructureModalWindowContainEdit").window("destroy");
				
				var id = data.codeName;
				var text = data.description;
				var attributes = data.attributes;
				
				if(operate == "add"){
					appendOrgStructure(id, text, attributes);	
				}else if (operate == "update") {
					updateOrgStructure(text, attributes);
				}
			} else {
				window.parent.$.messager.alert("错误信息","操作失败！请与系统管理员联系。",'error');
			}
		});
	}
	
	function removeOrgStructure() {
        var node = $('#orgStructureTree').tree('getSelected');
        var ids = node.attributes.id;
        
        var levelNum = node.attributes.levelNum;
		
		if(levelNum == 1){
			window.parent.$.messager.alert("错误信息","该节点不允许删除",'error');
			return false;
		}
        
        window.parent.$.messager.confirm("确认", "是否确认删除", function(r) {
			if(r){
				$.post("${ctx}/mobileOrgStructure.do?method=delete", {ids:ids}, function(data) {
					if (data.message == "200") {
						$('#orgStructureTree').tree('remove', node.target);
					
						window.parent.$.messager.show( {
							title : "提示信息",
							msg : "操作成功！",
							timeout : 3000,
							showType : 'show'
						});
					} else {
						window.parent.$.messager.alert("错误信息","操作失败！请与系统管理员联系。",'error');
					}
				});
			}else{
				return false;
			}
    	});
	}
</script>
