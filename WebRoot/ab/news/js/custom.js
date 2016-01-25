$().ready(function(){
	$("#chk-jjxs").click(function(){			
		if($(this).is(":checked")){				
			$(".classify_content_jing").show();
			$(".classify_content").hide();
		}else{				
			$(".classify_content_jing").hide();
			$(".classify_content").show();
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
})