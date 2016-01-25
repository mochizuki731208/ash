<%@ page language="java" import="java.util.*" pageEncoding="GBK"%>
<html>
	<head>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=GBK">
		<link href="otree.css" rel="stylesheet" type="text/css">
		<script type=text/javascript src="common.js"></script>
		<script type=text/javascript src="otree.js"></script>
		<script type=text/javascript src="china.js"></script>
		<script type=text/javascript src="jquery.js"></script>
	</head>
<body>
<div id="div1" style="width: 200px;height: 100px;">333</div>
<script type=text/javascript>
chinaAreas = ([
	{'pid':'-1','text':'�й�','id':'999999','checked':0},
	{'pid':'999999','text':'������','id':'110000','checked':0},
	{'pid':'110000','text':'��Ͻ��','id':'110100','checked':0},
	{'pid':'110100','text':'������','id':'110101','checked':0},
	{'pid':'110100','text':'������','id':'110102','checked':0}
   ]);

var l0 = new Date().getTime();
		var otree = new OTree({
			panel 	: document.body,
			data		: chinaAreas
		});
		otree.paint();
		alert( chinaAreas.length + ':'+ (new Date().getTime()-l0));

		function ClickEvent(node){
			alert('NodePath: '+node.getPath() + ' text:'+node.attributes.text);
		}
		otree.addListener('click',ClickEvent);

		function fdelete(){
			var node = otree.getSelectedNode();
			if(node){
				otree.removeNode(node)
			}
		}

		function flook(){
			var node = otree.getSelectedNode();
			if(node)
				alert(
					'id:' +	node.attributes.id + '; ' +
					'pid:' +	node.attributes.pid + '; ' +
					'text:' +	node.attributes.text + '; ' 
				);
		}

		function fadd(){

			var json = document.getElementById('testItem').value;
			try{
				eval( 'var item=(' + json + ')');
				//otree.appendNode(item);				
			}catch(e){
				alert('��ӵĽڵ���������');
			}
			otree.updateNode(item);
      /*
		var node = otree.getSelectedNode();
		if(node!=null){
			var text = window.prompt('����ڵ�����!','')
			eval( 'var item=(' + json + ')');
			if(text){
				var newNode = new TreeNode({text:text});
				node.appendChild(newNode);
			}
		}else{
			alert('û��ѡ�нڵ�')
		}
       */
		}
		function fChecked(){
			alert(otree.getCheckeds('id'));
			alert(otree.getCheckeds('text'));
		}
		//window.onunload=function(){otree.destroy();};

</script>
</body>
