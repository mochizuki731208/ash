$(function() {
	//我要悬赏
	$("input[name='orderStyle']").eq(0).attr("checked", true);
	$("input[name='orderStyle']").on('input propertychange', function() {
		chooseStyle();
	});//.change(chooseStyle);
	
	$("input[name='moreMoney']").on('input propertychange', function() {
		calcTotal();
	});//.change(calcTotal);
	if(zb == 'Y'){
		$("#zb_div1").hide();
		$("#xs_div").hide();
	} else {
		$("#xs_div").hide();
	}
	
	
	
	//$("#goods_volumn").change(calcTotal);
	$("#goods_volumn").on('input propertychange', function() {
		
	   
	 //零担
	 if(typeSelIndex==0){
	 //计算货物价格
		var volumn=$("#goods_volumn").val();
		
		var km=$("#kiloRst").html();
		if(eval(volumn-0.5)<=0){
			 
			    $("#total").html(parseInt(km*2*0.5+28).toFixed(2));
				 //将下面的悬赏金额或者我要出价方式改下
			 sysOrder= $("#total").html();
			return;
			  }
			  
			
			 
			 if(eval(volumn-1)>=0){
				 
			var less=volumn-1;
		
			 $("#total").html(parseInt(km*2*less+km*3.5*1+28).toFixed(2));
			  //将下面的悬赏金额或者我要出价方式改下
			 sysOrder= $("#total").html();
			 return;
			}
		
		if(eval(volumn-0.6)>0&&eval(volumn-1)<0){
			
			  $("#total").html(parseInt(km*3.5*volumn+28).toFixed(2));
			   //将下面的悬赏金额或者我要出价方式改下
			 sysOrder= $("#total").html();
			 return;
			 }
			 
			
	 }
	});
	$("#weight").on('input propertychange', function() {
		//calcTotal();
	});//.change(calcTotal);
	/**prepare**/
	$(".time_input").datetimepicker({
		showSecond : false,
		timeFormat : 'hh:mm:ss',
		stepHour : 1,
		stepMinute : 1,
		stepSecond : 1
	});

	$(".vname").blur(function() {
		
		if (!/^\S{2,10}$/.test($(this).val()))
			addPmt(this, null, '名称为长度2-10的非空字符');
		else
			addPmt(this, null);
		//获得收货人姓名
		if($("#rcvName").val().length>0){
			
			namejudge=$(this).val();
			
		}
		
	});
	$(".vphone").blur(function() {
		if (!/^\d{11}$/.test($(this).val()))
			addPmt(this, null, '电话为长度11的数字');
		else
			addPmt(this, null);
	});
	$(".vmoney").blur(function() {
		if (!/^\d+(\.\d{1,2})?$/.test($(this).val()))
			addPmt(this, $(this).next(".pmt"), '金额为整型或小数点后2位的浮点型');
		else
			addPmt(this, $(this).next(".pmt"));
	});
	 /***出价金额 **/
    $(".unit-detail-amount-control .amount-down").click(function(){
     
     	 var inputobj =  $(this).parent().find("input"); 
    	 var val = inputobj.val(); 
    	
    	 var minval =1;
    	 if(window.tmpSum)
    	 	 minval = parseFloat(tmpSum*0.9);
    	 if(!minval ||minval<0)minval=0;
    	 
    	 if(val<=0 || parseFloat(val) <= minval){
    	 	 val = minval;
    		 $(this).parent().find("#errorPrice").show();
    		 $(this).hide();
    	 }else{
    		 val = val - 1;
    	 } 
       inputobj.val(val);
    });
    $(".unit-detail-amount-control .amount-up").click(function(){
    	var inputobj =  $(this).parent().find("input");  
        inputobj.val(inputobj.val()*1+1);
         $(".unit-detail-amount-control .amount-down").show();
         $(this).parent().find("#errorPrice").hide();
    });
});
var stateMoney = 0;
//添加收货地址
function addRcv() {
	var arr = $(".more_rcv");
	for ( var i = 0; i < arr.length; i++) {
		if ($(arr[i]).css("display") == 'none') {
			$(arr[i]).show();
			return;
		}
	}
}
//移除收货地址
function removeRcv(a) {
	info['to' + a] = undefined;
	var i = a - 2;
	$("#rcvAddr" + a).val("");
	$("#rcvName" + a).val("");
	$("#rcvPhone" + a).val("");
	$(".more_rcv").eq(i).hide();
	tryDrive();
}

/*******************计算车辆内容**************************************/

function calcTrans() {
	if (typeSelIndex != 0)
		return;
	info['carType'] = "零担";
	info['basePrice'] = 28;
	info['overPrice'] = 3;
	info['nightPrice'] = 0;
	zb = '${(zb)!}';
	tryDrive();
}

function calcCar() {
	
	if (typeSelIndex == 0) {
		calcTrans();
		return;
	}
	if (typeSelIndex < 0 || selIndex < 0)
		return;
	info['carType'] = carNames[selIndex];
	info['basePrice'] = parseInt(basePrice[selIndex]);
	info['overPrice'] = parseInt(overPrice[selIndex]);
	info['nightPrice'] = parseInt(nightPrice[selIndex]);
	//$("#carRst").html(info['carType']);
	
	
	//选择车长
	if (carLong[selIndex] != '') {
		var radioStr = '';
		var ccArr = carLong[selIndex].split(",");
		for ( var i = 0; i < ccArr.length; i++) {
			var tmpChecked = 'checked="checked"';
			radioStr += '&nbsp;<input type="radio" name="cc" value="'
					+ ccArr[i] + '" '
					+ (info['cc'] == ccArr[i] ? tmpChecked : '') + '/>&nbsp;'
					+ ccArr[i] + '米&nbsp;&nbsp;';
		}
		$.dialog({
			title : '选择车长',
			content : radioStr,
			lock : true,
			resize : false,
			width : 300,
			height : 100,
			cover : true,
			max : false,
			min : false,
			cancel : true,
			button : [ {
				name : '确定',
				callback : function() {
					if ($("input[name='cc']:checked").length == 0) {
						alert("请选择车长!");
						return false;
					}
					info['cc'] = $("input[name='cc']:checked").val();
					//$(".carImg").eq(selIndex).find("li:last").html("车长：&nbsp;"+info['cc']+"米");

					//直接走物流专线的流程
					zb = 'Y';
					//$("#zb_div1").show();
					//$("#xs_div").hide();
				},
				focus : true
			} ],
			cancelVal : '关 闭'
		});//弹出框
	} else {
		zb = '${(zb)!}';
	}
	tryDrive();
}
function calcTotal() {
	
	//日历点击的时候添加sum的属性保证其不是NAN
		info['sum']=$("#total").html();
	
	//判断是否选择了我要出价
  
  if(offerPrice!=0){
  
  $("#onePrice").val(""); 

  }

	
	
	
	
	if (zb == 'Y' && $("#isXS:checked").length > 0) {//物流专线或超过100公里的物流，悬赏模式
		info['sum'] = parseFloat($("#xsMoney").val());
	
			$("#total").html(info['sum']);
			
	
		
		if (info['kilo']) {
			$("#kiloRst").html(info['kilo']);
		}
		return;
	} else if (zb == 'Y' && $("#isXS:checked").length == 0) {//物流专线或超过100公里的物流，招标模式
		info['sum'] = 0;
			
		if($("#minPrice").val()==""&&$("#maxPrice").val()==""){
			
			$("#total").html("_");
			return ;
		}
		
			$("#total").html($("#minPrice").val() + " - " + $("#maxPrice").val());
		
		if (info['kilo']) {
			$("#kiloRst").html(info['kilo']);
		}
		return;
	}
	if (typeSelIndex < 0)
		return;
	if (typeSelIndex > 0 && selIndex < 0)
		return;
	var sum = 0;
	if (typeSelIndex == 0) {
		if (info['basePrice']) {
			sum += info['basePrice'];
		}
		var vol = $("#goods_volumn").val();
		if (info['kilo'] && vol) {
			sum += (vol <= 1.5 ? vol * 3 : (vol - 1.5) * 1 + 1.5 * 3)
					* info['kilo'];
		}
	} else {
		if (info['basePrice']) {
			sum += info['basePrice'];
		}
		if (info['kilo'] && info['overPrice']) {
			sum += (info['kilo'] <= 10 ? 0 : info['kilo'] - 10)
					* info['overPrice'];
		}
	}
	if (info['kilo']) {
		$("#kiloRst").html(info['kilo']);
	}
	info['sum'] = sum;
	//----------------------
	if ($("#lift").attr("checked")) {
		info['sum'] += 70;
	} else {
		info['sum'] += 40;
	}
	if ($("#huidan_price").attr("checked")) {
		info['sum'] += 3;
	}
	if ($("#service_price").attr("checked")) {
		info['sum'] += 10;
	}
	//拆装
	if ($("#dismounting").attr("checked")) {
		info['sum'] += 3;
	}
	if ($("#sendTime").val() != '' && info['night_price'] != '') {
		
		var h = $("#sendTime").val().split(" ")[1].split(":")[0];
		h = parseInt(h);
		if (h >= 23 || h < 6) {
			info['sum'] += info['night_price'];
		}
		
	}
	//----------------------
	if ($("#isXS:checked").length > 0
			&& $("input[name='moreMoney']:checked").length > 0) {
		var more = $("input[name='moreMoney']:checked").val();
		info['sum'] += parseInt(more);
	}
	
	
	$("#total").html(info['sum']);
	//alert(info['sum']+'真棒');
	if(info['sum']>0) tmpSum = info['sum'];
	var val = $("input[name='orderStyle']:checked").val();
	if(val==3 && info['onePrice']!=""){
		if(window.tmpSum && parseFloat(info['onePrice'])<parseFloat(tmpSum*0.2)){
			$('#errorMoney').html("我要出价是在系统定价的基础上能下调20%。");
		}else{
			
		
			info['sum'] = info['onePrice'];
			$("#total").html(info['sum']);
			$('#errorMoney').html("");
		}
	}else{
		$('#errorMoney').html("");
	}
}
var namejudge;

function resetInfo() {





	if (typeSelIndex < 0) {
		alert("请选择类型！");
		return false;
	}
	if (typeSelIndex > 0 && selIndex < 0) {
		alert("请选择车型！");
		return false;
	}
	info['sendTime'] = $("#sendTime").val();
	
	if (info['sendTime'] == '') {
		alert("请选择发货的时间");
		return false;
	}
	info['from'] = $("#sendAddr").val();	
	if (!info['from']) {
		alert('请通过联想提示选入发货地址！');
		return false;
	}
	info['to'] = $("#rcvAddr").val();
	if (!info['to']) {
		alert('请通过联想提示选入收货地址！');
		return false;
	}
	if (!info['kilo']) {
		manKilo();
		return false;
	}
	//不是这个位置啊
	//calcTotal();
	info['sendName'] = $("#sendName").val();
	info['sendPhone'] = $("#sendPhone").val();
	info['rcvName'] = $("#rcvName").val();
	info['rcvPhone'] = $("#rcvPhone").val();
	for ( var i = 2; i <= 5; i++) {
		info['rcvName' + i] = $("#rcvName" + i).val();
		info['rcvPhone' + i] = $("#rcvPhone" + i).val();
		info['rcvAddr' + i] = $("#rcvAddr" + i).val();
	}
	if (!/^\d{11}$/.test(info['sendPhone'])) {
		alert("请填写正确的11位发货人电话号码");
		return false;
	}
	if (!/^\d{11}$/.test(info['rcvPhone'])) {
		alert("请填写正确的11位收货人电话号码");
		return false;
	}
	//竞标价格
	var over=(sysOrder)/($("#kiloRst").html())*100;
	
	if($("#minPrice").val()<over||$("#maxPrice").val()<over){
		alert("司机竞标价格不能低于100公里的预算费用!!");
		return false;
	}
	
	
	//姓名判断--发货
	if($("#rcvName").val().length<1){
		alert("您输入的收货人姓名最少2个长度!!");
		return false;
		
	}
	
	if(namejudge.length<1){
		alert("您输入的发货人姓名最少2个长度!!");
		return false;
		
	}
	
	//悬赏、竞价
	info['orderStyle'] = $("input[name='orderStyle']:checked").val();
	info['minPrice'] = $("#minPrice").val();
	info['maxPrice'] = $("#maxPrice").val();
	info['onePrice'] = $("#onePrice").val();
	//---------------------
	info['weight'] = $("#weight").val();
	if (info['weight'] == '') {
		alert("请输入货物的大约重量");
		return false;
	}
	if ($("#wt_unit").val() == "2") {//吨的换算
		info['weight'] = parseInt($("#weight").val()) * 1000;
	}
	info['people'] = $("#people").val();
	info['goodsType'] = $("#goodsType").val();
	info['goods_volumn'] = $("#goods_volumn").val();
	info['goods_mount'] = $("#goods_mount").val();
	if (info['goods_volumn'] == '') {
		alert("请输入货物的大约体积");
		return false;
	}
	if (!/^\d+$/.test(info['goods_mount'])) {
		return true;
	}
	info['lift'] = $("#lift").attr("checked") ? 1 : 0;
	info['moreMoney'] = $("input[name='moreMoney']:checked").length > 0 ? $(
			"input[name='moreMoney']:checked").val() : 0;
	info['huidan_price'] = $("#huidan_price").attr("checked") ? $(
			"#huidan_price").val() : 0;
	info['service_price'] = $("#service_price").attr("checked") ? $(
			"#service_price").val() : 0;
	info['payType'] = $("input[name='payType']:checked").val();
	if (!info['payType'] || info['payType'] == '') {
		alert("请选择支付类型!");
		return false;
	}
	info['remark'] = $("#remark").val();
	//-----------------------
	if (!info['sum'] || info['sum'] <= 0) {
		if(parseFloat($("#xsMoney").val())>0){
			info['sum'] = $("#xsMoney").val();
		}else{
			
			$("#zb_div1").hide();
			$("#xs_div").hide();
			return true;
		}
	}
	//是这个位置啊
	calcTotal();
	
}
function resetSInfo() {
	info['basePrice'] = 1; 
	info['overPrice'] = 1;
	info['nightPrice'] = 0;
	info['carType'] = "零担";
	info['mId'] = $('#mId').val();
	if (typeSelIndex < 0) {
		alert("请选择类型！");
		return false;
	}
	if (typeSelIndex > 0 && selIndex < 0) {
		alert("请选择车型！");
		return false;
	}
	info['sendTime'] = $("#sendTime").val();
	
	if (info['sendTime'] == '') {
		alert("请选择发货的时间");
		return false;
	}
	info['from'] = $("#sendAddr").val();	
	if (!$('#smth_no').is(':checked') && !info['from']) {
		alert('请通过联想提示选入发货地址！');
		return false;
	}
	if ($('#smth_no').is(':checked')){
		info['from'] = $("#sp").val();	
	}
	info['to'] = $("#rcvAddr").val();
	if (!$('#shsm_no').is(':checked') && !info['to']) {
		alert('请通过联想提示选入收货地址！');
		return false;
	}
	if ($('#shsm_no').is(':checked')){
		info['to'] = $("#ep").val();	
	}
	if ((!$('#smth_no').is(':checked') || !$('#shsm_no').is(':checked')) && !info['kilo']) {
		manKilo();
		return false;		
	}
	info['sendName'] = $("#sendName").val();
	info['sendPhone'] = $("#sendPhone").val();
	info['rcvName'] = $("#rcvName").val();
	info['rcvPhone'] = $("#rcvPhone").val();	
	if (!/^\d{11}$/.test(info['sendPhone'])) {
		alert("请填写正确的11位发货人电话号码");
		return false;
	}
	//悬赏、竞价
	info['orderStyle'] = 1;//$("input[name='orderStyle']:checked").val();
	info['minPrice'] = 0;//$("#minPrice").val();
	info['maxPrice'] = 100;//$("#maxPrice").val();
	info['onePrice'] = 0;//$("#onePrice").val();
	//---------------------
	info['weight'] = $("#weight").val();
	if ($("#wt_unit").val() == "2") {//吨的换算
		info['weight'] = parseInt($("#weight").val()) * 1000;
	}
	info['goodsType'] = $("#goodsType").val();
	info['goods_volumn'] = $("#goods_volumn").val();
	info['goods_mount'] = 0;//$("#goods_mount").val();
	if (info['goods_volumn'] == '' && info['weight'] == '') {
		alert("请输入货物的大约体积或者货物的大约重量");
		return false;
	}
	info['lift'] = 0;//$("#lift").attr("checked") ? 1 : 0;
	info['moreMoney'] = 0;//$("input[name='moreMoney']:checked").length > 0 ? $("input[name='moreMoney']:checked").val() : 0;
	info['huidan_price'] = 0;//$("#huidan_price").attr("checked") ? $("#huidan_price").val() : 0;
	info['service_price'] = 0;//$("#service_price").attr("checked") ? $("#service_price").val() : 0;
	info['payType'] = $("input[name='payType']:checked").val();
	if (!info['payType'] || info['payType'] == '') {
		alert("请选择支付类型!");
		return false;
	}
	info['remark'] = $("#remark").val();
	//-----------------------
	return true;
} 
setInterval(function () {
	var onePrice = $("#onePrice").val();
	var sumPrice = $('#total').html();
	var val = $("input[name='orderStyle']:checked").val();
	if(val==3){
		if(parseFloat(onePrice)>0.0){
			
			if(window.tmpSum && parseFloat(onePrice)<parseFloat(tmpSum*0.9)){
				//$('#errorMoney').html("我要出价是在系统定价的基础上能下调10%。");
				$("#errorPrice").show();
				stateMoney = 1;
			}else{
				info['sum'] = onePrice;
				$("#total").html(onePrice);
				$('#errorMoney').html("");
				stateMoney = 1;
			}
		}else{
			$('#errorMoney').html("");
		}
	}else if(window.tmpSum){
		$("#total").html(tmpSum);
	}else{
			$('#errorMoney').html("");
		}
}, 100);

//计算公里数
function tryDrive() {

	if (info['from'] && info['to']) {
		//driving.search("天府广场", "广福桥北街");
		info['kilo'] = 0;
		currKilo = 0;
		try {
			driving.search(new BMap.Point(info['from_lng'], info['from_lat']),
					new BMap.Point(info['to_lng'], info['to_lat']));
		} catch (e) {
			manKilo();
		}
	} else {
		//防止出错，先进行判断是否是选择了地址栏导致出错
	
		calcTotal();
	}
}
//手动录入公里数
function manKilo() {
	

	alert('由于您的地址无法自动识别，请手动录入大约的公里数');
	$.dialog({
		title : '大约的公里数',
		content : '<input type="text" name="dkilo" id="dkilo" />公里',
		lock : true,
		resize : false,
		width : 300,
		height : 100,
		cover : true,
		max : false,
		min : false,
		cancel : true,
		button : [ {
			name : '确定',
			callback : function() {
				if (!/^\d+$/.test($("#dkilo").val())) {
					alert("请输入正整数的公里数！");
					return false;
				}
				info['kilo'] = $("#dkilo").val();
				
        $("#kiloRst").html(info['kilo']);
				
		 //判断是否大于100公里，如果是则将显示我要出价去掉
      if($("#kiloRst").html()<100){
      document.getElementById("dirverOrder").style.display="none";
     }		
				
				calcTotal();
			},
			focus : true
		} ],
		
		cancelVal : '关 闭'
	});//弹出框
}
/*******************计算车辆内容 end**************************************/
function set100Kilo() {
	if (info['kilo'] >= maxKilo) {
		zb = 'Y';
		$("#zb_div1").hide();
		$("#xs_div").hide();
		//alert("您的行驶里程超过"+maxKilo+"公里，请直接输入一个悬赏价格作为订单价格！");
	}
	calcTotal();
}
//把金钱第一次的保存起来
var sysOrder;
var i=0;
function chooseStyle() {
	
	 if(i==0){	
	//选择之前先把金钱保存起来
	sysOrder= $("#total").html();
	}
	
	 i++;
	var val = $("input[name='orderStyle']:checked").val();
	if (val == 1) {
		
		document.getElementById("errorPrice").style.display="none";
		//隐藏错误提示信息
		 document.getElementById("errorDriverPrice").style.display="none"; 
		//判断是否选择了我要出价
  
  if(offerPrice!=0){
  
  $("#onePrice").val(""); 

  }

		
		  //把系统提示的金钱更改下
		 $("#total").html(sysOrder);
		
		$("#zb_div2").hide();
		$("#zb_div3").hide();
		$("#xs_div").hide();
		$("#minPrice").val("");
		$("#maxPrice").val("");
		$("#someInfo").show();
		if (zb == 'Y') {
			$("#zb_div1").hide();
			$("#xs_div").hide();
		}
		if (info['kilo'] >= maxKilo) {
			zb = 'Y';
			$("#zb_div1").hide();
			$("#xs_div").hide();
		}
	} else if (val == 2) {
		//隐藏错误提示信息
	  document.getElementById("errorDriverPrice").style.display="none"; 
		document.getElementById("errorPrice").style.display="none";
		$("input[name='moreMoney']").attr("checked", false);
		$("#zb_div2").show();
		$("#zb_div3").hide();
		$("#xs_div").hide();
		$("#someInfo").hide();
		if (zb == 'Y') {
			$("#zb_div1").hide();
			$("#zb_div1").val('');
		}
	} else if(val == 3) { 
		if(window.tmpSum)
			$(".unit-detail-amount-control input").val(tmpSum) 
		document.getElementById("errorPrice").style.display="none";
		//隐藏错误提示信息
		 document.getElementById("errorDriverPrice").style.display="none"; 
		 
		 
		//显示我要出价的时候进行判断是否大于100公里
		if($("#kiloRst").html()<100){
		//标准的下降10%价格
	  standard=$("#total").html()*0.9;
		//$("#onePrice").hide();
		$("#outPrice").show();
		$("#upPrice").show();
		$("#downPrice").show();
		$("#outPrice").val($("#total").html()); 
		$("#zb_div3").show();
		
		}else{
		 $("#zb_div3").show();
		 $("#onePrice").show();
		$("#outPrice").hide();
		$("#upPrice").hide();
		$("#downPrice").hide();
		}
		
		$("#zb_div2").hide();
		$("#xs_div").hide();
		$("#someInfo").hide();
		if (zb == 'Y') {
			$("#zb_div1").hide();
			$("#zb_div1").val('');
		}
	} else {
		$("#zb_div1").hide();
		$("#zb_div2").hide();
		$("#zb_div3").hide();
		$("#xs_div").hide();
		$("#someInfo").hide();
	}
}
//添加错误提示
function addPmt(o, p, msg) {
	if (!msg) {
		if (p)
			$(p).html('');
		else {
			$(o).next().remove();
			//$(o).parent().append('<span style="color:red; font-size:10px; padding-left: 0;"><img src="'+webctx+'/res/images/gou.jpg" width="25" height="25"/></span>');
		}
	} else if ($(o).parent().html().indexOf(msg) < 0) {
		if (p)
			$(p).html(
					'<span style="color:red; font-size:8px; padding-left: 0;">'
							+ msg + '</span>');
		else {
			$(o).parent().append(
					'<span style="color:red; font-size:8px; padding-left: 0;">'
							+ msg + '</span>');
		}
	}
}
//
function needCart(ck) {
	if (ck.checked) {
		info['cart'] = ck.value;
	} else {
		info['cart'] = null;
	}
}
//
function checkIt(d) {
	if (d.checked) {
		$(d).parent().find("img").hide();
		$(d).parent().find("span").css("color", "green");
	} else {
		$(d).parent().find("img").show();
		$(d).parent().find("span").css("color", "#ff5801");
	}
}

/**********************类型和车辆选择***************************************/

function transtype(tag, flag, data) {
	
	//预费用先清空
	$("#total").html("");
	typeSelIndex = data;
	
	//判断是否是零担类型，司机报价的显示问题
	if(typeSelIndex==0){
		if($("#kiloRst").html()>100){
			//显示系统竞价
			
			$("#systemOrder").show();
			
		}
		
	}
	
	
	
	
	
	$('.carselect li').removeClass("selected");
	$('.transselect li').removeClass("selected");
	$(tag).addClass("selected");
	if (flag) {
		x = document.getElementById("b2");
		x.style.display = "block";
		$("#kiloRst").html("&nbsp;&nbsp;&nbsp;&nbsp;");
		$("#total").html("&nbsp;&nbsp;&nbsp;&nbsp;");
	} else {
		x = document.getElementById("b2");
		x.style.display = "none";
		calcCar();
	}
	if(data==1) {
		$(".plus").hide();
	} else {
		$(".plus").show();
	}
	
	
	
}
function cartype(tag, data) {
	selIndex = data;
	$('.carselect li').removeClass("selected");
	$(tag).addClass("selected");
	calcCar();
}

/**********************常用地址***************************************/

var oldAddrArr = [];
function selOldAddr() {
	$.getJSON(webctx+'/ab/tc/selOldAddr', function(data) {
		if (!data || data['fail'] || !data.length || data.length < 1) {
			alert('暂无常用地址');
			return false;
		}
		oldAddrArr = data;
		var addrStr = '';
		for ( var i = 0; i < data.length; i++) {
			addrStr += '<br/><input type="radio" name="oldAddr" value="'
					+ data[i]['addr'] + '" '
					+ (i == 0 ? 'checked="checked"' : '') + '/>&nbsp;'
					+ data[i]['addr'] + '<br/><br/>';
		}
		//alert(data.length);
		$.dialog({
			title : '选择常用地址',
			content : addrStr,
			lock : true,
			resize : true,
			width : 600,
			height : 200,
			cover : true,
			max : true,
			min : false,
			cancel : true,
			button : [ {
				name : '确定',
				callback : function() {
					//alert('callback submit');
					var index = $("input[name='oldAddr']").index(
							$("input[name='oldAddr']:checked"));
					var addr = $("input[name='oldAddr']:checked").val();
					var jd = oldAddrArr[index]['jd'];
					var wd = oldAddrArr[index]['wd'];
					$("#sendAddr").val(addr);
					info['from'] = addr;
					info['from_lng'] = jd;
					info['from_lat'] = wd;
					//alert(addr+'_'+jd+'_'+wd);
					tryDrive();
				},
				focus : true
			} ],
			cancelVal : '关 闭'
		});//弹出框
	});
}
function selShAddr(num) {
	$.getJSON(webctx+'/ab/tc/selOldAddr', function(data) {
		if (!data || data['fail'] || !data.length || data.length < 1) {
			alert('暂无常用地址');
			return false;
		}
		oldAddrArr = data;
		var addrStr = '';
		for ( var i = 0; i < data.length; i++) {
			addrStr += '<br/><input type="radio" name="oldAddr' + num
					+ '" value="' + data[i]['addr'] + '" '
					+ (i == 0 ? 'checked="checked"' : '') + '/>&nbsp;'
					+ data[i]['addr'] + '<br/><br/>';
		}
		//alert(data.length);
		$.dialog({
			title : '选择常用地址',
			content : addrStr,
			lock : true,
			resize : true,
			width : 600,
			height : 200,
			cover : true,
			max : true,
			min : false,
			cancel : true,
			button : [ {
				name : '确定',
				callback : function() {
					//alert('callback submit');
					var index = $("input[name='oldAddr" + num + "']").index(
							$("input[name='oldAddr" + num + "']:checked"));
					var addr = $("input[name='oldAddr" + num + "']:checked")
							.val();
					var jd = oldAddrArr[index]['jd'];
					var wd = oldAddrArr[index]['wd'];
					$("#rcvAddr" + num).val(addr);
					info['rcvAddr' + num] = addr;
					info['rcvAddr' + num + '_lng'] = jd;
					info['rcvAddr' + num + '_lat'] = wd;
					//alert(addr+'_'+jd+'_'+wd);
					tryDrive();
				},
				focus : true
			} ],
			cancelVal : '关 闭'
		});//弹出框
	});
}

/*************************************************************/
/*************************************************************/
/*************************************************************/
/*************************************************************/

address_interval = null;
var clear_interval = function() {
	if (address_interval)
		window.clearInterval(address_interval);
};
$(function() {
	/*
	var sendSuggest = new InputSuggest({
		input: document.getElementById('sendAddr'),
		aid: 0
	});
	*/
	addressSearch0 = new AddressSearch(
			currLoc,
			5,
			function(search_data) {
				if (!search_data) {
					// 查询结果为空
					return;
				}
				if (search_keyword != search_data.keyword) {
					// 查询关键词与当前查询不匹配
					return;
				}
				//sendSuggest.refresh(search_data['data']);
				var html = '<div id="address-common">'
						+ '   <div class="address-common-list-wrap">';
				for ( var i = 0; i < search_data['data'].length; i++) {
					var item = search_data['data'][i];
					html += '       <div class="address-common-list" index="'
							+ i
							+ '">'
							+ '           <span class="address-common-icon"></span>'
							+ '           <p class="address-common-address">'
							+ '               <span class="address-common-title">'
							+ item['title']
							+ '</span>'
							+ '               <span class="address-common-detail-address"> 详细地址：<em>'
							+ item['address']
							+ '</em></span>'
							+ '               <span style="display:none;" class="lng">'
							+ item['lng']
							+ '</span>'
							+ '               <span style="display:none;" class="lat">'
							+ item['lat'] + '</span>' + '           </p>'
							+ '       </div>';
				}
				html += '   </div>' + '</div>';
				$("#sendAddr_panel").html(html);
				setPos("#sendAddr_panel #address-common", "#sendAddr");
				$("#sendAddr_panel .address-common-list").click(function() {
					$("#sendAddr").val($(this).find("em").html());
					$("#sendAddr_panel").html('');
					info['from'] = $(this).find("em").html();
					info['from_lng'] = $(this).find(".lng").html();
					info['from_lat'] = $(this).find(".lat").html();
					tryDrive();
				});
			});
	$("#sendAddr").on('focus', function() {
		var that = $(this);
		var last_val = that.val();
		clear_interval();
		address_interval = window.setInterval(function() {
			search_keyword = that.val();
			if (search_keyword && (search_keyword != last_val)) {
				addressSearch0.search(search_keyword);
				last_val = search_keyword;
			}
		}, 100);
	}).on('blur', function() {
		clear_interval();
	});
	
	$("input[name!='sendAddr']").on('focus', function() {
		$("#sendAddr_panel").html("");
	});
	
	/*
	var recSuggest = new InputSuggest({
		input: document.getElementById('rcvAddr'),
		aid: 1
	});
	*/
	addressSearch1 = new AddressSearch(
			currLoc,
			5,
			function(search_data) {
				if (!search_data) {
					// 查询结果为空
					return;
				}
				if (search_keyword != search_data.keyword) {
					// 查询关键词与当前查询不匹配
					return;
				}
				//recSuggest.refresh(search_data['data']);
				var html = '<div id="address-common">'
						+ '   <div class="address-common-list-wrap">';
				for ( var i = 0; i < search_data['data'].length; i++) {
					var item = search_data['data'][i];
					html += '       <div class="address-common-list" index="'
							+ i
							+ '">'
							+ '           <span class="address-common-icon"></span>'
							+ '           <p class="address-common-address">'
							+ '               <span class="address-common-title">'
							+ item['title']
							+ '</span>'
							+ '               <span class="address-common-detail-address"> 详细地址：<em>'
							+ item['address']
							+ '</em></span>'
							+ '               <span style="display:none;" class="lng">'
							+ item['lng']
							+ '</span>'
							+ '               <span style="display:none;" class="lat">'
							+ item['lat'] + '</span>' + '           </p>'
							+ '       </div>';
				}
				html += '   </div>' + '</div>';
				$("#rcvAddr_panel").html(html);
				setPos("#rcvAddr_panel #address-common", "#rcvAddr");
				$("#rcvAddr_panel .address-common-list").click(function() {
					$("#rcvAddr").val($(this).find("em").html());
					$("#rcvAddr_panel").html('');
					info['to'] = $(this).find("em").html();
					info['to_lng'] = $(this).find(".lng").html();
					info['to_lat'] = $(this).find(".lat").html();
					tryDrive();
				});
			});
			
	$("#rcvAddr").on('focus', function() {
		var that = $(this);
		var last_val = that.val();
		clear_interval();
		address_interval = window.setInterval(function() {
			search_keyword = that.val();
			if (search_keyword && (search_keyword != last_val)) {
				addressSearch1.search(search_keyword);
				last_val = search_keyword;
			}
		}, 100);
	}).on('blur', function() {
		clear_interval();
	});
	
	$("input[name!='rcvAddr']").on('focus', function() {
		$("#rcvAddr_panel").html("");
	});
});

///////////////////////////////////////////////////

	//取得绝对位置
    function absPos(node){
        var x=y=0;
        do{
            x+=node.offsetLeft;
            y+=node.offsetTop;
        }while(node=node.offsetParent);  
		return{  
			'x':x,  
            'y':y  
        };        
    }
	//设置绝对位置
    function setPos(target, obj){
        var company = $("#company").val();
		if(parseInt(company)==1){
			$(target).css("left", "0px");
		}else{
			$(target).css("left", $(obj).offset().left);
		}
        $(target).css("top", $(obj).offset().bottom);
    }