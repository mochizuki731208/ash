$(function(){
	InitTopMenu();
	tabClose();
	tabCloseEven();
//	$('#mainPanle').height($("#mainPanle").height()-100);
	$('#tabs').height( $('#mainPanle').height());
	$('#tabs').find('iframe').height( $('#mainPanle').height()-50);
})
function InitTopMenu(){
	$.ajax({
		url: basePath+"/25console/portal",
		data:{flag: 1},
		type: "POST",
		dataType: "json",
		cache: false,
		success: function(data) {
			var headerMenus = $(".headerMenus");
			headerMenus.empty();
			 $.each(data, function(j, o) {
				 headerMenus.append($('<li class=\"' + o.css + '\"><a href=\"javascript:void(0)\" onclick="ReInitLeftMenu(\'' + o.id + '\',\'' + o.mc +'\')">' + o.mc + '</a></li>'));
//				 InitLeftMenu(o.id,o.mc,o.subMenu);
				 //子菜单的处理
				 console.info(o);
				 var menulist ='<ul id="menu">';//alert(o.exta);
			     $.each(o.exta, function(jj, oo) {
					menulist += '<li><div><a rel="'+oo.id+'" ref="'+oo.menu_url+'" href="javascript:void(0)"><span class="nav">' + oo.mc + '</span></a></div></li> ';
			     })
			     menulist += '</ul>';
			     $('#nav').accordion('add', {
			            title: o.mc,
			            content: menulist
			      });
			 })
			 
			 $('.easyui-accordion li a').bind("click",function(){
				var tabTitle = $(this).children('.nav').text();//获取超链里span中的内容作为新打开tab的标题
				var url = $(this).attr("ref");
				var menuid = $(this).attr("rel");//获取超链接属性中ref中的内容
				addTab(tabTitle,url);//增加tab
				$('.easyui-accordion li div').removeClass("selected");
				$(this).parent().addClass("selected");
			 }).hover(function(){
				$(this).parent().addClass("hover");
			 },function(){
				$(this).parent().removeClass("hover");
			 });
			 var panels = $('#nav').accordion('panels');
			 var t = panels[0].panel('options').title;
			 $('#nav').accordion('select', t);
			 
			 headerMenus.append($('<li class="homepage"><a href=\"javascript:void(0)\" onclick="showTLLog(\'\')">语音盒</a></li>'));
			 headerMenus.append($('<li class="home"><a href=\"/25console/loginout\">退出系统</a></li>'));
		}
	});
}
function ReInitLeftMenu(id,title){
	 var panels = $('#nav').accordion('panels');
	 $.each(panels,function(i,obj){
		 if(obj.panel('options').title==title){
			 $('#nav').accordion('select', title);
		 }
	 })
	 
}
//初始化左侧
function InitLeftMenu(firstId,firstTitle,data) {
	$("#nav").accordion({animate:false});//为id为nav的div增加手风琴效果，并去除动态滑动效果
//	$.ajax({
//		url: basePath + "/25console/left/" + firstId,
//		data:{},
//		type: "POST",
//		dataType: "json",
//		cache: false,
//		success: function(data) {
			var menulist ='';
			menulist +='<ul id="menu">';
	        $.each(data, function(j, o) {
				menulist += '<li><div><a rel="'+o.id+'" ref="'+o.menu_url+'" href="javascript:void(0)"><span class="nav">' + o.mc + '</span></a></div></li> ';
	        })
			menulist += '</ul>';
	        $('#nav').accordion('add', {
	            title: firstTitle,
	            content: menulist
	        });
			$('.easyui-accordion li a').bind("click",function(){
				var tabTitle = $(this).children('.nav').text();//获取超链里span中的内容作为新打开tab的标题
				var url = $(this).attr("ref");
				var menuid = $(this).attr("rel");//获取超链接属性中ref中的内容
				addTab(tabTitle,url);//增加tab
				$('.easyui-accordion li div').removeClass("selected");
				$(this).parent().addClass("selected");
			}).hover(function(){
				$(this).parent().addClass("hover");
			},function(){
				$(this).parent().removeClass("hover");
			});
			 var panels = $('#nav').accordion('panels');
			 var t = panels[0].panel('options').title;
			 $('#nav').accordion('select', t);
//		}
//	});
}

function addTab(subtitle,url){
	if(!$('#tabs').tabs('exists',subtitle)){
		$('#tabs').tabs('add',{
			title:subtitle,
			content:createFrame(url),
			closable:true
		});
	}else{
		$('#tabs').tabs('select',subtitle);
		$('#mm-tabupdate').click();
	}
	tabClose();
}
function createFrame(url)
{
	var s = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:98%;"></iframe>';
	return s;
}
function tabClose()
{
	/*双击关闭TAB选项卡*/
	$(".tabs-inner").dblclick(function(){
		var subtitle = $(this).children(".tabs-closable").text();
		$('#tabs').tabs('close',subtitle);
	})
	/*为选项卡绑定右键*/
	$(".tabs-inner").bind('contextmenu',function(e){
		$('#mm').menu('show', {
			left: e.pageX,
			top: e.pageY
		});

		var subtitle =$(this).children(".tabs-closable").text();

		$('#mm').data("currtab",subtitle);
		$('#tabs').tabs('select',subtitle);
		return false;
	});
}

//绑定右键菜单事件
function tabCloseEven()
{
	//刷新
	$('#mm-tabupdate').click(function(){
		var currTab = $('#tabs').tabs('getSelected');
		var url = $(currTab.panel('options').content).attr('src');
		if(url=="" || url==undefined || url=="undefined"){
			url=basePath + "/25console/inforeminder";//消息提醒
		}
		$('#tabs').tabs('update',{
			tab:currTab,
			options:{
				content:createFrame(url)
			}
		})
	})
	//关闭当前
	$('#mm-tabclose').click(function(){
		var currtab_title = $('#mm').data("currtab");
		$('#tabs').tabs('close',currtab_title);
	})
	//全部关闭
	$('#mm-tabcloseall').click(function(){
		$('.tabs-inner span').each(function(i,n){
			var t = $(n).text();
			$('#tabs').tabs('close',t);
		});
	});
	//关闭除当前之外的TAB
	$('#mm-tabcloseother').click(function(){
		$('#mm-tabcloseright').click();
		$('#mm-tabcloseleft').click();
	});
	//关闭当前右侧的TAB
	$('#mm-tabcloseright').click(function(){
		var nextall = $('.tabs-selected').nextAll();
		if(nextall.length==0){
			//msgShow('系统提示','后边没有啦~~','error');
			alert('后边没有啦~~');
			return false;
		}
		nextall.each(function(i,n){
			var t=$('a:eq(0) span',$(n)).text();
			$('#tabs').tabs('close',t);
		});
		return false;
	});
	//关闭当前左侧的TAB
	$('#mm-tabcloseleft').click(function(){
		var prevall = $('.tabs-selected').prevAll();
		if(prevall.length==0){
			alert('到头了，前边没有啦~~');
			return false;
		}
		prevall.each(function(i,n){
			var t=$('a:eq(0) span',$(n)).text();
			$('#tabs').tabs('close',t);
		});
		return false;
	});
}

function showTLLog(value) {
	var obj = "";
	if(value=="" || null==value){
		obj=document.getElementById("logValue").value;
	}else{
		obj = value;
	}
	if(obj=="0" || obj==0) {
		document.getElementById("logValue").value="1";
		document.getElementById("callLayer").style.display="block";
	}else if(obj=="1") {
		document.getElementById("logValue").value="0";
		document.getElementById("callLayer").style.display="none";
	}
}