function formatterDate(val) {
    var date = new Date(val);
    return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();
}

function formatterDateTime(val) {
    var date = new Date(val);
    return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
}

function formatterStatusCode(codeName){
	if(codeName == "Y"){
		return "启用";
	}else if (codeName == "N") {
		return "禁用";
	}
}

/*两边去空格*/
function trimSpace(str){ 
	alert(str1);
    return str.replace(/(^\s*)|(\s*$)/g, "");
}

/*左边去空格*/
function ltrimSpace(str){
    return str.replace(/(^\s*)/g, "");
}

/*右边去空格*/
function rtrimSpace(str){
    return str.replace(/(\s*$)/g, "");
}

/*两边去双引号*/
function trimDoubleQuotation (str){ 
    return str.replace(/(^\"*)|(\"*$)/g, "");
}

/*两边去逗号*/
function trimComma (str){ 
    return str.replace(/(^\,*)|(\,*$)/g, "");
}

