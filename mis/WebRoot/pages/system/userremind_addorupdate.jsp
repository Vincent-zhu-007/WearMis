<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style type="text/css">
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
	
	.inputhtmllable{
		float: left;
		width: 455px;
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
    
	    $("#processTag").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=TARGETUSER",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
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
			<input type="hidden" id="targetUser" name="targetUser" />
			<div class="custtop">
				<ul>
					<li>
						<span class="textlable">标题：</span>
						<span class="inputlable">
							<input name="title" id="title" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">处理：</span>
						<span class="inputlable">
							<input name="processTag" id="processTag" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">状态：</span>
						<span class="inputlable">
							<input id="status" name="status" data-options="required:true" />
						</span>
					</li>
					
					<li style="height: 120px;">
						<span class="textlable">内容：</span>
						<span class="inputlable">
							<textarea id="content" name="content" style="width: 200px;"></textarea>
						</span>
					</li>
				</ul>
			</div>
			
			<div class="custbottom">
				<span class="custbottom_title">
					账户列表
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
	var editor;
	
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
				var user = "";
				var size = window.parent.$("#listboxAdd option").size();
				if (size > 0){
					 $.each($("#listboxAdd option"), function (i, own) {
					 
                        var text = $(own).val();
                        if(size - 1 == i){
                        	user += text;
                        }else{
                        	user += text + ",";
                        }
                    });
				}
				
				window.parent.$("#targetUser").val(user);
				
				var content = getContent();
				
				window.parent.$("#content").text(content);
				
				var flag = $(this).form('validate');
				if (flag) {
					var userFlag = false;
					var contentFlag = false;
				
                	var user = window.parent.$("#targetUser").val();
                	if (user != null && user != ""){
                		userFlag = true;
                	}else{
                		window.parent.$.messager.alert("错误信息","请选择账户",'error');
						userFlag = false;
                	}
                	
                	
					if(content != null && content != ""){
						contentFlag = true;
					}else{
						window.parent.$.messager.alert("错误信息","请输入内容",'error');
						contentFlag = false;
					}
					
					if(userFlag && contentFlag){
						flag = true;
					}else{
						flag = false;
					}
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
		$("#userRemindModalWindow").window("destroy");
	}
	
	function initEditor() {
	    editor = KindEditor.create($("#content"), {
	        resizeType : 0,
	        autoHeightMode : false,
	        minWidth: 376,
	        minHeight: 60,
	        uploadJson : '${ctx}/resources/kindeditor/jsp/upload_json.jsp',
			fileManagerJson : '${ctx}/resources/kindeditor/jsp/file_manager_json.jsp',
			allowFileManager : true,
	        items : ['source', '|', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
				'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
				'insertunorderedlist', '|', 'emoticons', 'image', 'link'],
			afterCreate : function() {
				this.sync();
	        },
	        afterBlur:function(){
	            this.sync();
	        }
	    });
	}
	
	function getContent() {
	    editor.sync();
	    return $("#content").val();
	}
</script>
