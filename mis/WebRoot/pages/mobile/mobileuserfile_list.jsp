<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="../taglib.jsp"%>
<script type="text/javascript" src="${ctx}/resources/custjs/pagemessage.js"></script>

<div class="easyui-layout" data-options="fit:true,border:false">
	<div data-options="region:'north',border:false" style="height: 40px; overflow: hidden;">
		<form id="searchForm">
			<ul>
				<li>用户地址：<input type="text" id="ownerUri" name="ownerUri" /></li>
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
			url : "${ctx}/mobileUserFile.do?method=list",
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
					width : 15,
					align : 'center'
				},
				{
					field : 'description',
					title : '描述',
					width : 15,
					align : 'center'
				},
				{
					field : 'fileName',
					title : '文件名',
					width : 10,
					align : 'center'
				},
				{
					field : 'fileSize',
					title : '文件大小',
					width : 10,
					align : 'center'
				},
				{
					field : 'fileType',
					title : '文件类型',
					width : 10,
					align : 'center'
				},
				{
					field : 'fileUrl',
					title : '文件地址',
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
					width:10,
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
		
		window.parent.$("#modalWindow").html("<div id=\"mobileUserFileModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		/* 初始化弹出页面的层 */
		window.parent.$("#mobileUserFileModalWindow").window({
			title : '新增',
			href : "${ctx}/pages/mobile/mobileuserfile_addorupdate.jsp?op=" + operate,
			width : 520,
			height : 450,
			onClose: function () {
				window.parent.$("#mobileUserFileModalWindow").window('destroy');
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
		
		window.parent.$("#modalWindow").html("<div id=\"mobileUserFileModalWindow\" modal=\"true\" shadow=\"false\" minimizable=\"false\" cache=\"false\" maximizable=\"false\" collapsible=\"false\" resizable=\"false\" style=\"margin: 0px;padding: 0px;overflow: auto;\"></div>");
		
		window.parent.$("#mobileUserFileModalWindow").window({
			title : '编辑',
			href : "${ctx}/pages/mobile/mobileuserfile_addorupdate.jsp?op=" + operate,
			width : 520,
			height : 400,
			onClose: function () {
				window.parent.$("#mobileUserFileModalWindow").window('destroy');
			},
			onLoad : function() {
				$.ajax({
					type : "post",
					url : "${ctx}/mobileUserFile.do?method=get",
					data : {
						"id" : rows.id
					},
					dataType : "json",
					timeout : 50000,
					success : function(data) {
						window.parent.$('#tableForm').form('load', {
							id:data.id,
							fileSize:data.fileSize,
							description:data.description,
							status:data.status
						});
						
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
			url = "${ctx}/mobileUserFile.do?method=add";
		} else if (operate == "update") {
			url = "${ctx}/mobileUserFile.do?method=update";
		}
		
		var formData = window.parent.$("#tableForm").serializeArray();
		
		$.post(url, formData, function(data) {
			if (data.message == "200") {
				actionSuccess();
				window.parent.$("#mobileUserFileModalWindow").window("destroy");
				$('#tableData').datagrid("reload");
			} else {
				actionFailure();
			}
		});
	}
	
	function initListboxRemove(targetOwnerUri){
		$.ajax({
			url:"${ctx}/mobileUser.do?method=getmobileuserhtmloptions&targetOwnerUri=" + targetOwnerUri,
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
			url:"${ctx}/mobileUser.do?method=getremoveandaddmobileuserfilesharehtmloptions&id=" + id,
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
				$.post("${ctx}/mobileUserFile.do?method=delete", {ids:ids}, function(data) {
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
		var description = $("#description").val(); 
		
		$.ajax({  
	        type:"POST",
	        dataType: "text",
	        url:"${ctx}/mobileUserFile.do?method=getfilepath",
	        data:"ownerUri="+ownerUri+"&description="+description+"",
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
</script>
