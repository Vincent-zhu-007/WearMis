<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="../taglib.jsp"%>
<script type="text/javascript" src="${ctx}/resources/custjs/pagemessage.js"></script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height: 40px; overflow: hidden;">
		<form id="searchForm">
			<ul>
				<li>公司编码：<input id="companyCodeSearch" name="companyCodeSearch" /></li>
				<li>描述：<input type="text" id="description" name="description" /></li>
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
		$("#companyCodeSearch").combobox({
		    url:"${ctx}/code.do?method=getddlcodecache&category=MOBILECOMPANY",
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
			url : "${ctx}/mobileServerConfig.do?method=list",
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
					field : 'companyCode',
					title : '公司编码',
					width : 20,
					align : 'center'
				},
				{
					field : 'host',
					title : '主机',
					width : 20,
					align : 'center'
				},
				{
					field : 'port',
					title : '端口',
					width : 10,
					align : 'center'
				},
				{
					field : 'httpMethod',
					title : '请求类型',
					width : 10,
					align : 'center'
				},
				{
					field : 'appName',
					title : '应用名',
					width : 10,
					align : 'center'
				},
				{
					field : 'contentType',
					title : 'MEDIA类型',
					width : 20,
					align : 'center'
				},
				{
					field : 'description',
					title : '描述',
					width : 20,
					align : 'center'
				},
				{
					field : 'urlPart1',
					title : '地址第一部分',
					width : 20,
					align : 'center'
				},
				{
					field : 'urlPart2',
					title : '地址第二部分',
					width : 20,
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
				id : "copyBtn",
				text : '拷贝',
				iconCls : 'icon-add',
				handler : function() {
					copy();
				}
			},'-',
			{
				id : "clearMobileServerConfigCacheBtn",
				text : '重置终端服务配置缓存',
				iconCls : 'icon-redo',
				handler : function() {
					clearMobileServerConfigCache();
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
		
		window.parent.$("#modalWindow").html("<div id=\"mobileServerConfigModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		/* 初始化弹出页面的层 */
		window.parent.$("#mobileServerConfigModalWindow").window({
			title : '新增',
			href : "${ctx}/pages/mobile/mobileserverconfig_addorupdate.jsp",
			width : 300,
			height : 420,
			onClose: function () {
				window.parent.$("#mobileServerConfigModalWindow").window('destroy');
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
		
		window.parent.$("#modalWindow").html("<div id=\"mobileServerConfigModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		window.parent.$("#mobileServerConfigModalWindow").window({
			title : '编辑',
			href : "${ctx}/pages/mobile/mobileserverconfig_addorupdate.jsp",
			width : 300,
			height : 420,
			onClose: function () {
				window.parent.$("#mobileServerConfigModalWindow").window('destroy');
			},
			onLoad : function() {
				$.ajax({
					type : "post",
					url : "${ctx}/mobileServerConfig.do?method=get",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						window.parent.$('#tableForm').form('load', {
							id:data.id,
							companyCode:data.companyCode,
							host:data.host,
							port:data.port,
							appName:data.appName,
							userAgent:data.userAgent,
							contentType:data.contentType,
							description:data.description,
							httpMethod:data.httpMethod,
							urlPart1:data.urlPart1,
							urlPart2:data.urlPart2,
							urlPart3:data.urlPart3,
							urlPart4:data.urlPart4,
							responseXmlFile:data.responseXmlFile,
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
			url = "${ctx}/mobileServerConfig.do?method=add";
		} else if (operate == "update") {
			url = "${ctx}/mobileServerConfig.do?method=update";
		}
		
		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileServerConfigModalWindow").window("destroy");
				$('#tableData').datagrid("reload");
			} else {
				actionFailure();
			}
		});
	}
	
	function clearMobileServerConfigCache(){
		$.ajax({
			type:"POST",
	        url:"${ctx}/mobileServerConfig.do?method=clearmobileserverconfigcache",
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
				$.post("${ctx}/mobileServerConfig.do?method=delete", {ids:ids}, function(data) {
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
		var companyCodeSearch = $("#companyCodeSearch").combobox('getValue');
		var description = $("#description").val(); 
		
		$.ajax({  
	        type:"POST",
	        dataType: "text",
	        url:"${ctx}/mobileServerConfig.do?method=getfilepath",
	        data:"companyCodeSearch="+companyCodeSearch+"&description="+description+"",
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
	
	function copy() {
		window.parent.$("#modalWindow").html("<div id=\"mobileServerConfigModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		window.parent.$("#mobileServerConfigModalWindow").window({
			title : '编辑',
			href : "${ctx}/pages/mobile/mobileserverconfig_copy.jsp",
			width : 300,
			height : 260,
			onClose: function () {
				window.parent.$("#mobileServerConfigModalWindow").window('destroy');
			},
			onLoad : function() {
				
			}
		});
	}
	
	function copySave() {
		var sourceCompanyCode = window.parent.$("#sourceCompanyCode").combobox('getValue');
		var companyCode = window.parent.$("#companyCode").combobox('getValue');
		var host = window.parent.$("#host").val();
		var port = window.parent.$("#port").val();
		var appName = window.parent.$("#appName").val();
		var userAgent = window.parent.$("#userAgent").val();
		
		var url = "${ctx}/mobileServerConfig.do?method=copy";
		
		var formData = "sourceCompanyCode="+sourceCompanyCode+"&companyCode="+companyCode+"&host="+host+"&port="+port+"&appName="+appName+"&userAgent="+userAgent+"";
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileServerConfigModalWindow").window("destroy");
				$('#tableData').datagrid("reload");
			} else {
				actionFailure();
			}
		});
	}
</script>
