<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String op=(String)request.getParameter("op"); %>

<style type="text/css">
	#btnFileUpload{
		width: 60px;
		font:12px/18px "微软雅黑","宋体","黑体",Arial;
	}
	
	.custbottom_left{
		float: left;
		width: 202px;
		height: 202px;
		padding-left: 12px;
	}
	
	.custbottom_middle{
		float: left;
		width: 27px;
		height: 187px;
		padding-left: 10px;
		padding-right: 10px;
		padding-top: 13px;
	}
	
	.custbottom_right{
		float: left;
		width: 202px;
		height: 202px;
	}
	
	.multiple{
		width: 200px;
		height: 200px;
		border: 1px solid #95b8e7;
	}
	
	.multiple option{
		font:12px/18px "微软雅黑","宋体","黑体",Arial;
	}
	
	.btn_multiple{
		width: 27px;
		height: 27px;
		margin-top: 13px;
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
	    
	    var editor = KindEditor.editor({
			uploadJson : '${ctx}/resources/kindeditor/jsp/upload_json.jsp',
			fileManagerJson : '${ctx}/resources/kindeditor/jsp/file_manager_json.jsp',
			allowFileManager : true
		});
		
		$('#btnFileUpload').click(function() {
			editor.loadPlugin('insertfile', function() {
				editor.plugin.fileDialog({
					fileUrl : $('#fileUrl').val(),
					clickFn : function(url, title, width, height, border, align) {
						$('#fileUrl').val(url);
						editor.hideDialog();
					}
				});
			});
		});
		
		/* 添加项[向右移单项移动] */
        multipleAddClick = function () {
            var $options = $('#listboxAdd option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxRemove");
        };
        
		/* 添加项[向右移全部移动] */
        multipleAddAllClick = function () {
            var $options = $('#listboxAdd option');
            var $remove = $options.remove();
            $remove.appendTo("#listboxRemove");
        };

		/* 移除项[向左移单项移动] */
        multipleDelClick = function () {
            $('#listboxRemove option:selected').appendTo('#listboxAdd');
        };
        
		/* 移除项[向左移单项移动] */
        multipleDelAllClick = function () {
            $('#listboxRemove option').appendTo('#listboxAdd');
        };
        
		/* 双击选项 */
        $('#listboxAdd').dblclick(function () { /* 绑定双击事件 */
			/* 获取全部的选项,删除并追加给对方 */
            var $options = $('#listboxAdd option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxRemove");
        });
        
		/* 双击选项 */
        $('#listboxRemove').dblclick(function () {
            var $options = $('#listboxRemove option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxAdd");
        });
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<input type="hidden" id="targetMemberUri" name="targetMemberUri" />
			<div class="custtop">
				<ul>
					<%if(op.equals("add")){ %>
					<li>
						<span class="textlable">用户地址：</span>
						<span class="inputlable">
							<input id="ownerUri" name="ownerUri" class="easyui-validatebox" data-options="required:true,validType:{
								length:[0,50],
								remote:['${ctx}/mobileUser.do?method=ownerUriIsNotExist','ownerUri']
							},invalidMessage:'用户地址不存在'" />
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
						<span class="textlable">文件大小：</span>
						<span class="inputlable">
							<input type="text" id="fileSize" name="fileSize" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">状态：</span>
						<span class="inputlable">
							<input id="status" name="status" data-options="required:true" />
						</span>
					</li>
					
					<%if(op.equals("add")){ %>
					<li>
						<span class="textlable">文件地址：</span>
						<span class="inputlable">
							<input type="text" id="fileUrl" name="fileUrl" />
							<input type="button" id="btnFileUpload" value="选择文件" />
						</span>
					</li>
					<%} %>
				</ul>
			</div>
			
			<div class="custbottom">
				<span class="custbottom_title">
					用户列表
				</span>
				<div class="custbottom_left">
					<select id="listboxRemove" multiple="multiple" name="listboxRemove" class="multiple"></select>
				</div>
				<div class="custbottom_middle">
					<input type="button" id="multiple_del" value=">" onclick="multipleDelClick();" class="btn_multiple" />
                    <input type="button" id="multiple_del_all" value=">>" onclick="multipleDelAllClick();" class="btn_multiple" />
					<input type="button" id="multiple_add" value="<" onclick="multipleAddClick();" class="btn_multiple" />
                    <input type="button" id="multiple_add_all" value="<<" onclick="multipleAddAllClick();" class="btn_multiple" />
				</div>
				<div class="custbottom_right">
					<select id="listboxAdd" multiple="multiple" name="listboxAdd" class="multiple"></select>
				</div>
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
		
		window.parent.$("#ownerUri").change(function(){
			var targetOwnerUri = window.parent.$("#ownerUri").val();
			var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
			var frameId = "iframeId_" + tabId;
			var currentFrames = window.frames[frameId];
			currentFrames.initListboxRemove(targetOwnerUri);
		});
		
		$("#tableForm").form({
			url : "",
			onSubmit : function() {
				var targetMemberUri = "";
				var size = window.parent.$("#listboxAdd option").size();
				if (size > 0){
					 $.each($("#listboxAdd option"), function (i, own) {
					 
                        var text = $(own).val();
                        if(size - 1 == i){
                        	targetMemberUri += text;
                        }else{
                        	targetMemberUri += text + ",";
                        }
                    });
				}
				
				window.parent.$("#targetMemberUri").val(targetMemberUri);
			
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
		$("#mobileUserFileModalWindow").window("destroy");
	}
</script>
