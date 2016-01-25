$(document).ready(function(){
	var $liCur = $("#top2 .banxin ul li"),
	  curP = $liCur.position().left,
	  curA = $(".curBg").position().left,
	  curW = $liCur.outerWidth(true),
	  $slider = $(".curBg"),
	  $navBox = $("#top2 .banxin");
	 $targetEle = $("#top2 .banxin ul li a"),
	$slider.animate({
	  "left":curA,
	  "width":curW
	});
	$targetEle.mouseenter(function () {
	  var $_parent = $(this).parent(),
		_width = $_parent.outerWidth(true),
		posL = $_parent.position().left;
	  $slider.stop(true, true).animate({
		"left":posL,
		"width":_width
	  }, "fast");
	});
	$navBox.mouseleave(function (cur, wid) {
	  cur = curA;
	  wid = curW;
	  $slider.stop(true, true).animate({
		"left":cur,
		"width":wid
	  }, "fast");
	});
})
