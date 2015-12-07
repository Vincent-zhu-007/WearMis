<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style type="text/css">
	#btnImageUpload{
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
		/* 添加项[向右移单项移动] */
        multipleMobileUserAddClick = function () {
            var $options = $('#listboxAddMobileUser option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxRemoveMobileUser");
        };
        
		/* 添加项[向右移全部移动] */
        multipleMobileUserAddAllClick = function () {
            var $options = $('#listboxAddMobileUser option');
            var $remove = $options.remove();
            $remove.appendTo("#listboxRemoveMobileUser");
        };

		/* 移除项[向左移单项移动] */
        multipleMobileUserDelClick = function () {
            $('#listboxRemoveMobileUser option:selected').appendTo('#listboxAddMobileUser');
        };
        
		/* 移除项[向左移单项移动] */
        multipleMobileUserDelAllClick = function () {
            $('#listboxRemoveMobileUser option').appendTo('#listboxAddMobileUser');
        };
        
		/* 双击选项 */
        $('#listboxAddMobileUser').dblclick(function () { /* 绑定双击事件 */
			/* 获取全部的选项,删除并追加给对方 */
            var $options = $('#listboxAddMobileUser option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxRemoveMobileUser");
        });
        
		/* 双击选项 */
        $('#listboxRemoveMobileUser').dblclick(function () {
            var $options = $('#listboxRemoveMobileUser option:selected');
            var $remove = $options.remove();
            $remove.appendTo("#listboxAddMobileUser");
        });
	});
</script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'center',border:false" style="padding: 5px;">
		
		<form id="tableForm" name="tableForm">
			<input type="hidden" id="id" name="id" />
			<input type="hidden" id="mobileOwnerUris" name="mobileOwnerUris" />
			<div class="custtop">
			</div>
			
			<div class="custbottom">
				<span class="custbottom_title">
					终端列表
				</span>
				<div class="custbottom_left">
					<select id="listboxRemoveMobileUser" multiple="multiple" name="listboxRemoveMobileUser" class="multiple"></select>
				</div>
				<div class="custbottom_middle">
					<input type="button" id="multiple_del" value=">" onclick="multipleMobileUserDelClick();" class="btn_multiple" />
                    <input type="button" id="multiple_del_all" value=">>" onclick="multipleMobileUserDelAllClick();" class="btn_multiple" />
					<input type="button" id="multiple_add" value="<" onclick="multipleMobileUserAddClick();" class="btn_multiple" />
                    <input type="button" id="multiple_add_all" value="<<" onclick="multipleMobileUserAddAllClick();" class="btn_multiple" />
				</div>
				<div class="custbottom_right">
					<select id="listboxAddMobileUser" multiple="multiple" name="listboxAddMobileUser" class="multiple"></select>
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
		
		$("#tableForm").form({
			url : "",
			onSubmit : function() {
				var mobileOwnerUris = "";
				var size = window.parent.$("#listboxAddMobileUser option").size();
				if (size > 0){
					 $.each($("#listboxAddMobileUser option"), function (i, own) {
					 
                        var text = $(own).val();
                        if(size - 1 == i){
                        	mobileOwnerUris += text;
                        }else{
                        	mobileOwnerUris += text + ",";
                        }
					});
				}
				
				window.parent.$("#mobileOwnerUris").val(mobileOwnerUris);
				
				var flag = $(this).form('validate');
				if (flag) {
					
				}
				return flag;
			},
			success : function(data) {
				var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
				var frameId = "iframeId_" + tabId;
				var currentFrames = window.frames[frameId];
			
				currentFrames.addOrUpdateWeiXinInMobile();
			}
		});
	});
	
	function save() {
		$("#tableForm").submit();
	}
	
	function cancel() {
		$("#weiXinUserModalWindow").window("destroy");
	}
</script>
