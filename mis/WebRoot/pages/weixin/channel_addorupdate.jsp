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
	    
	    $("#channelHasChildren").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=WEIXINCHANNELCHANNELHASCHILDREN",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
	    $("#channelLevelCode").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=WEIXINCHANNELCHANNELLEVELCODE",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
	    $("#type").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=WEIXINCHANNELTYPE",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<ul>
				<%if(op.equals("add")){ %>
				<li>
					<span class="textlable">频道编码：</span>
					<span class="inputlable">
						<input id="channelCodeName" name="channelCodeName" class="easyui-validatebox" data-options="required:true,validType:{
							length:[0,40],
							remote:['${ctx}/weiXinChannel.do?method=isExist','channelCodeName']
						},invalidMessage:'频道编码已经存在'" />
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
					<span class="textlable">包含子：</span>
					<span class="inputlable">
						<input id="channelHasChildren" name="channelHasChildren" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">类型：</span>
					<span class="inputlable">
						<input id="channelLevelCode" name="channelLevelCode" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">父频道：</span>
					<span class="inputlable">
						<input id="channelParentCode" name="channelParentCode" style="width: 120px;" />
						<a href="javascript:;" onclick="setEmptyChannelParentCode()">设置空</a>
					</span>
				</li>
				
				<li>
					<span class="textlable">顺序：</span>
					<span class="inputlable">
						<input id="displaySort" name="displaySort" class="easyui-numberspinner" style="width: 150px; border: 0px;" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">频道类型：</span>
					<span class="inputlable">
						<input id="type" name="type" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">类型值：</span>
					<span class="inputlable">
						<input id="typeValue" name="typeValue" data-options="required:true" />
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
		$("#weiXinChannelModalWindow").window("destroy");
	}
	
	function setEmptyChannelParentCode(){
		window.parent.$("#channelParentCode").combotree("setValue","");
	}
</script>
