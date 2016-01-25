/**
 * Created by shijiuwei on 2015/2/28.
 */
define(function (require) {
    require("bootstrap");
    require("plugins/formValidation/zh_CN");
    require("plugins/cityplugin/city-select-zh-CN");

    /*2015-3-8*/
    function onlyNum(eleId) {
        $("#" + eleId).on("keyup", function () {
            var tmptxt = $(this).val();
            $(this).val(tmptxt.replace(/\D/g, ''));
        }).on("paste", function () {
            var tmptxt = $(this).val();
            $(this).val(tmptxt.replace(/\D/g, ''));
        }).css("ime-mode", "disabled");
    }

    function onlyfloat(eleId) {
        $("#" + eleId).on("keyup", function () {
            var tmptxt = $(this).val();
            $(this).val(tmptxt.replace(/[^\d.]/g, ''));
        }).on("paste", function () {
            var tmptxt = $(this).val();
            $(this).val(tmptxt.replace(/[^\d.]/g, ''));
        }).css("ime-mode", "disabled");
    }

    function format2(state) {
        if (!state.id) return state.text; // optgroup
        var idx = state.text.indexOf("(");
        if (idx > 0) {
            return state.text.substring(0, idx);
        }
        else
            return state.text;
    }

    function loadingSelectForJson(eleId, placeholder, type, maxcount) {
        var url = "ab/fmcar/city2.html";
        if (type == 1) {
            url = "ab/fmcar/city.html";
        }
        $.ajax({
            url: url,
            dataType: "json",
            success: function (h) {
                $(eleId).select2({
                    multiple: true,
                    maximumSelectionSize: maxcount,
                    formatSelection: format2,
                    minimumInputLength: 2,
                    escapeMarkup: function (m) { return m; },
                    data: h
                });
            }
        });
    }


    loadingSelectForJson("#s2id_FromName","",0,5);
    /*end 2015-3-8*/
    
    var validator = function (o) {
        o = {
            formid: o.formid || "#formImportCarS2"
        };
        $(o.formid).formValidation({
            framework: 'bootstrap',
            icon: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
            	abFmcar_car_no: {
                    validators: {
			            callback: {
                            message: '请输入正确车牌号',
                            callback: function(value, validator, $field) {
                            	var regExp = /^[\u4e00-\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{4}[a-zA-Z_0-9_\u4e00-\u9fa5]$/;
                            	
                            	$fields = validator.getFieldElements('abFmcar_car_no');;
                            	var value = $fields.eq(0).val();
                            	if(!regExp.test(value)){
                            		return false;
                            	}
                            	
                            	var cpTag = ["京", "津", "沪", "渝", "冀", "豫", "云", "辽", "黑", "湘", "皖", "鲁", "新", "苏", "浙", "赣", "鄂", "桂", "甘", "晋", "蒙", "陕", "吉", "闽", "贵", "粤", "川", "青", "藏", "琼", "宁"];
                                var tagOk = false;
                                
                                for (var i = 0; i < cpTag.length; i++) {
                                    if (cpTag[i] == value.substring(0, 1)) {
                                        tagOk = true;
                                        break;
                                    }
                                }
                                
                                if (tagOk) {
                                    // Update the status of callback validator for all fields
                                    validator.updateStatus('abFmcar_car_no', validator.STATUS_VALID, 'callback');
                                    return true;
                                }
                                
                                return false;
                            }
                        }
                    }
                },
                abFmcar_driver: {
                    validators: {
                        notEmpty: {
                            message: '请输入司机姓名'
                        },
		                regexp: {
			                regexp: /[\u4e00-\u9fa5]{2,6}/,
			                message: '司机姓名格式不正确,只能输入中文,且2-6位'
			            }
                    }
                },
                abFmcar_mobile: {
                    validators: {
                        notEmpty: {
                            message: '请输入电话号码'
                        }
                    }
                },
                abFmcar_length: {
                    validators: {
                        notEmpty: {
                            message: '请选择车长'
                        }
                    }
                },
                abFmcar_type: {
                    validators: {
                        notEmpty: {
                            message: '请输入车型'
                        }
                    }
                },
                runcity: {
                    validators: {
                        notEmpty: {
                            message: '请输入常跑城市'
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

                // 这里你想干什么都行 ...
                // 然后用defaultSubmit()方法提交表单
                $.ajax({
                    url: "/ab/fmcar/addFmcarywy",
                    cache:false,
                    type: "POST",
        			data: $("#formImportCarS2").serialize(),
                    dataType:"json"
                }).done(function(json) {
                	if(json){
                		alert("保存成功");
                	}else{
                		alert("保存失败.");
                	}
                }).fail(function(){ alert("出错啦！"); });
            });
    };
    
    validator({formid:"#formImportCarS2"});
    
    $("#addKnowYes").off("click").on("click", function () {
		$("#formImportCarS2").submit();
    });
    
    $("#is_protect").change(function(){
    	if($(this).prop("checked")){
    		$("#abFmcar_is_protect").val("1");
    	}else{
    		$("#abFmcar_is_protect").val("0");
    	}
    });
    
    $("#is_locate").change(function(){
    	if($(this).prop("checked")){
    		$("#abFmcar_is_locate").val("1");
    	}else{
    		$("#abFmcar_is_locate").val("0");
    	}
    });
    
    $("#searchFmcar").off("click").on("click", function () {
    	$("#searchFmcarForm").submit();
    });
    
    $("#carSearchYes").off("click").on("click",function(){
    	$("#searchFmcarFormMore").submit();
    });
    
    $("#orderCar").off("click").on("click",function(){
    	window.location.href = "/ab/fmcarorders";
    });
    
    return {};
    
    /*
    var myModalAddKnowModal = $("#myModalAddKnow");
    var myModalCarDetailModal = $("#myModalCarDetail");
    
    $("#btnshowMarket").on("click", function () {
       var $marketCode = $("#marketCode"),
           $this = $(this);
        if($marketCode.is(':visible')){
            $marketCode.hide();
        }else{
            $marketCode.show();
        }
    });

    var openLocation = function (id) {
            var dtd = $.Deferred(),
                myId = id || null;
            $.ajax({
                url: "res/js/fmcar/response.json",
                cache:false,
                data:{
                    id:myId
                },
                dataType:"json"
            }).done(function(json) {
                dtd.resolve(json);
            });
            return dtd.promise();
        },
        delThisCar = function (id) {
            var dtd = $.Deferred(),
                myId = id || null;
            $.ajax({
                url: "ab/fmcar/delFmcarUser",
                cache:false,
                data:{
                	carId:myId
                },
                dataType:"json"
            }).done(function(json) {
                dtd.resolve(json);
            });
            return dtd.promise();
        },
        addKnowCarStep = function (mobile) {
            var dtd = $.Deferred(),
                myId = mobile || null;
            var reg = /^1\d{10}$/;
        	if (reg.test(mobile)) {
        		$.ajax({
                    url: "ab/fmcar/checkMobile",
                    cache:false,
                    data:{
                    	mobile:myId
                    },
                    dataType:"json"
                }).done(function(json) {
                    dtd.resolve(json);
                });
        	 }else{
        		 dtd.resolve({"result":"error mobile"});
        	 };
            return dtd.promise();
        };
    $(".openLocation").on("click", function () {
        $.when(openLocation($(this).attr("id")))
            .then(function (json) {
                var mmm = $("#myModalMsg"),
                    mmmmb = mmm.find('.modal-body');
                if(json.test==="yes"){
                    mmmmb.html('<p>申请成功！小秘会给司机师傅发送一条短信，需要回复“Y”才能开通成功，建议您要求司机予以回复。</p>' +
                    '<p>电信手机会在明天收到邀请开通的短信</p>');
                    mmm.modal();
                }else{
                    mmmmb.html('<p>开通短信定位失败，请重试！</p>');
                    mmm.modal();
                }
            });
    });
    $(".delThisCar").on("click", function () {
        var $thisdel = $(this),
            mmd = $("#myModalDel"),
            mmdmb = mmd.find('.modal-body'),
            mmldy = $("#myModalLabelDelYes");
        var carId = $(this).parent().parent().parent().parent().parent().find("td:eq(0)").text();
        mmd.modal();
        mmldy.off("click").on("click", function () {
            $.when(delThisCar(carId))
                .then(function (json) {
                	if(json){
                		window.location.reload();
                	}else{
                		alert("删除失败.请重试！");
                	}
                });
        });
    });

    $(".editThisCar").on("click", function () {
    	var id = $(this).parent().parent().parent().parent().parent().find("td:eq(0)").text();
    	myModalAddKnowModal.modal({
    		remote:"ab/fmcar/editYwyCar?id="+id
    	});
    	
    	//loadingSelectForJson("#s2id_FromName","",1,5);
    });
    
    $("#myModalAddKnowBtn").off("click").on("click",function(){
    	myModalAddKnowModal.modal({
    		remote:"ab/fmcar/addYwyCar?mobile="+$("#carMobilePhone").val()
    	});
    });
    
    $("#addKnowStep1").off("click").on("click",function(){
    	$.when(addKnowCarStep($("#carMobilePhone").val())).then(function (json) {
    		if(json.result == "error mobile"){
            	$("#errormsg").html("请输入正确的手机号码...");
            	$("#errormsg").show();
            }else if(json.result == "notExist"){
            	myModalAddKnowModal.modal({
            		remote:"ab/fmcar/edit?mobile="+$("#carMobilePhone").val()
            	});
            	//loadingSelectForJson("#s2id_FromName","",1,5);
            }else if(json.result == "notMine"){
            	myModalCarDetailModal.modal({
            		remote:"ab/fmcar/showDetail?id="+json.car.id+"&type=notMine"
            	});
            }else if(json.result == "mine"){
            	myModalAddKnowNextStepModal.modal('hide');
            	myModalCarDetailModal.modal({
            		remote:"ab/fmcar/showDetail?id="+json.car.id+"&type=mine"
            	});
            }else{
            	alert("出错啦:" + json.result);
            }
    	});
    });
    
    myModalCarDetailModal.on("hidden.bs.modal", function() { $(this).removeData("bs.modal"); });
    myModalAddKnowModal.on("hidden.bs.modal", function() { $(this).removeData("bs.modal"); });
    
    myModalCarDetailModal.on('loaded.bs.modal', function (e) {
    	var detailType = $("#detailType").val();
    	
    	if(detailType == 'notMine'){
    		$("#addKnowYesExistBtn").show();
        	$("#existCarBtn").hide();
    	}else if(detailType == 'mine'){
    		$("#addKnowYesExistBtn").hide();
        	$("#existCarBtn").show();
    	}else{
    		$("#addKnowYesExistBtn").hide();
        	$("#existCarBtn").hide();
    	}
    	
    	$("#existCarBtn").off("click").on("click",function(){
    		myModalCarDetailModal.modal('hide');
    	});
    	
    	$("#addKnowYesExistBtn").off("click").on("click",function(){
        	var carId = $("#detailCarId").val();
        	$.ajax({
                url: "ab/fmcar/addFmcarUser",
                cache:false,
                type: "POST",
    			data: {carId:carId},
                dataType:"json"
            }).done(function(json) {
            	if(json){
            		alert("添加成功");
            		window.location.reload();
            	}else{
            		alert("添加失败.");
            	}
            }).fail(function(){ alert("出错啦！"); });
        });
    });
    
    myModalAddKnowModal.on('loaded.bs.modal', function (e) {
    	validator({formid:"#formImportCarS2"});
    	$("#addKnowYes").off("click").on("click", function () {
    		$("#formImportCarS2").submit();
        });
    });
    
    $(".showCarDetail").off("click").on("click",function(){
    	var id = $(this).parent().parent().find("td:eq(0)").text();
    	myModalCarDetailModal.modal({
    		remote:"ab/fmcar/showDetail?id="+id
    	});
    });
    
    function delNull(data){
    	if(data == null || data == "null"){
    		return "";
    	}
    	return data;
    }
    
    $("#addKnowYes").off("click").on("click", function () {
    	alert(1);
    	$.ajax({
            url: "ab/fmcar/addFmcar",
            cache:false,
            type: "POST",
			data: $("#formImportCarS2").serialize(),
            dataType:"json"
        }).done(function(json) {
        	if(json){
        		alert("添加成功");
        		window.location.reload();
        	}else{
        		alert("添加失败.");
        	}
        }).fail(function(){ alert("出错啦！"); });
    	
    });*/
});