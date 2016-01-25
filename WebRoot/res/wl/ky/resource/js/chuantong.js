function chuantong(jiangeshijian,yundongshijian,tupiankuandu){
	var nowimg = 0;	
	
	$("#chuantong .tuul li").eq(0).clone().appendTo("#chuantong .tuul");

	var timer = setInterval(youanniuyewu,jiangeshijian);

	$("#chuantong").mouseenter(
		function(){
			clearInterval(timer);
		}
	);

	$("#chuantong").mouseleave(
		function(){
			clearInterval(timer);
			timer = setInterval(youanniuyewu,jiangeshijian);
		}
	);

	$("#chuantong .anniu .rightbut").click(youanniuyewu);
	function youanniuyewu(){
		if(!$(".tuul").is(":animated")){
			if(nowimg < $("#chuantong .tuul li").length - 2){
				nowimg = nowimg + 1;
				huan();	
			}else{
				nowimg = 0;

				$("#chuantong .tuul").animate(
					{
						"left":-tupiankuandu * ($("#chuantong .tuul li").length - 1)
					}
					,yundongshijian
					,function(){
						$("#chuantong .tuul").css("left",0);
					}
				);

				$("#chuantong .dianul li").eq(nowimg).addClass("cur").siblings().removeClass("cur");
			}
		}
	}

	$("#chuantong .anniu .leftbut").click(
		function(){
			if(!$(".tuul").is(":animated")){
				if(nowimg > 0){
					nowimg = nowimg - 1;
					huan();	
				}else{
					nowimg = $("#chuantong .tuul li").length - 2;

					$("#chuantong .tuul").css("left",-tupiankuandu*($("#chuantong .tuul li").length - 1));
					$("#chuantong .tuul").animate(
						{
							"left":-tupiankuandu * ($("#chuantong .tuul li").length - 2)
						}
					,yundongshijian);

					$("#chuantong .dianul li").eq(nowimg).addClass("cur").siblings().removeClass("cur");
				}
			}	
		}
	);

	$("#chuantong .dianul li").click(
		function(){
			if(!$(".tuul").is(":animated")){
				nowimg = $(this).index();
				huan();
			}
		}
	);

	function huan(){

		$("#chuantong .tuul").animate(
			{
				"left":nowimg * -tupiankuandu
			}
		,yundongshijian);

		$("#chuantong .dianul li").eq(nowimg).addClass("cur").siblings().removeClass("cur");
	}

	

}

$(function(){
	$(".loginSta").mouseover(function(){
		$(this).removeClass();
		$(this).addClass("loginChange");
	}).mouseout(function(){
		$(this).removeClass();
		$(this).addClass("loginSta");
	});
});
