<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style type="text/css">
	#btnImageUpload{
		width: 60px;
		font:12px/18px "微软雅黑","宋体","黑体",Arial;
	}
</style>

<script type="text/javascript">
	$(function(){
	    $("#status").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=STATUS",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
	    $("#verCategory").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=MOBILEVERSIONCATEGORY",
		    valueField:'id',
		    textField:'text',
		    required:true,
			editable:false
	    });
	    
	    $("#isMandatoryUpdate").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=MOBILEVERSIONISMANDATORYUPDATE",
		    valueField:'id',
		    textField:'text',
		    required:true,
			editable:false
	    });
	    
	    var editor = KindEditor.editor({
			uploadJson : '${ctx}/resources/kindeditor/jsp/upload_json.jsp',
			fileManagerJson : '${ctx}/resources/kindeditor/jsp/file_manager_json.jsp',
			allowFileManager : true
		});
		
		$('#btnImageUpload').click(function() {
			editor.loadPlugin('insertfile', function() {
				editor.plugin.fileDialog({
					fileUrl : $('#verFileAddress').val(),
					clickFn : function(url, title, width, height, border, align) {
						$('#verFileAddress').val(url);
						editor.hideDialog();
					}
				});
			});
		});
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<ul>
				
				<li>
					<span class="textlable">名称：</span>
					<span class="inputlable">
						<input id="name" name="name" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">展示版本：</span>
					<span class="inputlable">
						<input type="text" id="verNo" name="verNo" class="easyui-validatebox" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">适用类型：</span>
					<span class="inputlable">
						<input id="verCategory" name="verCategory" />
					</span>
				</li>
				
				<li>
					<span class="textlable">版本号：</span>
					<span class="inputlable">
						<input id="verSort" name="verSort" class="easyui-numberspinner" data-options="required:true, min: 1, max: 100" style="border: 0px;" />
					</span>
				</li>
				
				<li>
					<span class="textlable">是否强制：</span>
					<span class="inputlable">
						<input id="isMandatoryUpdate" name="isMandatoryUpdate" />
					</span>
				</li>
				
				<li>
					<span class="textlable">状态：</span>
					<span class="inputlable">
						<input id="status" name="status" data-options="required:true" />
					</span>
				</li>
				
				<li>
					<span class="textlable">上传文件：</span>
					<span class="inputlable">
						<input type="text" id="verFileAddress" name="verFileAddress" />
						<input type="button" id="btnImageUpload" value="选择文件" />
					</span>
				</li>
				
				<li style="height: 120px;">
					<span class="textlable">备注：</span>
					<span class="inputlable">
						<textarea id="verRemark" name="verRemark" style="width: 380px; height: 115px;"></textarea>
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
		$("#mobileVersionConfigModalWindow").window("destroy");
	}
</script>
