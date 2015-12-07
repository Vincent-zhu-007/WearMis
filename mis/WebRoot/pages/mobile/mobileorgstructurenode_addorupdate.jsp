<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<input type="hidden" id="orgStructureId" name="orgStructureId" />
			<input type="hidden" id="codeName" name="codeName" />
			<input type="hidden" id="hasChildren" name="hasChildren" />
			<input type="hidden" id="displaySort" name="displaySort" />
			<input type="hidden" id="levelNum" name="levelNum" />
			<input type="hidden" id="parentCode" name="parentCode" />
			<input type="hidden" id="status" name="status" />
			<ul>
				<li>
					<span class="textlable">描述：</span>
					<span class="inputlable">
						<input type="text" id="description" name="description" class="easyui-validatebox" data-options="required:true" />
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
				window.parent.addOrUpdateOrgStructureNode();
			}
		});
	});
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		window.parent.$("#orgStructureModalWindowContainEdit").window("destroy");
	}
</script>
