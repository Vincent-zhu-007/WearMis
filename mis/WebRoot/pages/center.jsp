<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<script type="text/javascript">
	$(function() {
		/* 鼠标右键出来的菜单 */
		$('#layout_center_tabsMenu').menu({
			onClick : function(item) {
				/* 获取title的文字 */
				var curTabTitle = $(this).data('tabTitle');
				var type = $(item.target).attr('type');
				
				/* 点击刷新按钮 */
				if (type == 'refresh') {
					layout_center_refreshTab(curTabTitle);
					return;
				}
				
				/* 点击关闭按钮 */
				if (type == 'close') {
					var t = $('#layout_center_tabs').tabs('getTab', curTabTitle);
					if (t.panel('options').closable) {
						$('#layout_center_tabs').tabs('close', curTabTitle);
					} else {
						$.messager.alert('提示', '[' + t.panel('options').title + ']不可以被关闭', 'error');
					}
					return;
				}

				var allTabs = $('#layout_center_tabs').tabs('tabs');
				var closeTabsTitle = [];

				$.each(allTabs, function() {
					var opt = $(this).panel('options');
					if (opt.closable && opt.title != curTabTitle && type === 'closeOther') //当点击关闭其他按钮的时候
					{
						closeTabsTitle.push(opt.title);
					} 
					else if (opt.closable && type === 'closeAll') //当点击关闭全部按钮的时候
					{
						closeTabsTitle.push(opt.title);
					}
				});

				for ( var i = 0; i < closeTabsTitle.length; i++)
				{
					$('#layout_center_tabs').tabs('close', closeTabsTitle[i]);
				}
			}
		});
		
		/* 初始化选项卡 */
		$('#layout_center_tabs').tabs({
			fit : true,/* 设置为true时，控制面板的大小将铺满它所在的容器（浏览器) */
			border : false,/* 定义是否显示控制面板边框。默认true */
			onContextMenu : function(e, title) {
				/* 在一个选项卡面板被鼠标右键单击后触发 */
				e.preventDefault();
				$('#layout_center_tabsMenu').menu('show', {
					left : e.pageX,
					top : e.pageY
				}).data('tabTitle', title);
			},
			tools : [{
				/* 控制面板右侧的工具栏，每个工具选项都跟链接按钮一样。 */
				iconCls : 'icon-reload',/* 加载按钮图片 */
				handler : function() {
					/* 当按钮被点击时调用的一个句柄函数 */
					var href = $('#layout_center_tabs').tabs('getSelected').panel('options').href;
					if (href) {
						/*说明tab是以href方式引入的目标页面*/
						var index = $('#layout_center_tabs').tabs('getTabIndex', $('#layout_center_tabs').tabs('getSelected'));
						$('#layout_center_tabs').tabs('getTab', index).panel('refresh');
					} else {
						/*说明tab是以content方式引入的目标页面*/
						var panel = $('#layout_center_tabs').tabs('getSelected').panel('panel');
						var frame = panel.find('iframe');
						try {
							if (frame.length > 0) {
								for ( var i = 0; i < frame.length; i++) {
									frame[i].contentWindow.document.write('');
									frame[i].contentWindow.close();
									frame[i].src = frame[i].src;
								}
								
								if ($.browser.msie) {
									CollectGarbage();
								}
							}
						} catch (e) {
						}
					}
				}
			}, 
			{
				iconCls : 'icon-cancel',//关闭按钮图片
				handler : function() {
					var allTabs = $('#layout_center_tabs').tabs('tabs');
					var closeTabsTitle = [];
	
					$.each(allTabs, function() {
						var opt = $(this).panel('options');
						if(opt.closable) {
							closeTabsTitle.push(opt.title);
						}
					});
	
					for ( var i = 0; i < closeTabsTitle.length; i++) {
						$('#layout_center_tabs').tabs('close', closeTabsTitle[i]);
					}
				}
			}]
		});
	});

	function layout_center_refreshTab(title) {
		$('#layout_center_tabs').tabs('getTab', title).panel('refresh');
	}

	function layout_center_addTabFun(opts) {
		var t = $('#layout_center_tabs');
		
		/* 验证一个特定的选项卡面板是否存在 */
		if (t.tabs('exists', opts.title)) {
			/* 存在就选中 */
			t.tabs('select', opts.title);
		} else {
			/* 大于等于18个tab页就不允许添加了 */
			if(t.tabs("tabs").length >= 18) {
				$.messager.alert('提示','添加的tab页不能超过18个,请关闭一些tab页！');
			} else {
				/* 不存在就添加 */
				t.tabs('add', opts);
			}
		}
	}
</script>
<div id="layout_center_tabs" style="overflow: hidden;">
	<div title="首页" data-options="href:'${pageContext.request.contextPath}/pages/index.jsp'"></div>
</div>
<div id="layout_center_tabsMenu" style="width: 120px;display:none;">
	<div type="refresh">刷新</div>
	<div class="menu-sep"></div>
	<div type="close">关闭</div>
	<div type="closeOther">关闭其他</div>
	<div type="closeAll">关闭所有</div>
</div>