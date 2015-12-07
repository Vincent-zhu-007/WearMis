<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String op=(String)request.getParameter("op"); %>

<script type="text/javascript">
	$("#status").combobox({
	    url:"${ctx}/code.do?method=getcodecache&category=STATUS",
	    valueField:'id',
	    textField:'text',
		required:true,
		editable:false
    });
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<ul style="width: 280px;">
				<%if(op.equals("add")){ %>
				<li>
					<span class="textlable">编码类型：</span>
					<span class="inputlable">
						<input name="category" id="category" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">编码名称：</span>
					<span class="inputlable">
						<input id="codeName" name="codeName" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				<%} %>
				
				<li>
					<span class="textlable">描述：</span>
					<span class="inputlable">
						<input type="text" id="description" name="description" />
					</span>
				</li>
				
				<li>
					<span class="textlable">状态：</span>
					<span class="inputlable">
						<input id="status" name="status" data-options="required:true" />
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
					var category = $("#category").val();
					var codeName = $("#codeName").val();
					$.ajax({
						type:"POST",
				        url:"${ctx}/code.do?method=isExist",
				        data: "codeName="+codeName+"&category="+category+"",
				        async: false,
				        success: function(data){
				        	if(!data){
				        		window.parent.$.messager.alert("错误信息","此类型编码已经存在",'error');
				        		flag = false;
				        	}
				        }
					});
				}
				return flag;
			},
			success : function(data) {
				var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
				var frameId = "iframeId_" + tabId;
				var currentFrames = window.frames[frameId];
			
				currentFrames.addOrUpdate();
			}
		});
	});
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		$("#codeModalWindow").window("destroy");
	}
</script>
