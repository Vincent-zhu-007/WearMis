<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String op=(String)request.getParameter("op"); %>

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
	    $("#status").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=STATUS",
		    valueField:'id',
		    textField:'text',
			required:true,
			editable:false
	    });
	    
	    $("#gender").combobox({
		    url:"${ctx}/code.do?method=getcodecache&category=GENDER",
		    valueField:'id',
		    textField:'text',
			editable:false
	    });
	    
	    $("#province").combobox({
		    url:"${ctx}/province.do?method=getprovincecachemap",
		    valueField:'id',
		    textField:'text',
			editable:false,
			onChange: function (n,o) {
				$("#city").combobox({
				    url:"${ctx}/city.do?method=getcitycachemapbyprovincecodename&provinceCodeName=" + n,
				    valueField:'id',
				    textField:'text',
					editable:false
			    });
			}
	    });
	    
	    $("#mobilePhone").validatebox();
	    $("#trueName").validatebox();
	    
	    var editor = KindEditor.editor({
			uploadJson : '${ctx}/resources/kindeditor/jsp/upload_json.jsp',
			fileManagerJson : '${ctx}/resources/kindeditor/jsp/file_manager_json.jsp',
			allowFileManager : true
		});
		
		$('#btnImageUpload').click(function() {
			editor.loadPlugin('image', function() {
				editor.plugin.imageDialog({
					imageUrl : $('#headPortrait').val(),
					clickFn : function(url, title, width, height, border, align) {
						$('#headPortrait').val(url);
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
			<input type="hidden" id="weiXinRole" name="weiXinRole" />
			<div class="custtop">
				<ul>
					<%if(op.equals("add")){ %>
					<li>
						<span class="textlable">用户地址：</span>
						<span class="inputlable">
							<input id="ownerUri" name="ownerUri" class="easyui-validatebox" data-options="required:true,validType:{
								length:[0,50],
								remote:['${ctx}/weiXinUser.do?method=owneruriisexist','ownerUri']
							},invalidMessage:'用户地址已经存在'" />
						</span>
					</li>
					
					<li>
						<span class="textlable">用户名：</span>
						<span class="inputlable">
							<input id="userName" name="userName" class="easyui-validatebox" data-options="required:true,validType:{
								length:[0,40],
								remote:['${ctx}/weiXinUser.do?method=usernameisexist','userName']
							},invalidMessage:'用户名已经存在或长度超过40'" />
						</span>
					</li>
					<%} %>
					
					<li>
						<span class="textlable">密码：</span>
						<span class="inputlable">
							<input type="password" id="password" name="password" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">组织架构：</span>
						<span class="inputlable">
							<input id="orgStructure" name="orgStructure" />
						</span>
					</li>
					
					<li>
						<span class="textlable">显示名称：</span>
						<span class="inputlable">
							<input type="text" id="displayName" name="displayName" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">真实姓名：</span>
						<span class="inputlable">
							<input id="trueName" name="trueName" />
						</span>
					</li>
					
					<li>
						<span class="textlable">手机号码：</span>
						<span class="inputlable">
							<input id="mobilePhone" name="mobilePhone" class="easyui-validatebox" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">性别：</span>
						<span class="inputlable">
							<input id="gender" name="gender" />
						</span>
					</li>
					
					<li>
						<span class="textlable">邮箱地址：</span>
						<span class="inputlable">
							<input type="text" id="mail" name="mail" class="easyui-validatebox" data-options="validType: 'email'" />
						</span>
					</li>
					
					<li>
						<span class="textlable">生日：</span>
						<span class="inputlable">
							<input id="birthday" name="birthday" class="easyui-datebox" />
						</span>
					</li>
					
					<li>
						<span class="textlable">省：</span>
						<span class="inputlable">
							<input id="province" name="province" />
						</span>
					</li>
					
					<li>
						<span class="textlable">市：</span>
						<span class="inputlable">
							<input id="city" name="city" />
						</span>
					</li>
					
					<li>
						<span class="textlable">简介：</span>
						<span class="inputlable">
							<input id="sign" name="sign" />
						</span>
					</li>
					
					<li>
						<span class="textlable">状态：</span>
						<span class="inputlable">
							<input id="status" name="status" data-options="required:true" />
						</span>
					</li>
					
					<li>
						<span class="textlable">上传头像：</span>
						<span class="inputlable">
							<input type="text" id="headPortrait" name="headPortrait" />
							<input type="button" id="btnImageUpload" value="选择头像" />
						</span>
					</li>
				</ul>
			</div>
			
			<div class="custbottom">
				<span class="custbottom_title">
					微信角色列表
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
		
		$("#tableForm").form({
			url : "",
			onSubmit : function() {
				var weiXinRole = "";
				var size = window.parent.$("#listboxAdd option").size();
				if (size > 0){
					 $.each($("#listboxAdd option"), function (i, own) {
					 
                        var text = $(own).val();
                        if(size - 1 == i){
                        	weiXinRole += text;
                        }else{
                        	weiXinRole += text + ",";
                        }
					});
				}
				
				window.parent.$("#weiXinRole").val(weiXinRole);
				
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
		$("#weiXinUserModalWindow").window("destroy");
	}
</script>
