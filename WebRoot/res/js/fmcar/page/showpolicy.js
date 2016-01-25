/**
 * Created by shijiuwei on 2015/3/1.
 */
define(function (require) {
    require("bootstrap");
    require("plugins/formValidation/zh_CN");
    
    require('plugins/cityplugin/cityplug');
    
    var validator = function (o) {
        o = {
            formid: o.formid || "#editForm"
        };
        $(o.formid).formValidation({
            framework: 'bootstrap',
            icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
            	// 被保险人
            	"abPolicy.insurant": {
                    validators: {
                        notEmpty: {
                            message: '请输入被保险人'
                        }
                    }
                },
            	// 被保险人手机
            	"abPolicy.insurant_tel": {
                    validators: {
                        notEmpty: {
                            message: '请输入被保险人手机'
                        },
                        regexp: {
                            regexp: /^([0-9]{3,4}\-?)?[0-9]{7,8}$|^(\+86)?1[0-9]{10}$/,
                            message: '请输入正确格式的联系电话'
                        }
                    }
                },
            	// 货物名称
            	"abPolicy.goods_name": {
                    validators: {
                        notEmpty: {
                            message: '请输入货物名称'
                        }
                    }
                },
            	// 包装及数量
            	"abPolicy.goods_num": {
                    validators: {
                        notEmpty: {
                            message: '请输入包装数量'
                        }
                    }
                },
            	// 运单号/发票号/合同号
            	"abPolicy.order_sn": {
                    validators: {
                        notEmpty: {
                            message: '请输入运单号/发票号/合同号'
                        }
                    }
                },
            	// 运输工具
            	"abPolicy.transport_name": {
                    validators: {
                        notEmpty: {
                            message: '请输入运输工具'
                        }
                    }
                },
            	// 目的地
            	/*
            	end_site_province: {
                    validators: {
                        notEmpty: {
                            message: '请输入目的城市'
                        }
                    }
                },*/
            	// 保险金额
            	"abPolicy.policy_quota": {
                    validators: {
                        notEmpty: {
                            message: '请输入保单金额'
                        },
                        number: {
                            message: '请输入数字'
                        }
                    }
                }
            }
        })
            .on('success.form.fv', function(e) {
                //阻止表达提交
                e.preventDefault();

                //get表单实例
                var $form = $(e.target);

                //get BootstrapValidator实例
                var bv = $form.data('formValidation');
                
                $("#submit_btn").attr("disabled" , "disabled");
                $("#span_save_tip").css("display" ,"");
                // 这里你想干什么都行 ...
                // 然后用defaultSubmit()方法提交表单
                bv.defaultSubmit();
            });
    };
    
    validator({formid:"#editForm"});
    
    $("#submit_btn").click(function(){
    	var submit = true;
        var end_site_city = $("#end_site_city").val();
        var start_site_city = $("#start_site_city").val();
        var money = $("#policy_quota").val();
        var time = $("#start_time").val();
        
        var end_site_province = $("#end_site_province").val();
        var start_site_province = $("#start_site_province").val();
        
        if (start_site_province == "北京" || start_site_province == "天津" || start_site_province == "上海" || start_site_province == "重庆") {
        } else if (start_site_city == null || start_site_city == "") {
			alert("起运地至少选择到市!");
			submit = false;
		}
		if (end_site_province == "北京" || end_site_province == "天津" || end_site_province == "上海" || end_site_province == "重庆") {
		} else if (end_site_city == null || end_site_city == "") {
			alert("目的地至少选择到市!");
			submit = false;
		}
		if(parseInt(money) <= 0) {
			alert("保险额度为正数!");
			submit = false;
		}
        if(time == null || time == "") {
        	alert("起运时间不能为空!");
			submit = false;
        }
        
        if (submit) {
        	if(checkValues()) {
        		if(confirm("您确定提交保单?")) {
		        	// 提交表单
		        	$.ajax({
					   type: "POST",
					   url: $("#editForm").attr("action"),
					   data: $("#editForm").serialize(),
					   dataType: "text",
					   success: function(_data){
			        		if(_data == "2") {
			        			alert("投保失败!");
			        		}else {
			        			// 缴费
			        			TableSumit(_data);
			        			$("#editForm")[0].reset();
			        		}
					   },
					   error:function(XMLHttpRequest, textStatus, errorThrown) {
					   		alert("执行错误!" + textStatus + "  " + errorThrown);
					   }
					});
				}
        	} else {
        		return false;
        	}
        }
        
        return false;
    });
    
    function checkValues(){
		var WIDtotal_fee = $("#policy_cost").val();
		if(!WIDtotal_fee){
			alert("请输入金额");
			$("#policy_cost").focus();
			return false;
		}
		var exp = /^([1-9][\d]{0,7}|0)(\.[\d]{1,2})?$/;    
		if(!exp.test(WIDtotal_fee)){    
			alert("您输入的金额不正确！"); 
			$("#policy_cost").val('');
			$("#policy_cost").focus();
			return false;
		}
		return true;
	}
	function TableSumit(trade_no){
		$.ajax({
			url: $("#pay_url").val(),
			type: "POST",
			data:{
				WIDout_trade_no:trade_no,// 保费单号
				WIDsubject:"充值",
				WIDbody:"充值",
				zzfs:"0",// 在线充值
				WIDtotal_fee:$("#policy_cost").val(),
				yhkid:$("input:radio[name='yhkid']:checked").val(),
				type:"0" // 充值
			},
			dataType: "json",
			cache: false,
			success: function(data) {
				if(!data.result){
					alert("信息有误，请重新提交！");
				}else{
					$.ajax({
					   type: "POST",
					   url: $("#after_pay_url").val(),
					   data: {"policy_id" : trade_no},
					   dataType: "text",
					   success: function(_data){
					   		if(_data == "2") {
			        			alert("保费已支付但是保单状态修改失败，请联系客服人员!");
			        		}else {
			        			// alert("保单已保存成功并且保费已支付成功!");
			        			window.location.href = data.url;
			        		}
					   },
					   error:function(XMLHttpRequest, textStatus, errorThrown) {
					   		alert("执行错误!" + textStatus + "  " + errorThrown);
					   }
					});
				}
			},
			error:function(){
				alert("系统错误，请稍后重试！");
			}
		});
	}
    
    //城市选择
    $('.addrselector').addrDropmenu({ level: 3 });
    return {};
});