<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<ul style="width: 280px;">
				<li>
					<span class="textlable">旧密码：</span>
					<span class="inputlable">
						<input type="password" name="oldPassword" id="oldPassword" class="easyui-validatebox" data-options="required:true,validType:{
						remote:['${ctx}/user.do?method=passwodiscorrect','password']
						},invalidMessage:'旧密码错误'" />
					</span>
				</li>
				
				<li>
					<span class="textlable">新密码：</span>
					<span class="inputlable">
						<input type="password" id="newPassword" name="newPassword" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">确认密码：</span>
					<span class="inputlable">
						<input type="password" id="rePassword" name="rePassword" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
			</ul>
		</form>
	</div>
	
	<div data-options="region:'south',border:false" style="padding-right: 5px;padding-bottom: 5px;text-align: right;">
		<a id="saveBtn" class="easyui-linkbutton" data-options="iconCls:'icon-save'" href="javascript:;" onclick="save()">保存</a>
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
				var flag = $(this).form('validate');
				if (flag) {
					var newPassword = window.parent.$("#newPassword").val();
					var rePassword = window.parent.$("#rePassword").val();
					
					if(newPassword != rePassword){
						flag = false;
						$.messager.alert("错误信息","新密码与确认密码不一致",'error');
					}else{
						flag = true;
					}
				}
				return flag;
			},
			success : function(data) {
				doChangePassword();
				$("#modalWindow").window("close");
			}
		});
	});
	
	function doChangePassword() {
		var url = "${ctx}/user.do?method=changepassword";
		var newPassword = $("#newPassword").val();
		var formData = {newPassword:newPassword};
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				window.parent.$.messager.alert("错误信息","密码修改成功,请重新登录.");
				window.parent.$("#modalWindow").window("close");
			} else {
				actionFailure();
			}
		});
	}
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		$("#modalWindow").window("close");
	}
</script>