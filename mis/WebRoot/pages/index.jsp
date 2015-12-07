<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<div class="easyui-layout" data-options="fit:true,border:false">
	<!-- <div data-options="region:'north',border:false"></div> -->
	<div data-options="region:'center',border:false">
		<table id="tableData"></table>
	</div>
</div>

<script type="text/javascript">
	$(function() {
		tableDataInit();
	});

	/* init grid */
	function tableDataInit() {
		$("#tableData").datagrid({
			title: '提醒',
			fitColumns : true,/* 自动调整各列，用了这个属性，下面各列的宽度值就只是一个比例。 */
			nowrap :false,/* true 就会把数据显示在一行里。 */
			striped : true,/* 奇偶行颜色不同 */
			sortName : 'createTime',/* 排序的列 */
			sortOrder : 'desc',/* asc正序，desc倒序 */
			remoteSort : true,/* 服务器端排序 */
			fit : true,
			url : "${pageContext.request.contextPath}/userRemind.do?method=getremindsbyuser",
			loadMsg : "数据加载中,请稍等............",/* 加载的时候显示的文字 */
			idField : 'id',/* 主键字段 */
			columns : [ [
				{
					field : 'id',
					title : 'Id',
					hidden: true
				},
				{
					field : 'title',
					title : '标题',
					width : 30,
					align : 'center'
				},
				{
					field : 'processTag',
					title : '处理状态',
					width : 25,
					align : 'center'
				},
				{
					field : 'status',
					title : '状态',
					width : 30,
					align : 'center'
				},
				{
					field : 'createTime',
					title : '创建日期',
					width : 20,
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
						oper = oper + '&nbsp;<a href="javascript:view('+ rowIndex+ ')">查看</a>';
						return oper;
					}
				}
			] ],
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
	
	function view(rowIndex) {
		$('#tableData').datagrid("clearSelections");/* 清除全部选择的行 */
		$('#tableData').datagrid("selectRow", rowIndex);/* 设置选择行 */
		var rows = $("#tableData").datagrid("getSelected", rowIndex);/* 获得选择行 */
		
		window.parent.$.messager.alert("提醒内容",rows.content);
	}
	
	function exprot() {
		var startTime = $("#startTime").datebox('getValue'); 
		var endTime = $("#endTime").datebox('getValue');
		var meiNo = $("#meiNo").val();
		var summaryHour = $("#summaryHour").combobox('getValue');
		var summaryWeekly = $("#summaryWeekly").combobox('getValue');
		var summaryMonth = $("#summaryMonth").combobox('getValue');
		
		$.ajax({  
	        type:"POST",
	        dataType: "text",
	        url:"${ctx}/summaryDay.do?method=getfilepath",
	        data:"startTime="+startTime+"&endTime="+endTime+"&meiNo="+meiNo+"&summaryHour="+summaryHour+"&summaryWeekly="+summaryWeekly+"&summaryMonth="+summaryMonth+"",
	        async: false,  
	        success: function(data) {
	        	if(data != null && data != ""){
	        		window.location.href = "${ctx}/summaryDay.do?method=export&filepath=" + data;	
	        	} else {
	        		alert("此条件下没有有效数据，请重新选择条件进行导出.");
	        		return false;
	        	}  
	        } 
	    });
	}
</script>

