function huxi(huxishijian,jiangeshijian) {
	var nowimg = 0;

	var timer = setInterval(youanniuyewu,jiangeshijian);

	$("#top3").mouseenter(
		function() {
			clearInterval(timer);
		}
	);

	$("#top3").mouseleave(
		function() {
			clearInterval(timer);
			timer = setInterval(youanniuyewu,jiangeshijian);
		}
	);

	// $("#huxi .anniu .rightbut").click(youanniuyewu);

	function youanniuyewu(){
		if(!$(".banner ul li").is(":animated")){

			$(".banner ul li").eq(nowimg).fadeOut(huxishijian);

			if(nowimg < $(".banner ul li").length - 1){
				nowimg = nowimg + 1;
			}else{
				nowimg = 0;
			}

			$(".banner ul li").eq(nowimg).fadeIn(huxishijian);

			$(".banner ul li").eq(nowimg).addClass("cur").siblings().removeClass("cur");
		}
	}

	$(".banner ul li .anniu").click(
		function(){
			if(!$(".banner ul li").is(":animated")){
				$(".banner ul li").eq(nowimg).fadeOut(huxishijian);

				if(nowimg > 0){
					nowimg = nowimg - 1;
				}else{
					nowimg = $(".banner ul li").length - 1;
				}

				$(".banner ul li").eq(nowimg).fadeIn(huxishijian);

				$(".banner ul li").eq(nowimg).addClass("cur").siblings().removeClass("cur");
			}
		}
	);

	// $("#huxi .dianul li").click(
	// 	function(){

	// 		$(".banner ul li").eq(nowimg).fadeOut(huxishijian);
	// 		nowimg = $(this).index();	

	// 		$(".banner ul li").eq(nowimg).fadeIn(huxishijian);

	// 		$(".banner ul li").eq(nowimg).addClass("cur").siblings().removeClass("cur");
	// 	}
	// );
}