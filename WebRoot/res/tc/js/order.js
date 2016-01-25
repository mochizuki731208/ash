var o = {
	getPrice : [], //上门取货相关费用信息
	sendPrice : [], //上门送货相关费用信息
	inputW : 0, // 输入的重货重量
	inputV : 0, // 输入的轻货重量
	wPrice : 0, //重货单价
	vPrice : 0, //轻货单价
	price  : 0, //最低价
	calcPrice : function (){
		var totalPrice = 0;	
		o.inputW = $('#weight').val();
		o.inputV = $('#goods_volumn').val();
		totalPrice1 = o.wPrice * o.inputW 
		totalPrice2 = o.vPrice * o.inputV;
		if(totalPrice1>totalPrice2){
			totalPrice = totalPrice1;
		}else{
			totalPrice = totalPrice2;
		}
		if( totalPrice > 0) {
			if( totalPrice <  o.price ){
				totalPrice = o.price;
			}
			$('#spanPriceId').text(totalPrice+'元');			
			$('input[name=amount]').val(totalPrice);
		}		
		return totalPrice;
	},
	
	getCountyPrice : function (param){
		$.ajax({
			  type: "POST", 
			  url: DTPath+"deliver/ajax.php",
			  dataType : "json",
			  data: param,
			  success: function( data ) {
				  //if( !data ) return false;
				  if( param.cateid === 0 ){
					  o.getPrice = data;
				  }else{
					  o.sendPrice = data;
				  }
				  return true;
			  }
		});
	},
	
	filterNum : function(obj){
	    obj.value = obj.value.replace(/[^\d.]/g,"");
	    obj.value = obj.value.replace(/^\./g,"");
	    obj.value = obj.value.replace(/\.{2,}/g,".");
	    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
	},
	
	showMoreEdit : function(){
		$(".section.second").show();
		$(".aHideMoreEdit,.aShowMoreEdit").toggle();
	},
	
	hideMoreEdit : function(){
		$(".section.second").hide();
		$(".aHideMoreEdit,.aShowMoreEdit").toggle();
	},
	
	
	submit : function(){
		if( $('#send_userName').val() == '' ){
			$('#tip_send_userName').text('请填写联系人');
			$('html, body').animate({scrollTop:$('#send_userName').height()+300}, 500);
			return false;
		}
	
		if( $('#send_senderMobilePhone').val() == '' ){
			$('#tip_send_senderMobilePhone').text('请填写手机');
			$('html, body').animate({scrollTop:$('#send_senderMobilePhone').height()+300}, 500);
			return false;
		}
		if($('#send_senderMobilePhone').val().match(/^1[0-9]{10}$/)) {
			$('#tip_send_senderMobilePhone').text('');
		}else{
			$('#tip_send_senderMobilePhone').text('请输入正确的手机号码!');
			$('html, body').animate({scrollTop:$('#send_senderMobilePhone').height()+300}, 500);
			return false;
		}
		if( $('#goodsName').val() == '' ){
			$('#tip_goodsName').text('请填写货物名称');
			$('html, body').animate({scrollTop:$('#goodsName').height()+300}, 500);
			return false;
		}
		
		if( $('#goodsWeight').val() == '' || $('#goodsVolume').val() == '' ){
			$('#tip_WV').text('请填写货物信息');
			$('html, body').animate({scrollTop:$('#goodsWeight').height()+300}, 500);
			return false;
		}
		
		if( $('#goodsWeight').val() <= 0 || $('#goodsVolume').val() <= 0 ){
			$('#tip_WV').text('值必须大于0');
			$('html, body').animate({scrollTop:$('#goodsWeight').height()}, 500);
			return false;
		}
		
		if($('#arrive_mobile').val()!=''){
			if($('#arrive_mobile').val().match(/^1[0-9]{10}$/)) {
				$('#tip_arrive_mobile').text('');
			}else{
				$('#tip_arrive_mobile').text('请输入正确的手机号码!');
				
				return false;
			}
		}
		if($('#arrive_tel').val()!=''){

		    var re = /^((0\d{2,3}-?\d{7,8})|([4|8]\d{2}-?\d{3}-?\d{4})|([4|8]\d{3}-?\d{3}-?\d{3}))$/
		 
			if($('#arrive_tel').val().match(re)) {
				$('#tip_arrive_tel').text('');
			}else{
				$('#tip_arrive_tel').text('请输入正确的电话号码!');
				
				return false;
			}
		}

		if(  $('input[name="smth"]:checked').val() == '1' ){

			$('#sendAddr').val($('#senderCityName').text()+$('#senderCountySelect').find("option:selected").text()+$("#fake_sendAddress").val());
		}

		if(  $('input[name="shsm"]:checked').val() == '1' ){

			$('#rcvAddr').val($('#receiverCityName').text()+$('#receiverCountySelect').find("option:selected").text()+$("#fake_receiptAddress").val());
		}

		$('#submitform').attr("disabled", true);
		$('#addOrderForm').submit();
		$("#submitform").text('订单提交中...');

	},
	
	times : 60,
	sendCode : function(obj){
		$.ajax({
			  type: "POST", 
			  url: DTPath+"deliver/ajax.php",
			  dataType : "json",
			  data: { action : 'getVerifyCode', mobile : o.mobile },
			  success: function( data ) {
				  if( data && data.flag ){
					  $(obj).html("验证码发送成功");
					  setTimeout(function () {
				        	o.timeout(obj);
				      }, 1300);
				  }else{
					  $(obj).html("验证码发送失败");
					  $(obj).css({'background-color':'gray'});
					  setTimeout(function () {
							$(obj).html("重新发送验证码");
							$(obj).css({'background-color':'#ff6600'});
				      }, 3300);
				  }
				
			  }
		});
		
		
	},
	
	timeout : function(obj){
        if(o.times == 0){
        	$(obj).bind("click", function(){ o.sendCode(this); });
        	//$(obj).attr("html","重新发送验证码").attr("disabled",false);
        	$(obj).css({'background-color':'#ff6600'});
        	$(obj).html("重新发送验证码");
        	o.times = 60;
        	return;
        }else{
        	o.times--;
        	$(obj).unbind("click");
        	//$(obj).attr("html",o.times+"秒后重新发送").attr("disabled",true);
        	$(obj).css({'background-color':'gray'});
        	$(obj).html(o.times+"秒后重新发送");
        }
        setTimeout(function () {
        	o.timeout(obj);
        }, 1000);
	},
	
	cofirmCode : function(){
		var inputCode=$("#orderVerifyCode").val();
		if(inputCode==""){
			alert("请输入验证码！");
			return false;
		}
		$.ajax({
			  type: "POST", 
			  url: DTPath+"deliver/ajax.php",
			  dataType : "json",
			  data: { 
				  action : 'cofirmCode',
				  mobile : o.mobile,
				  orderid : o.orderid,
				  code : inputCode,
				  isFromCarrier : o.isFromCarrier ? 1 : 0
			  },
			  success: function( data ) {
				  if( data && !data.flag ){
					  alert(data.msg);
				  }else{
					 window.location.href = DTPath+'deliver/detail.php?orderid='+o.orderid;
				  }
			  }
		});
	}
	
};

$(function(){
	$('input[name=smth]').bind("click", function(){
		if( this.value == 1 ){
			$('.smth_area').show();
		}else{
			$('.smth_area').hide();
		}
	});
	$('input[name=shsm]').bind("click", function(){
		if( this.value == 1 ){
			$('.shsm_area').show();
		}else{
			$('.shsm_area').hide();
		}
	});
	
	$('#send_senderMobilePhone').keyup(function(){
		this.value = this.value.replace(/[^\d.]/g,"");
	});
	
	$('#fake_sendAddress').keyup(function(){
		if($('#senderCountySelect').val() == ''){
			alert('请先选择地区');
			this.value = '';
			return false;
		}
		$('#sendAddr').val($('#senderCityName').text()+$('#senderCountySelect').find("option:selected").text()+this.value);
	});
	
	$('#fake_receiptAddress').keyup(function(){
		if($('#receiverCountySelect').val() == ''){
			alert('请先选择地区');
			this.value = '';
			return false;
		}
		$('#rcvAddr').val($('#receiverCityName').text()+$('#receiverCountySelect').find("option:selected").text()+this.value);
	});
	
	$('#weight').blur(function(){
		o.inputW = this.value;
		info['sum'] = o.calcPrice();		
	});
	$('#goods_volumn').blur(function(){
		o.inputV = this.value;
		info['sum'] = o.calcPrice();
	});
	
	$('#sendOrderPhoneCode').click(function(){
		o.sendCode(this);
	});
	
	$('#cofirmCode').click(function(){
		o.cofirmCode();
	});
	
});
