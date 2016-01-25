$().ready(function(){
	$("#chk-jjxs").click(function(){			
		if($(this).is(":checked")){				
			$(".classify").removeClass("canpinByimg").addClass("canpinBylist");
		}else{				
			$(".classify").removeClass("canpinBylist").addClass("canpinByimg");
		}
	});
	
	$(".main-tab").click(function(){
		var clickTab = $(this).attr("tab-name");
		var curActiveTab = $(".tab-content.active").attr("tab-name");
		if(curActiveTab != undefined && curActiveTab != ""){
			if(curActiveTab != clickTab){
				$(".tab-content").removeClass("active");
				$(".tab-content[tab-name=" + clickTab + "]").addClass("active");
			}
		}else{
			$(".tab-content[tab-name=" + clickTab + "]").addClass("active");
		}
	});
	
	var top = $('[class="tab-content active"]').offset().top;
	$(window).scroll(function() {
		var scrolls = $(this).scrollTop();
		if (scrolls >= top) {
			$('#fixed_msg_box>div').css({
				position:'absolute',
				top: scrolls - top - 1
			});	
			//msgbox.parent().css({paddingTop:element.outerHeight()});
		}else {
			$('#fixed_msg_box>div').css({
				position: 'static'
			});	
			//msgbox.parent().css({paddingTop:0});
		}
	});
	
	var _height = $(window).height();
	var leanHight = $('#msgDiv').height();
	var _top = _height > leanHight? (_height - leanHight)/2 : 0;
	$('#leaveMsgBtn').leanModal({ top: _top, overlay: 0.45, closeButton: ".closebtn" });
	
})