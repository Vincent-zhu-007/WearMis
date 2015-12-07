<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<style type="text/css">
	#foot{
		color: #1F5BAC;
		text-align: center;
		line-height: 27px;
	}
</style>

<script type="text/javascript">
	$(function(){
		var date = new Date();
		$("#foot").html('诺维信息    ©' + date.getFullYear() + '  NuoWei All Rights Reserved');
	});
</script>
<div id="footer">
	<div id="foot"></div>
</div>