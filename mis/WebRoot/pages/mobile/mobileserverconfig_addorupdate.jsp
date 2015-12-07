<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<script type="text/javascript">
	$("#status").combobox({
	    url:"${ctx}/code.do?method=getcodecache&category=STATUS",
	    valueField:'id',
	    textField:'text',
		required:true,
		editable:false
    });
    
    $("#httpMethod").combobox({
	    url:"${ctx}/code.do?method=getcodecache&category=HTTPMETHOD",
	    valueField:'id',
	    textField:'text',
		required:true,
		editable:false
    });
    
    $("#companyCode").combobox({
	    url:"${ctx}/code.do?method=getcodecache&category=MOBILECOMPANY",
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
			<ul style="width:280px;">
				<li>
					<span class="textlable">公司编码：</span>
					<span class="inputlable">
						<input id="companyCode" name="companyCode" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">主机：</span>
					<span class="inputlable">
						<input name="host" id="host" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">端口：</span>
					<span class="inputlable">
						<input name="port" id="port" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">应用名：</span>
					<span class="inputlable">
						<input name="appName" id="appName" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">代理：</span>
					<span class="inputlable">
						<input type="text" id="userAgent" name="userAgent" />
					</span>
				</li>
				
				<li>
					<span class="textlable">响应类型：</span>
					<span class="inputlable">
						<input id="contentType" name="contentType" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">描述：</span>
					<span class="inputlable">
						<input id="description" name="description" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">请求类型：</span>
					<span class="inputlable">
						<input id="httpMethod" name="httpMethod" />
					</span>
				</li>
				
				<li>
					<span class="textlable">第一部分：</span>
					<span class="inputlable">
						<input id="urlPart1" name="urlPart1" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">第二部分：</span>
					<span class="inputlable">
						<input id="urlPart2" name="urlPart2" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">xml模板：</span>
					<span class="inputlable">
						<input id="responseXmlFile" name="responseXmlFile" class="easyui-validatebox" data-options="required:true" />
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
		$("#mobileServerConfigModalWindow").window("destroy");
	}
</script>
