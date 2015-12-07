<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="../taglib.jsp"%>

<script type="text/javascript" src="${ctx}/resources/custjs/pagemessage.js"></script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height: 40px; overflow: hidden;">
		<form id="searchForm">
			<ul>
				<li>用户地址：<input type="text" id="ownerUri" name="ownerUri" /></li>
				<li>用户名：<input type="text" id="userName" name="userName" /></li>
				<li>显示名称：<input type="text" id="displayName" name="displayName" /></li>
				<li>真实姓名：<input type="text" id="trueName" name="trueName" /></li>
				<li>组织架构：<input id="orgStructureSearch" name="orgStructureSearch" /></li>
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
		buildSearchMobileUserOrgStructureTree();
	
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
			sortName : 'ownerUri',/* 排序的列 */
			sortOrder : 'desc',/* asc正序，desc倒序 */
			remoteSort : true,/* 服务器端排序 */
			fit : true,
			url : "${ctx}/mobileUser.do?method=list",
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
					field : 'ownerUri',
					title : '用户地址',
					width : 20,
					align : 'center'
				},
				{
					field : 'userName',
					title : '用户名',
					width : 10,
					align : 'center'
				},
				{
					field : 'displayName',
					title : '显示名称',
					width : 10,
					align : 'center'
				},
				{
					field : 'trueName',
					title : '真实姓名',
					width : 10,
					align : 'center'
				},
				{
					field : 'mobilePhone',
					title : '终端号码',
					width : 10,
					align : 'center'
				},
				{
					field : 'gender',
					title : '性别',
					width : 10,
					align : 'center'
				},
				{
					field : 'mail',
					title : '邮箱地址',
					width : 10,
					align : 'center'
				},
				{
					field : 'birthday',
					title : '生日',
					width : 10,
					align : 'center'
				},
				{
					field : 'headPortrait',
					title : '头像路径',
					width : 10,
					align : 'center'
				},
				{
					field : 'meiNo',
					title : '终端串号',
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
					width : 20,
					formatter : function(value, rows, rowIndex) {
						var oper = '';
						oper = oper + '&nbsp;<a href="javascript:edit('+ rowIndex+ ')">编辑</a>';
						oper = oper + '&nbsp;|&nbsp;<a href="javascript:mobileListening('+ rowIndex+ ')">远程监听</a>';
						oper = oper + '&nbsp;|&nbsp;<a href="javascript:mobileReportLocation('+ rowIndex+ ')">上报位置</a>';
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
				id : "exportBtn",
				text : '导出',
				iconCls : 'icon-save',
				handler : function() {
					exprot();
				}
			},'-',
			{
				id : "importMeiBtn",
				text : '导入MEI账户',
				iconCls : 'icon-add',
				handler : function() {
					importByMei();
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
		
		window.parent.$("#modalWindow").html("<div id=\"mobileUserModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		/* 初始化弹出页面的层 */
		window.parent.$("#mobileUserModalWindow").window({
			title : '新增',
			href : "${ctx}/pages/mobile/mobileuser_addorupdate.jsp?op=" + operate,
			width : 520,
			height : 560,
			onClose: function () {
				window.parent.$("#mobileUserModalWindow").window('destroy');
			},
			onLoad : function() {
				buildCreateMobileUserOrgStructureTree();
				initListboxRemove();
			}
		});
	}
	
	function edit(rowIndex) {
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		
		operate = "update";
		
		window.parent.$("#modalWindow").html("<div id=\"mobileUserModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		window.parent.$("#mobileUserModalWindow").window({
			title : '编辑',
			href : "${ctx}/pages/mobile/mobileuser_addorupdate.jsp?op=" + operate,
			width : 520,
			height : 540,
			onClose: function () {
				window.parent.$("#mobileUserModalWindow").window('destroy');
			},
			onLoad : function() {
				$.ajax({
					type : "post",
					url : "${ctx}/mobileUser.do?method=get",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						window.parent.$("#city").combobox({
						    url:"${ctx}/city.do?method=getcitycachemapbyprovincecodename&provinceCodeName=" + data.province,
						    valueField:'id',
						    textField:'text',
							editable:false
					    });
						
						window.parent.$('#tableForm').form('load', {
							id:data.id,
							password:data.password,
							displayName:data.displayName,
							orgStructure:data.orgStructure,
							status:data.status,
							trueName:data.trueName,
							mobilePhone:data.mobilePhone,
							gender:data.gender,
							mail:data.mail,
							birthday:data.birthday,
							headPortrait:data.headPortrait,
							province:data.province,
							city:data.city,
							sign:data.sign,
							mobileRole:data.mobileRole
						});
						
						buildEditMobileUserOrgStructureTree(rows.id);
						initListboxRemoveAndListboxAdd(rows.id);
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
			url = "${ctx}/mobileUser.do?method=add";
		} else if (operate == "update") {
			url = "${ctx}/mobileUser.do?method=update";
		}
		
		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileUserModalWindow").window("destroy");
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
				$.post("${ctx}/mobileUser.do?method=delete", {ids:ids}, function(data) {
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
		var ownerUri = $("#ownerUri").val();
		var userName = $("#userName").val();
		var displayName = $("#displayName").val();
		var trueName = $("#trueName").val();
		var orgStructureSearch = $("#orgStructureSearch").combotree('getValue');
		
		if(ownerUri != "" || userName != "" || displayName != "" || trueName != "" || orgStructureSearch != ""){
			$.ajax({  
		        type:"POST",
		        dataType: "text",
		        url:"${ctx}/mobileUser.do?method=getfilepath",
		        data:"ownerUri="+ownerUri+"&userName="+userName+"&displayName="+displayName+"&trueName="+trueName+"&orgStructureSearch="+orgStructureSearch+"",
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
		}else{
			window.parent.$.messager.alert("提示信息","由于数据量过大，请至少先择一个查询条件作为导出条件.",'error');
		}
	}
	
	function clearData(rowIndex){
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		window.parent.$.messager.confirm("确认", "是否确认删除", function(r) {
			if(r){
				$.ajax({
					type : "post",
					url : "${ctx}/mobileUser.do?method=clearData",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						if (data.message == "200") {
							actionSuccess();
							$('#tableData').datagrid("reload");
						} else {
							actionFailure();
						}
					},
					error : function(XMLHttpRequest, data, errorThrown) {
						 actionFailure();
					}
				});
			}else{
				return false;
			}
		});
	}
	
	function buildCreateMobileUserOrgStructureTree(){
	    window.parent.$("#orgStructure").combotree({
		    url: "${ctx}/mobileUser.do?method=buildcreatemobileuserorgstructuretree",
		    required: true,
		    lines: true,
			animate : false,
			onlyLeafCheck:true
	    });
	}
	
	function buildSearchMobileUserOrgStructureTree(){
		$("#orgStructureSearch").combotree({
		    url: "${ctx}/mobileUser.do?method=buildcreatemobileuserorgstructuretree",
		    lines: true,
			animate : false,
			onlyLeafCheck:true
	    });    
	}
	
	function buildEditMobileUserOrgStructureTree(id){
	    window.parent.$("#orgStructure").combotree({
		    url: "${ctx}/mobileUser.do?method=buildeditmobileuserorgstructuretree&id="+id+"",
		    required: true,
		    lines: true,
			animate : false,
			onlyLeafCheck:true
	    });
	}
	
	function initListboxRemove(){
		$.ajax({
			url:"${ctx}/code.do?method=getcodehtmloptionsbycategory&category=MOBILEROLE",
			type:"POST",
			async:false,
			dataType:"text",
			success:function(data){
				if(data != null && data != ""){
					var result = trimDoubleQuotation(data);
					window.parent.$("#listboxRemove").html(result);	
				}
			}
		});
	}
	
	function initListboxRemoveAndListboxAdd(id){
		$.ajax({
			url:"${ctx}/code.do?method=getremoveandaddcodehtmloptionsbymobilerolecategory&category=MOBILEROLE&id=" + id,
			type:"POST",
			async:false,
			dataType:"text",
			success:function(data){
				if(data != null && data != ""){
					var listboxAddOptions = data.split("&")[0];
					var listboxRemoveOptions = data.split("&")[1];
					window.parent.$("#listboxAdd").html(listboxAddOptions);
					window.parent.$("#listboxRemove").html(listboxRemoveOptions);	
				}
			}
		});
	}
	
	function buildImportMeiMobileUserOrgStructureTree(){
	    window.parent.$("#import_orgStructure").combotree({
		    url: "${ctx}/mobileUser.do?method=buildcreatemobileuserorgstructuretree",
		    required: true,
		    lines: true,
			animate : false,
			onlyLeafCheck:true
	    });
	}
	
	function importByMei() {
		window.parent.$("#modalWindow").html("<div id=\"mobileUserImportMeiModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		/* 初始化弹出页面的层 */
		window.parent.$("#mobileUserImportMeiModalWindow").window({
			title : '新增',
			href : "${ctx}/pages/mobile/mobileuser_import_mei.jsp",
			width : 520,
			height : 440,
			onClose: function () {
				window.parent.$("#mobileUserImportMeiModalWindow").window('destroy');
			},
			onLoad : function() {
				buildImportMeiMobileUserOrgStructureTree();	
			}
		});
	}
	
	function initListboxMeiRemove(fileName){
		$.ajax({
			url:"${ctx}/mobileUser.do?method=initlistboxmeiremove",
			data:"fileName=" + fileName,
			type:"POST",
			async:false,
			dataType:"text",
			success:function(data){
				if(data != null && data != ""){
					var strData = trimDoubleQuotation(data);
					var result = strData.split("&")[0];
					var message = strData.split("&")[1];
					
					if(result == "1"){
						window.parent.$("#listboxMeiRemove").html(message);
					}else{
						window.parent.$.messager.alert("错误信息", message, 'error');
	        			return false;
					}
				}
			}
		});
	}
	
	function addMeiNoUsers(){
		var url = "${ctx}/mobileUser.do?method=addmeinousers";

		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileUserImportMeiModalWindow").window("destroy");
				$('#tableData').datagrid("reload");
			} else {
				actionFailure();
			}
		});
	}
	
	function mobileControl(rowIndex) {
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		
		$.ajax({
			type : "post",
			url : "${ctx}/mobileUser.do?method=get",
			data : {
				"id" : rows.id
			},
			dataType : "json",
			timeout : 50000,
			success : function(data) {
				var mobileRole = data.mobileRole;
				
				if(mobileRole != null && mobileRole != ""){
					window.parent.$("#modalWindow").html("<div id=\"mobileUserModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
					window.parent.$("#mobileUserModalWindow").window({
						title : '编辑',
						href : "${ctx}/pages/mobile/mobileuser_mobilecontrol.jsp",
						width : 520,
						height : 350,
						onClose: function () {
							window.parent.$("#mobileUserModalWindow").window('destroy');
						},
						onLoad : function() {
							window.parent.$('#tableForm').form('load', {
								ownerUri:data.ownerUri
							});
						
							window.parent.$("#mobileUserRoleMenu").tree({
				        		url: "${ctx}/mobileRole.do?method=initmobileusermenubypermission&id=" + rows.id,
				        		checkbox: true,
				        		animate:false,/* 是否有动画效果 */
				        		onClick:function(node){
				        			window.parent.$(this).tree('toggle', node.target);
				        		}
			       			});
						}
					});
				}else{
					window.parent.$.messager.alert("错误信息","请首先为用户设置终端角色",'error');
				}
			},
			error : function(XMLHttpRequest, data, errorThrown) {
				 actionFailure();
			}
		});
	}
	
	function sendMobileControl(){
		var url = "${ctx}/mobileUser.do?method=sendmobilecontrol";
		
		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileUserModalWindow").window("destroy");
			} else {
				actionFailure();
			}
		});
	}
	
	function mobileListening(rowIndex){
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		window.parent.$.messager.confirm("确认", "是否确认远程监听", function(r) {
			if(r){
				$.ajax({
					type : "post",
					url : "${ctx}/mobileUser.do?method=sendmobilelistening",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						if (data.message == "200") {
							actionSuccess();
						} else {
							actionFailure();
						}
					},
					error : function(XMLHttpRequest, data, errorThrown) {
						 actionFailure();
					}
				});
			}else{
				return false;
			}
		});
	}
	
	function mobileReportLocation(rowIndex){
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		window.parent.$.messager.confirm("确认", "是否确认上报位置", function(r) {
			if(r){
				$.ajax({
					type : "post",
					url : "${ctx}/mobileUser.do?method=sendmobilereportlocation",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						if (data.message == "200") {
							actionSuccess();
						} else {
							actionFailure();
						}
					},
					error : function(XMLHttpRequest, data, errorThrown) {
						 actionFailure();
					}
				});
			}else{
				return false;
			}
		});
	}
</script>
