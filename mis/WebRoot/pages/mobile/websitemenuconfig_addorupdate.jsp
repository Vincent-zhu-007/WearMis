<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String op=(String)request.getParameter("op"); %>

<script type="text/javascript">
	$(function(){
		$("#status").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=STATUS",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
	    $("#hasChildren").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=WEBSITEMENUCONFIGHASCHILDREN",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
	    $("#levelCode").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=WEBSITEMENUCONFIGLEVELCODE",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false,
			onSelect:function(record){
				initParentCode(record);
			}
	    });
	});

	function initParentCode(record){
		var levelCode = record.id;
		if(levelCode != null && levelCode != ""){
			$("#parentCode").combobox({
			    url:"${ctx}/webSiteMenuConfig.do?method=initparentcode&levelCode=" + levelCode,
			    valueField:'id',
			    textField:'text',
				required:true,
				editable:false
		    });	
		}
	}
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<ul>
				<%if(op.equals("add")){ %>
				<li>
					<span class="textlable">编码名称：</span>
					<span class="inputlable">
						<input id="code" name="code" class="easyui-validatebox" data-options="required:true,validType:{
							length:[0,50],
							remote:['${ctx}/webSiteMenuConfig.do?method=isExist','code']
						},invalidMessage:'编码已经存在'" />
					</span>
				</li>
				<%} %>
				
				<li>
					<span class="textlable">描述：</span>
					<span class="inputlable">
						<input id="description" name="description" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				<li>
					<span class="textlable">地址：</span>
					<span class="inputlable">
						<input type="text" id="url" name="url" />
					</span>
				</li>
				<li>
					<span class="textlable">是否包含：</span>
					<span class="inputlable">
						<input id="hasChildren" name="hasChildren" data-options="required:true" />
					</span>
				</li>
				<li>
					<span class="textlable">顺序：</span>
					<span class="inputlable">
						<input id="displaySort" name="displaySort" class="easyui-numberspinner" style="width: 156px; border: 0px;" data-options="required:true" />
					</span>
				</li>
				<li>
					<span class="textlable">类型：</span>
					<span class="inputlable">
						<input id="levelCode" name="levelCode" data-options="required:true" />
					</span>
				</li>
				<li>
					<span class="textlable">父编码：</span>
					<span class="inputlable">
						<input id="parentCode" name="parentCode" data-options="required:true" />
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
		$("#webSiteMenuConfigModalWindow").window("destroy");
	}
</script>
