<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="ownerUri" name="ownerUri" />
			<input type="hidden" id="permission" name="permission" />
			<div class="custtop">
				
			</div>
			
			<div class="custbottom">
				<span class="custbottom_title">
					权限列表
				</span>
				<ul id="mobileUserRoleMenu"></ul>
			</div>
		</form>
	</div>
	
	<div data-options="region:'south',border:false" style="padding-right: 5px;padding-bottom: 5px;text-align: right;">
		<a id="saveBtn" class="easyui-linkbutton" data-options="iconCls:'icon-save'" href="javascript:;" onclick="save()">发送</a>
		<a id="cancelBtn" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:;" onclick="cancel()">关闭</a>
	</div>
</div>

<script type="text/javascript">
	$(function() {
		$("#saveBtn").linkbutton({
			plain:false
		});
		
		$("#cancelBtnId").linkbutton({
			plain:false
		});
		
		$("#tableForm").form({
			url : "",
			onSubmit : function() {
				var nodes = window.parent.$("#mobileUserRoleMenu").tree('getChecked');
				var permission = "";
				
				if(nodes != null && nodes != ""){
					$.each(nodes, function(i, node) {
						if(nodes.length - 1 == i){
							permission += node.attributes.code;						
						}else{
							permission += node.attributes.code + ",";
						}
					});
				}
				
				window.parent.$("#permission").val(permission);
			
				var flag = $(this).form('validate');
				if (flag) {
					var permission = window.parent.$("#permission").val();
					if(permission == null || permission == ""){
						window.parent.$.messager.alert("错误信息","请选择权限",'error');
						flag = false;
					}else{
						flag = true;
					}
				}
				return flag;
			},
			success : function(data) {
				var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
				var frameId = "iframeId_" + tabId;
				var currentFrames = window.frames[frameId];
			
				currentFrames.sendMobileControl();
			}
		});
	});
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		$("#mobileUserModalWindow").window("destroy");
	}
</script>
