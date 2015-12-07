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
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<input type="hidden" id="companyId" name="companyId" />
			<div class="custtop">
				<ul>
					<%if(op.equals("add")){ %>
					<li>
						<span class="textlable">公司编码：</span>
						<span class="inputlable">
							<input id="codeName" name="codeName" class="easyui-validatebox" data-options="required:true,validType:{
								length:[0,50],
								remote:['${ctx}/mobileCompany.do?method=isExist','codeName']
							},invalidMessage:'编码已经存在'" />
						</span>
					</li>
					<%} %>
					
					<li>
						<span class="textlable">描述：</span>
						<span class="inputlable">
							<input type="text" id="description" name="description" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">主域名：</span>
						<span class="inputlable">
							<input name="mobileHost" id="mobileHost" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">主端口：</span>
						<span class="inputlable">
							<input name="mobilePort" id="mobilePort" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">主应用：</span>
						<span class="inputlable">
							<input name="mobileAppName" id="mobileAppName" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">rpc域名：</span>
						<span class="inputlable">
							<input name="rpcHost" id="rpcHost" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">rpc端口：</span>
						<span class="inputlable">
							<input name="rpcPort" id="rpcPort" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">rpc应用：</span>
						<span class="inputlable">
							<input name="rpcAppName" id="rpcAppName" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">免压ip1：</span>
						<span class="inputlable">
							<input name="unzipServerIp1" id="unzipServerIp1" />
						</span>
					</li>
					
					<li>
						<span class="textlable">免压ip2：</span>
						<span class="inputlable">
							<input name="unzipServerIp2" id="unzipServerIp2" />
						</span>
					</li>
					
					<li>
						<span class="textlable">架构层数：</span>
						<span class="inputlable">
							<input name="orgStructureLayerNum" id="orgStructureLayerNum" class="easyui-numberspinner" style="width: 156px; border: 0px;" data-options="required:true,min:1,max:5,editable:false" />
						</span>
					</li>
					
					<li>
						<span class="textlable">AppID：</span>
						<span class="inputlable">
							<input name="appID" id="appID" />
						</span>
					</li>
					
					<li>
						<span class="textlable">AppSecret：</span>
						<span class="inputlable">
							<input name="appSecret" id="appSecret" />
						</span>
					</li>
					
					<li>
						<span class="textlable">状态：</span>
						<span class="inputlable">
							<input id="status" name="status" data-options="required:true" />
						</span>
					</li>
				</ul>
			</div>
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
		$("#mobileCompanyModalWindow").window("destroy");
	}
</script>
