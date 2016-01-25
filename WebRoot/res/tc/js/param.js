/**
 * JS对象转URL参数
 * param 将要转为URL参数字符串的对象
 * key URL参数字符串的前缀
 * encode true/false 是否进行URL编码,默认为true
 * 
 * return URL参数字符串
 */
var urlEncode = function (param, key, encode) {
  if(param==null) return '';
  var paramStr = '';
  var t = typeof (param);
  if (t == 'string' || t == 'number' || t == 'boolean') {
    paramStr += '&' + key + '=' + ((encode==null||encode) ? encodeURIComponent(param) : param);
  } else {
    for (var i in param) {
      var k = key == null ? i : key + (param instanceof Array ? '[' + i + ']' : '.' + i);
      paramStr += urlEncode(param[i], k, encode);
    }
  }
  return paramStr;
};

/*
var obj={name:'tom','class':{className:'class1'},classMates:[{name:'lily'}]};
console.log(urlEncode(obj));
//output: &name=tom&class.className=class1&classMates[0].name=lily
console.log(urlEncode(obj,'stu'));
//output: &stu.name=tom&stu.class.className=class1&stu.classMates[0].name=lily
*/

/**
 * JS对象转URL参数
 */
var parseParam=function(param, key){
	  var paramStr="";
	  if(param instanceof String||param instanceof Number||param instanceof Boolean){
	    paramStr+="&"+key+"="+encodeURIComponent(param);
	  }else{
	    $.each(param,function(i){
	      var k=key==null?i:key+(param instanceof Array?"["+i+"]":"."+i);
	      paramStr+='&'+parseParam(this, k);
	    });
	  }
	  return paramStr.substr(1);
	};
	
	/*
	 //test
	var obj={name:'tom','class':{className:'class1'},classMates:[{name:'lily'}]};
	parseParam(obj);
	//output: "name=tom&class.className=class1&classMates[0].name=lily"
	parseParam(obj,'stu');
	//output: "stu.name=tom&stu.class.className=class1&stu.classMates[0].name=lily"
	*/