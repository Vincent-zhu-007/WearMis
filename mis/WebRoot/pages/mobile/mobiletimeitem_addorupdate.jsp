<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String mobileTimeId=(String)request.getParameter("mobileTimeId"); %>
<% String op=(String)request.getParameter("op"); %>

<script type="text/javascript">
	$(function(){
	    $("#level").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=MOBILETIMEITEMLEVEL",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
		$("#startTime").timespinner({
			showSeconds:true
		});
		
		$("#endTime").timespinner({
			showSeconds:true
		});
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<input type="hidden" id="mobileTimeId" name="mobileTimeId" value="<%=mobileTimeId%>" />
			<ul>
				<li>
					<span class="textlable">开始时间：</span>
					<span class="inputlable">
						<input id="startTime" name="startTime" data-options="required:true" style="border: 0px; width: 150px;" />
					</span>
				</li>
				
				<li>
					<span class="textlable">结束时间：</span>
					<span class="inputlable">
						<input id="endTime" name="endTime" data-options="required:true" style="border: 0px; width: 150px;" />
					</span>
				</li>
				
				<li>
					<span class="textlable">级别：</span>
					<span class="inputlable">
						<input id="level" name="level" data-options="required:true" />
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
                	
				}
				return flag;
			},
			success : function(data) {
				var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
				var frameId = "iframeId_" + tabId;
				var currentFrames = window.frames[frameId];
				currentFrames.mobileTimeItemAddOrUpdate();
			}
		});
	});
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		$("#mobileTimeItemModalWindow").window("destroy");
	}
</script>
