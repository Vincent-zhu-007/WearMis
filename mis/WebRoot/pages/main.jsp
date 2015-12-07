<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="taglib.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="x-ua-compatible" content="ie=7" />
	<title>Mis</title>
	
	<script type="text/javascript">
		$(function(){
			/* loadBaiDuMapScript(); */
		});
	
		function loadBaiDuMapScript() {
			var script = document.createElement("script");
			script.src = "http://api.map.baidu.com/api?v=2.0&ak=XL9I0GDfqGsiU3pGMjeF0Mnx&callback=initialize";
			document.body.appendChild(script);
		}
	</script>
</head>
<body class="easyui-layout">
	<!-- top -->
	<div data-options="region:'north',href:'${ctx}/pages/north.jsp'" style="height: 56px;overflow: hidden;" class="logo">
	</div>
	
	<!-- menu -->
	<div data-options="region:'west',title:'信息管理中心',href:'${ctx}/pages/west.jsp'" style="width: 200px;overflow: hidden;">
	</div>
	
	<!-- center -->
	<div data-options="region:'center',href:'${ctx}/pages/center.jsp'" style="overflow: hidden;">
	</div>
	
	<!-- bottom -->
	<div data-options="region:'south',href:'${ctx}/pages/south.jsp'" style="height: 30px;overflow: hidden;">
	</div>
	
	<!-- 新增，编辑 使用的弹出层 -->
	<div id="modalWindow" style="padding: 0px; margin: 0px;">
	</div>
</body>
</html>

