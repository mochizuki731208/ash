function uf_addleave(isrefresh, path) {
	var $msg = $("#leaveMsg");
	$.ajax({
		url : path+'/ab/leave/addleave',
		type : "post",
		async : true,
		data : {
			'mid' : $('#mid').val(),
			'qdrid' : $('#qdrid').val(),
			'orderid' : $('#orderid').val(),
			'content' : $('#content').val(),
			'captchaCode' : $('#captchaCode').val(),
			'isrefresh' : isrefresh //1为刷新，0为不刷新
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			} else {
				uf_leave_close();
				if("1" == isrefresh){
					$("#leavemsglist").html(data);
				}else{
					$.dialog.alert("催单成功！", function() {
						refresh();
					}).title('3秒后关闭').time(3);
				}
			}
		}
	});
}

function uf_addreply(isrefresh, path) {
	var $msg = $("#leaveMsg");
	$.ajax({
		url : path+'/ab/leave/addreply',
		type : "post",
		async : true,
		data : {
			'lid' : $('#leave_id').val(),
			'mid' : $('#mid').val(),
			'content' : $('#content').val(),
			'captchaCode' : $('#captchaCode').val(),
			'isrefresh' : isrefresh //1为刷新，0为不刷新
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			} else {
				uf_leave_close();
				if("1" == isrefresh){
					$("#leavemsglist").html(data);
				}else{
					$.dialog.alert("回复成功！", function() {
						refresh();
					}).title('3秒后关闭').time(3);
				}
			}
		}
	});
}

function uf_addback(path) {
	var $msg = $("#backMsg");
	$.ajax({
		url : path+'/ab/chargeback/addchargeback',
		type : "post",
		async : true,
		data : {
			'orderid' : $('#backorderid').val(),
			'applyBackContent' : $('#applyBackContent').val(),
			'backCaptchaCode' : $('#backCaptchaCode').val(),
			'apyimg' : $('#apyimg').val()
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			}else if ("3" == data) {
				$.dialog.alert("不能重复维权！", function() {
					refresh();
				}).title('3秒后关闭').time(3);
			} 
			else {
				uf_back_close();
				$.dialog.alert("维权成功！", function() {
					refresh();
				}).title('3秒后关闭').time(3);
			}
		}
	});
}

function uf_addcomplement(path) {
	var $msg = $("#backMsg");
	$.ajax({
		url : path+'/ab/chargeback/addcomplement',
		type : "post",
		async : true,
		data : {
			'cbid' : $('#cbid').val(),
			'comContent' : $('#comContent').val(),
			'comCaptchaCode' : $('#comCaptchaCode').val(),
			'apyimg' : $('#apyimg').val(),
			'cbitmtyp' : $('#cbitmtyp').val()
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			} else {
				uf_cbplus_close();
				$.dialog.alert("维权补充说明成功！", function() {
					refresh();
				}).title('3秒后关闭').time(3);
			}
		}
	});
}

function uf_addReponse(path) {
	var $msg = $("#repMsg");
	$.ajax({
		url : path+'/ab/chargeback/addcbReponse',
		type : "post",
		async : true,
		data : {
			'cbid' : $('#cbid').val(),
			'content' : $('#content').val(),
			'agree' : $("input[name='agree'][checked]").val(),
			'captchaCode' : $('#captchaCode').val(),
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			} else {
				uf_cbreponse_close();
				$.dialog.alert("回复成功！", function() {
					refresh();
				}).title('3秒后关闭').time(3);
			}
		}
	});
}

function uf_addjudge(path) {
	var $msg = $("#judgeMsg");
	$.ajax({
		url : path+'/ab/chargeback/addcbJudge',
		type : "post",
		async : true,
		data : {
			'cbid' : $('#cbid').val(),
			'content' : $('#content').val(),
			'agree' : $("input[name='agree'][checked]").val(),
			'captchaCode' : $('#captchaCode').val(),
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			} else {
				uf_cbjudge_close();
				$.dialog.alert("回复成功！", function() {
					refresh();
				}).title('3秒后关闭').time(3);
			}
		}
	});
}

function uf_applycbjudge(path) {
	var $msg = $("#applycbjudgeMsg");
	$.ajax({
		url : path+'/ab/chargeback/applycbJudge',
		type : "post",
		async : true,
		data : {
			'cbid' : $('#cbid').val(),
			'content' : $('#applycbcontent').val(),
			'captchaCode' : $('#applycbCaptchaCode').val(),
		},
		dataType : "html",
		timeout : "6000",
		error : function(data) {
		},
		success : function(data) {
			if ("1" == data) {
				$msg.html("验证码输入不正确！");
			} else if ("2" == data) {
				$msg.html("内容不能为空且不超过100个字");
			} else {
				uf_applycbjudge_close();
				$.dialog.alert("申请客服成功！", function() {
					refresh();
				}).title('3秒后关闭').time(3);
			}
		}
	});
}

function uf_leave_show(orderid, mid, qdrid, leaveId){
	refreshSafecode();
	$('#lean_overlay').show();
	$('#msgDiv').show();
	if(mid != null){
		$('#mid').val(mid);
	}
	if(orderid != null){
		$('#orderid').val(orderid);
	}
	if(qdrid != null){
		$('#qdrid').val(qdrid);
	}
	if(leaveId != null){
		$('#leave_id').val(leaveId);
	}
}

function uf_back_show(orderid){
	refreshBackSafecode();
	$('#lean_overlay').show();
	$('#backMsgDiv').show();
	if(orderid != null){
		$('#backorderid').val(orderid);
	}
}

function uf_cbreponse_show(cbid){
	refreshSafecode();
	$('#lean_overlay').show();
	$('#msgDiv').show();
	if(cbid != null){
		$('#cbid').val(cbid);
	}
}


function uf_applycbreponse_show(cbid){
	refreshApplyCbSafecode();
	$('#lean_overlay').show();
	$('#applycbmsgDiv').show();
	if(cbid != null){
		$('#cbid').val(cbid);
	}
}

function uf_cbplus_show(cbid){
	refreshComSafecode();
	$('#lean_overlay').show();
	$('#backMsgDiv').show();
	if(cbid != null){
		$('#cbid').val(cbid);
	}
}

function uf_leave_close(){
	$('#lean_overlay').hide();
	$('#msgDiv').hide();
	$('#content').val('');
	$('#captchaCode').val('');
}

function uf_back_close(){
	$('#lean_overlay').hide();
	$('#backMsgDiv').hide();
	$('#applyBackContent').val('');
	$('#backCaptchaCode').val('');
	$('#imga').html('');
	$('#apyimg').val('');
}

function uf_cbreponse_close(){
	$('#lean_overlay').hide();
	$('#msgDiv').hide();
	$('#content').val('');
	$('#agree1').attr('checked', 'checked');
	$('#captchaCode').val('');
}

function uf_cbjudge_close(){
	$('#lean_overlay').hide();
	$('#msgDiv').hide();
	$('#content').val('');
	$('#agree1').attr('checked', 'checked');
	$('#captchaCode').val('');
}

function uf_cbplus_close(){
	$('#lean_overlay').hide();
	$('#backMsgDiv').hide();
	$('#comContent').val('');
	$('#comCaptchaCode').val('');
}

function uf_applycbjudge_close(){
	$('#lean_overlay').hide();
	$('#applycbmsgDiv').hide();
	$('#applycbcontent').val('');
	$('#applycbCaptchaCode').val('');
}
