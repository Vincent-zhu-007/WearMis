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
	    
	    $("#listType").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=MOBILECONTACTLISTTYPE",
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
			<input type="hidden" id="mobileContactMembers" name="mobileContactMembers" />
			<div class="custtop">
				<ul>
					<%if(op.equals("add")){ %>
					<li>
						<span class="textlable">用户地址：</span>
						<span class="inputlable">
							<input id="ownerUri" name="ownerUri" class="easyui-validatebox" data-options="required:true,validType:{
								length:[0,50],
								remote:['${ctx}/mobileContact.do?method=ownermobilecontactisexist','ownerUri']
							},invalidMessage:'用户联系人列表已经存在'" />
						</span>
					</li>
					<%} %>
					
					<li>
						<span class="textlable">类型：</span>
						<span class="inputlable">
							<input id="listType" name="listType" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">姓名1：</span>
						<span class="inputlable">
							<input id="displayName1" name="displayName1" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">短号1：</span>
						<span class="inputlable">
							<input id="shortNum1" name="shortNum1" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">号码1：</span>
						<span class="inputlable">
							<input id="mobilePhone1" name="mobilePhone1" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">姓名2：</span>
						<span class="inputlable">
							<input id="displayName2" name="displayName2" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">短号2：</span>
						<span class="inputlable">
							<input id="shortNum2" name="shortNum2" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">号码2：</span>
						<span class="inputlable">
							<input id="mobilePhone2" name="mobilePhone2" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">姓名3：</span>
						<span class="inputlable">
							<input id="displayName3" name="displayName3" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">短号3：</span>
						<span class="inputlable">
							<input id="shortNum3" name="shortNum3" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">号码3：</span>
						<span class="inputlable">
							<input id="mobilePhone3" name="mobilePhone3" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">姓名4：</span>
						<span class="inputlable">
							<input id="displayName4" name="displayName4" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">短号4：</span>
						<span class="inputlable">
							<input id="shortNum4" name="shortNum4" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">号码4：</span>
						<span class="inputlable">
							<input id="mobilePhone4" name="mobilePhone4" data-options="required:true" />
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
		$("#mobileContactModalWindow").window("destroy");
	}
</script>
