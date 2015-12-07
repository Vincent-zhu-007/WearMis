<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style type="text/css">
	#btnFileUpload{
		width: 60px;
		font:12px/18px "微软雅黑","宋体","黑体",Arial;
	}
</style>

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
</style>

<script type="text/javascript">
	$(function(){
		var editor = KindEditor.editor({
			uploadJson : '${ctx}/resources/kindeditor/jsp/upload_json.jsp',
			fileManagerJson : '${ctx}/resources/kindeditor/jsp/file_manager_json.jsp',
			allowFileManager : true
		});
	
		$('#btnFileUpload').click(function () {
            editor.loadPlugin('insertfile', function () {
                editor.plugin.fileDialog({
                    fileUrl: $('#fileName').val(),
                    clickFn: function (url, title, width, height, border, align) {
                        $('#fileName').val(url);
                        editor.hideDialog();
                    }
                });
            });
        });
        
        /* 添加项[向右移单项移动] */
        multipleAddMeiClick = function () {
            var $options = $('#listboxMeiAdd option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxMeiRemove");
        };
        
		/* 添加项[向右移全部移动] */
        multipleAddMeiAllClick = function () {
            var $options = $('#listboxMeiAdd option');
            var $remove = $options.remove();
            $remove.appendTo("#listboxMeiRemove");
        };

		/* 移除项[向左移单项移动] */
        multipleDelMeiClick = function () {
            $('#listboxMeiRemove option:selected').appendTo('#listboxMeiAdd');
        };
        
		/* 移除项[向左移单项移动] */
        multipleDelMeiAllClick = function () {
            $('#listboxMeiRemove option').appendTo('#listboxMeiAdd');
        };
        
		/* 双击选项 */
        $('#listboxMeiAdd').dblclick(function () { /* 绑定双击事件 */
			/* 获取全部的选项,删除并追加给对方 */
            var $options = $('#listboxMeiAdd option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxMeiRemove");
        });
        
		/* 双击选项 */
        $('#listboxMeiRemove').dblclick(function () {
            var $options = $('#listboxMeiRemove option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxMeiAdd");
        });
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="meiNos" name="meiNos" /> 
			<div class="custtop">
				<ul>
					<li>
						<span class="textlable">上传文件：</span>
						<span class="inputlable">
							<input type="text" id="fileName" name="fileName" style="width:85px;" />
							<input type="button" id="btnFileUpload" value="选择文件" />
						</span>
					</li>
					<li>
						<span class="textlable">加载文件：</span>
						<span class="inputlable">
							<a href="javascript:;" onclick="readFile()">读取文件</a>
						</span>
					</li>
					<li>
						<span class="textlable">域名：</span>
						<span class="inputlable">
							<input id="sipHostName" name="sipHostName" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					<li>
						<span class="textlable">密码：</span>
						<span class="inputlable">
							<input type="password" id="import_password" name="import_password" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					<li>
						<span class="textlable">组织架构：</span>
						<span class="inputlable">
							<input id="import_orgStructure" name="import_orgStructure" />
						</span>
					</li>
				</ul>
			</div>
			
			<div class="custbottom">
				<span class="custbottom_title">
					串号列表
				</span>
				<div class="custbottom_left">
					<select id="listboxMeiRemove" multiple="multiple" name="listboxMeiRemove" class="multiple"></select>
				</div>
				<div class="custbottom_middle">
					<input type="button" id="mei_multiple_del" value=">" onclick="multipleDelMeiClick();" class="btn_multiple" />
                    <input type="button" id="mei_multiple_del_all" value=">>" onclick="multipleDelMeiAllClick();" class="btn_multiple" />
					<input type="button" id="mei_multiple_add" value="<" onclick="multipleAddMeiClick();" class="btn_multiple" />
                    <input type="button" id="mei_multiple_add_all" value="<<" onclick="multipleAddMeiAllClick();" class="btn_multiple" />
				</div>
				<div class="custbottom_right">
					<select id="listboxMeiAdd" multiple="multiple" name="listboxMeiAdd" class="multiple"></select>
				</div>
			</div>
		</form>
	</div>
	
	<div data-options="region:'south',border:false" style="padding-right: 5px;padding-bottom: 5px;text-align: right;">
		<a id="saveBtn" class="easyui-linkbutton" data-options="iconCls:'icon-save'" href="javascript:;" onclick="save()">导入</a>
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
				var meiNos = "";
				var size = window.parent.$("#listboxMeiAdd option").size();
				if (size > 0){
					 $.each($("#listboxMeiAdd option"), function (i, own) {
					 
                        var text = $(own).val();
                        if(size - 1 == i){
                        	meiNos += text;
                        }else{
                        	meiNos += text + ",";
                        }
					});
				}
				
				window.parent.$("#meiNos").val(meiNos);
				
				var flag = $(this).form('validate');
				if (flag) {
					if(meiNos != null && meiNos != ""){
						flag = true;
					}else{
						window.parent.$.messager.alert("错误信息","请选择串号",'error');
						flag = false;
					}
				}
				return flag;
			},
			success : function(data) {
				var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
				var frameId = "iframeId_" + tabId;
				var currentFrames = window.frames[frameId];
			
				currentFrames.addMeiNoUsers();
			}
		});
	});
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		$("#mobileUserImportMeiModalWindow").window("destroy");
	}
	
	function readFile(){
		$("#listboxMeiRemove").html("");
		$("#listboxMeiAdd").html("");
	
		var fileName = $("#fileName").val();
	
		var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
		var frameId = "iframeId_" + tabId;
		var currentFrames = window.frames[frameId];
	
		currentFrames.initListboxMeiRemove(fileName);
	}
</script>
