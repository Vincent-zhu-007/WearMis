/**
 * 操作成功
 */
function actionSuccess(){
	window.parent.$.messager.show( {
		title : "提示信息",
		msg : "操作成功！",
		timeout : 3000,
		showType : 'show'
	});	
}

/**
 * 操作失败
 */
function actionFailure(){
	window.parent.$.messager.alert("错误信息","操作失败！请与系统管理员联系。",'error');
}

function actionCustFailure(message){
	window.parent.$.messager.alert("错误信息",message,'error');
}


function actionSuccessChild(){
	window.$.messager.show( {
		title : "提示信息",
		msg : "操作成功！",
		timeout : 3000,
		showType : 'show'
	});	
}

/**
 * 操作失败
 */
function actionFailureChild(){
	window.$.messager.alert("错误信息","操作失败！请与系统管理员联系。",'error');
}