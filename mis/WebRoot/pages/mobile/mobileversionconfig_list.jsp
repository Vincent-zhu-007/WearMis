<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="../taglib.jsp"%>
<script type="text/javascript" src="${ctx}/resources/custjs/pagemessage.js"></script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height: 40px; overflow: hidden;">
		<form id="searchForm">
			<ul>
				<li>名称：<input type="text" id="name" name="name" /></li>
				<li>版本显示号：<input type="text" id="verNo" name="verNo" /></li>
				<li>适用类型：<input id="verCategorySearch" name="verCategorySearch" /></li>
				<li><a id="searchBtn" class="easyui-linkbutton" data-options="iconCls:'icon-search'" href="javascript:;" onclick="doSearch()">查询</a></li>
			</ul>
		</form>
	</div>
	<div data-options="region:'center',border:false">
		<table id="tableData"></table>
	</div>
</div>

<script type="text/javascript">
	var operate = "";

	$(function() {
		$("#verCategorySearch").combobox({
		    url:"${ctx}/code.do?method=getddlcodecache&category=MOBILEVERSIONCATEGORY",
		    valueField:'id',
		    textField:'text',
			editable:false
	    });
	
		$("#searchBtn").linkbutton({
			plain:false
		});
		
		tableDataInit();
	});

	/* init grid */
	function tableDataInit() {
		$("#tableData").datagrid({
			fitColumns : true,/* 自动调整各列，用了这个属性，下面各列的宽度值就只是一个比例。 */
			nowrap :false,/* true 就会把数据显示在一行里。 */
			striped : true,/* 奇偶行颜色不同 */
			sortName : 'createTime',/* 排序的列 */
			sortOrder : 'desc',/* asc正序，desc倒序 */
			remoteSort : true,/* 服务器端排序 */
			fit : true,
			url : "${ctx}/mobileVersionConfig.do?method=list",
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
					field : 'name',
					title : '名称',
					width : 10,
					align : 'center'
				},
				{
					field : 'verFileAddress',
					title : '文件地址',
					width : 10,
					align : 'center'
				},
				{
					field : 'verNo',
					title : '版本显示号',
					width : 10,
					align : 'center'
				},
				{
					field : 'verCategory',
					title : '适用类型',
					width : 10,
					align : 'center'
				},
				{
					field : 'verSort',
					title : '版本号',
					width : 10,
					align : 'center'
				},
				{
					field : 'isMandatoryUpdate',
					title : '是否强制',
					width : 10,
					align : 'center'
				},
				{
					field : 'verRemark',
					title : '备注',
					width : 10,
					align : 'center'
				},
				{
					field : 'status',
					title : '状态',
					width : 10,
					align : 'center'
				},
				{
					field : 'createTime',
					title : '创建日期',
					width : 10,
					align : 'center',
					formatter : function(value, rows, rowIndex) {
						return value == null ? '' : formatterDate(value);
					}
				},
				{
					field : 'operate',
					title : '操作',
					align : 'center',
					width:20,
					formatter : function(value, rows, rowIndex) {
						var oper = '';
						oper = oper + '&nbsp;<a href="javascript:edit('+ rowIndex+ ')">编辑</a>';
						return oper;
					}
				}
			] ],
			toolbar : [ {
				id : "addBtn",
				text : '新增',
				iconCls : 'icon-add',
				handler : function() {
					add();
				}
			},'-',
			{
				id : "removeBtn",
				text : '删除',
				iconCls : 'icon-remove',
				handler : function() {
					remove();
				}
			},'-',
			{
				id : "clearMobileVersionConfigCacheBtn",
				text : '重置终端版本配置缓存',
				iconCls : 'icon-redo',
				handler : function() {
					clearMobileVersionConfigCache();
				}
			},'-',
			{
				id : "exportBtn",
				text : '导出',
				iconCls : 'icon-save',
				handler : function() {
					exprot();
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
	
	function doSearch() {
		var params = $('#tableData').datagrid('options').queryParams;
	
		var fields = $("#searchForm").serializeArray();
		
		$.each(fields, function(i, field) {
			params[field.name] = field.value;
		});
		
		$("#tableData").datagrid('load');
	}
	
	function add() {
		operate = "add";
		
		window.parent.$("#modalWindow").html("<div id=\"mobileVersionConfigModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		/* 初始化弹出页面的层 */
		window.parent.$("#mobileVersionConfigModalWindow").window({
			title : '新增',
			href : "${ctx}/pages/mobile/mobileversionconfig_addorupdate.jsp",
			width : 520,
			height : 330,
			onClose: function () {
				window.parent.$("#mobileVersionConfigModalWindow").window('destroy');
			},
			onLoad : function() {
				
			}
		});
	}
	
	function edit(rowIndex) {
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		
		operate = "update";
		
		window.parent.$("#modalWindow").html("<div id=\"mobileVersionConfigModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		window.parent.$("#mobileVersionConfigModalWindow").window({
			title : '编辑',
			href : "${ctx}/pages/mobile/mobileversionconfig_addorupdate.jsp",
			width : 520,
			height : 330,
			onClose: function () {
				window.parent.$("#mobileVersionConfigModalWindow").window('destroy');
			},
			onLoad : function() {
				$.ajax({
					type : "post",
					url : "${ctx}/mobileVersionConfig.do?method=get",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						window.parent.$('#tableForm').form('load', {
							id:data.id,
							name:data.name,
							verFileAddress:data.verFileAddress,
							verNo:data.verNo,
							verCategory:data.verCategory,
							verSort:data.verSort,
							isMandatoryUpdate:data.isMandatoryUpdate,
							verRemark:data.verRemark,
							status:data.status
						});
					},
					error : function(XMLHttpRequest, data, errorThrown) {
						 actionFailure();
					}
				});
			}
		});
	}
	
	function addOrUpdate() {
		var url = "";
		if (operate == "add") {
			url = "${ctx}/mobileVersionConfig.do?method=add";
		} else if (operate == "update") {
			url = "${ctx}/mobileVersionConfig.do?method=update";
		}
		
		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileVersionConfigModalWindow").window("destroy");
				$('#tableData').datagrid("reload");
			} else {
				actionFailure();
			}
		});
	}
	
	function remove(){
		var rows = $("#tableData").datagrid("getSelections");
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
				$.post("${ctx}/mobileVersionConfig.do?method=delete", {ids:ids}, function(data) {
					if (data.message == "200") {
						actionSuccess();
						$('#tableData').datagrid("reload");
						rows.length=0;
					} else {
						actionFailure();
						rows.length=0;
					}
				});
			}else{
				return false;
			}
    	}); 
	}
	
	function exprot() {
		var name = $("#name").val();
		var verNo = $("#verNo").val();
		var verCategory = $("#verCategorySearch").combobox('getValue'); 
		
		$.ajax({  
	        type:"POST",
	        dataType: "text",
	        url:"${ctx}/mobileVersionConfig.do?method=getfilepath",
	        data:"name="+name+"&verNo="+verNo+"&verCategory="+verCategory+"",
	        async: false,  
	        success: function(data) {
	        	if(data != null && data != ""){
	        		window.location.href = "${ctx}/common.do?method=export&filepath=" + data;	
	        	} else {
	        		window.parent.$.messager.alert("错误信息","此条件下没有有效数据，请重新选择条件进行导出.",'error');
	        		return false;
	        	}  
	        } 
	    });
	}
	
	function clearMobileVersionConfigCache(){
		$.ajax({
			type:"POST",
	        url:"${ctx}/mobileVersionConfig.do?method=clearmobileversionconfigcache",
	        async: false,
	        success: function(data){
	        	if(data.message == "200"){
	        		actionSuccess();
	        	}else{
	        		actionFailure();
	        	}
	        }
		});
	}
</script>
