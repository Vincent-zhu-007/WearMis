<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="taglib.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>mis-login</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" type="text/css" href="${ctx}/resources/css/login.css">
	<script type="text/javascript" src="${ctx}/resources/custjs/pagemessage.js"></script>
	
	<script type="text/javascript">
		$(function(){
			checkParent();
		
			var date = new Date();
			$("#loginfoot").html('诺维信息    ©' + date.getFullYear() + '  NuoWei All Rights Reserved');
			
			$("#loginForm").form({
				url : "",
				onSubmit : function() {
					var flag = $(this).form('validate');
					if (flag) {
						
					}
					return flag;
				},
				success : function(data) {
					login();
				}
			});
			
			$("#loginmain_btn_btnlogin").keyup(function (event) {
	            if (event.keyCode == 13) {
	                login();
	                return false;
	            }
	        });
			
		});
		
		function login() {
			var url = "${ctx}/login.do?method=login";
			var formData = window.parent.$("#loginForm").serializeArray();
			
			$.post(url, formData, function(data) {
				if (data.message == "200") {
					actionSuccess();
					location.href = "main.jsp";
					return;
				} else {
					window.parent.$.messager.alert("错误信息",data.message,'error');
				}
			});
		}
		
		function bindEnter(obj){
			var loginBtn = $("#loginmain_btn_btnlogin");
			if(obj.keyCode == 13){
				loginBtn.click();         
				obj.returnValue = false;
			} 
		}
		
		function checkParent(){
			if(window != top || window.parent != top){
				top.location.href = location.href;
			}
		}
	</script>
	
  </head>
  
  <body style="background-color: #f0f0f0; text-align:center;" onkeypress="bindEnter(event)">
  	<div id="loginmain">
  		<form id="loginForm" name="loginForm">
	  		<span class="loginmain_title">诺维可穿戴设备智能管理平台</span>
	  		<div id="loginFormContent">
		    	<ul>
		    		<li class="usernameli">
						<input name="userName" id="userName" class="easyui-validatebox" missingMessage="请输入用户名" data-options="required:true" />
		    		</li>
		    		<li class="passwordli">
						<input type="password" name="password" class="easyui-validatebox" missingMessage="请输入密码" id="password" data-options="required:true" />
		    		</li>
		    	</ul>
	    	</div>
	    	
	    	<span class="loginmain_btn">
	    		<font>请使用IE10或以上浏览器或兼容HTML5 CSS3的浏览器</font>
	    		<input type="button" id="loginmain_btn_btnlogin" class="loginmain_btn_btnlogin" value="登录" onclick="login()" />
			</span>
    	</form>
  	</div>
  	
  	<div id="loginfoot"></div>
  
  </body>
</html>
