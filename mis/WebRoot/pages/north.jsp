<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/north.css">

<script type="text/javascript">
	$(function(){
		$.ajax({  
		    type: "POST",
		    url: "${pageContext.request.contextPath}/user.do?method=getusernamebysession",  
		    dataType: "json",
		    success: function(data){
		    	$("#welcome_username").html(data.userName);
		    }, 
	        error: function (XMLHttpRequest, data, errorThrown) { 
	            if(XMLHttpRequest.readyState > 1){
	        		$.messager.alert('提示','服务器未知错误');
	            }
	    	} 
		});
	});

	function logon(){
		$.ajax({  
		    type: "POST",
		    url: "${pageContext.request.contextPath}/user.do?method=logon",  
		    dataType: "json",
		    success: function(data){
		    	if(data.message == "200"){
		    		location.href = "${pageContext.request.contextPath}/pages/login.jsp";
		    	}
		    }, 
	        error: function (XMLHttpRequest, data, errorThrown) { 
	            if(XMLHttpRequest.readyState > 1){
	        		$.messager.alert('提示','服务器未知错误');
	            }
	    	} 
		});
	}
	
	function changePassword(){
		$("#modalWindow").window({  
	        title:'修改密码',  
	        href:"${pageContext.request.contextPath}/pages/updatepasswod.jsp",  
	        width:300,  
	        height:200,
	        onLoad: function(){
				$('#messigeText').text("");
	        }  
      	}); 
	}
</script>

<div id="northcontent">
	<div class="northcontent_left">
	</div>
	<div class="northcontent_right">
		<div class="northcontent_right_welcome">
			您好&nbsp;&nbsp;,&nbsp;&nbsp;[<span id="welcome_username"></span>]&nbsp;&nbsp;,&nbsp;&nbsp;欢迎使用&nbsp;&nbsp;诺维-MIS
		</div>
		<div class="northcontent_right_toolbar">
			<a href="javascript:;" class="easyui-menubutton" data-options="menu:'#northcontent_right_toolbar_items',iconCls:'icon-edit'">控制面板</a>
			<div id="northcontent_right_toolbar_items">
				<div data-options="iconCls:'icon-undo'" onclick="changePassword()">修改密码</div>
				<div data-options="iconCls:'icon-redo'" onclick="logon()">退出登录</div>
			</div>
		</div>
	</div>
</div>