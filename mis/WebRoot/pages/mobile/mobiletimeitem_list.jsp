<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<% String id=(String)request.getParameter("id"); %>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height: 0px; overflow: hidden;">
	</div>
	<div data-options="region:'center',border:false">
		<table id="tableDataView"></table>
	</div>
</div>

<script type="text/javascript">
	var operate_mobileTimeItem = "";
	var id = '<%=id%>';

	$(function() {
		view();
	});
	
	function view(){
		tableDataViewInit(id);
	}

	/* init grid */
	function tableDataViewInit(id) {
		$("#tableDataView").datagrid({
			fitColumns : true,/* 自动调整各列，用了这个属性，下面各列的宽度值就只是一个比例。 */
			nowrap :false,/* true 就会把数据显示在一行里。 */
			striped : true,/* 奇偶行颜色不同 */
			sortName : 'startTime',/* 排序的列 */
			sortOrder : 'asc',/* asc正序，desc倒序 */
			remoteSort : true,/* 服务器端排序 */
			fit : true,
			url : "${ctx}/mobileTime.do?method=mobiletimeitemlist&id=" + id,
			loadMsg : "数据加载中,请稍等............",/* 加载的时候显示的文字 */
			idField : 'id',/* 主键字段 */
			columns : [ [
				{
					field : 'ck',
					checkbox : true
				},
				{
					field : 'id',
					title : 'Id',
					hidden: true
				},
				{
					field : 'startTime',
					title : '开始时间',
					width : 20,
					align : 'center'
				},
				{
					field : 'endTime',
					title : '结束时间',
					width : 20,
					align : 'center'
				},
				{
					field : 'level',
					title : '级别',
					width : 10,
					align : 'center'
				},
				{
					field : 'operate',
					title : '操作',
					align : 'center',
					width:20,
					formatter : function(value, rows, rowIndex) {
						var oper = '';
						oper = oper + '&nbsp;<a href="javascript:doEditMobileTimeItem('+ rowIndex+ ')">编辑</a>';
						return oper;
					}
				}
			] ],
			toolbar : [ {
				id : "addMobileTimeItemBtn",
				text : '新增',
				iconCls : 'icon-add',
				handler : function() {
					var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
					var frameId = "iframeId_" + tabId;
					var currentFrames = window.frames[frameId];
					currentFrames.addMobileTimeItem(id);
				}
			},'-',
			{
				id : "removeMobileTimeItemBtn",
				text : '删除',
				iconCls : 'icon-remove',
				handler : function() {
					removeMobileTimeItem();
				}
			}
			],
			onLoadError : function(none) {
				actionFailure();
			},
			pagination : true,
			rownumbers : true,
			pageList : [ 20, 30, 50, 100 ]
		});
	}
	
	function doEditMobileTimeItem(rowIndex){
		var tabId = $("#layout_center_tabs").tabs("getSelected").panel('options').id;
		var frameId = "iframeId_" + tabId;
		var currentFrames = window.frames[frameId];
		currentFrames.editMobileTimeItem(rowIndex);
	}
	
	function removeMobileTimeItem(){
		var rows = $("#tableDataView").datagrid("getSelections");
		var ids = "";
		for (var i = 0; rows && i < rows.length; i++) {
		    var id = rows[i].id;
		    ids += id + ",";
		}
		
		ids = trimComma(ids);
		
		if(ids == ""){
			window.parent.$.messager.alert("错误信息","请选择您要删除的数据",'error');
			return false;
		}
		
		window.parent.$.messager.confirm("确认", "是否确认删除", function(r) {
			if(r){
				$.post("${ctx}/mobileTime.do?method=deletemobiletimeitem", {ids:ids}, function(data) {
					if (data.message == "200") {
						$('#tableDataView').datagrid("reload");
						rows.length=0;
					} else {
						rows.length=0;
					}
				});
			}else{
				return false;
			}
    	}); 
	}
</script>
