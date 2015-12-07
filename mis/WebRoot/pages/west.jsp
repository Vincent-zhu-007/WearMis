<%@ page language="java"  pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<script type="text/javascript">
	$(function() {
		/* init tree */
		$('#layout_west_tree').tree({
			url : '${pageContext.request.contextPath}/menu.do?method=initmenu',
			lines : true,
			onClick : function(node) {
				var url;
				
				if (node.attributes.url != null && node.attributes.url != "") {
					url = '${pageContext.request.contextPath}/' + node.attributes.url;
				} else {
					url = "";
					return false;
				}
				
				layout_center_addTabFun({
					id : node.id,
					title : node.text,
					closable : true,
					iconCls : node.iconCls,
					content : '<iframe id="iframeId_' + node.id + '" name="iframeId_' + node.id + '" src="' + url + '" frameborder="0" style="border:0;width:100%;height:100%;"></iframe>'
				});
			}
		});
	});
</script>

<div class="easyui-accordion" data-options="fit:true,border:false">
	<div data-options="">
		<ul id="layout_west_tree"></ul>
	</div>
</div>
