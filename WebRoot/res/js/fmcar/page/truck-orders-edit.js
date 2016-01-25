/**
 * Created by shijiuwei on 2015/3/1.
 */
define(function (require) {
    require("bootstrap");
    require("plugins/formValidation/zh_CN");
    
    require('plugins/cityplugin/cityplug');
    var ias = require("inputAutoSearch");
    
    var $chakancheliang = $("#chakancheliang");
    $chakancheliang.find('.daichengjiao').on('click', function () {
        window.location.href = "/ab/fmcarorders/list/waiting";
    });
    $chakancheliang.find('.jinxingzhong').on('click', function () {
        window.location.href = "/ab/fmcarorders/list/working";
    });
    
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
            	order_start_city_name: {
                    validators: {
                        notEmpty: {
                            message: '请输入出发城市'
                        }
                    }
                },
                order_start_addr: {
                    validators: {
                        notEmpty: {
                            message: '请输入出发详细地址'
                        }
                    }
                },
                order_arr_city_name: {
                    validators: {
                        notEmpty: {
                            message: '请输入到达城市'
                        }
                    }
                },
                order_arr_addr: {
                    validators: {
                        notEmpty: {
                            message: '请输入达到详细地址'
                        }
                    }
                },
                order_car_length: {
                    validators: {
                        notEmpty: {
                            message: '请输入车长'
                        }
                    }
                },
                order_car_type: {
                    validators: {
                        notEmpty: {
                            message: '请输入车型'
                        }
                    }
                },
                /*order_load_time: {
                    validators: {
                        notEmpty: {
                            message: '请输入装货时间'
                        }
                    }
                },*/
                order_mobile: {
                    validators: {
                        notEmpty: {
                            message: '请输入联系电话'
                        },
                        regexp: {
                            regexp: /^([0-9]{3,4}\-?)?[0-9]{7,8}$|^(\+86)?1[0-9]{10}$/,
                            message: '请输入正确格式的联系电话'
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
        var depProvinceCode = $("#depProvinceCode").val();
        var ToProvinceCode = $("#ToProvinceCode").val();
        
        if (!depProvinceCode) {
			alert("请先选择出发城市");
			submit = false;
		}else if (!ToProvinceCode) {
			alert("请先选择到达城市");
			submit = false;
		}
        
        if (submit) {
        	return true;
        }
        
        return false;
    });
    
    //城市选择
    $('.addrselector').addrDropmenu({ level: 3 });
    var map = new AMap.Map("mapContainer",{
        resizeEnable: true,
        center:[116.397428,39.90923],//地图中心点
        zoom:13,//地图显示的缩放级别
        keyboardEnable:false
    });
    //详细地址自动完成
    ias.inputAutoSearch(map,'startPlaceDetail','result1','resultItem1');
    ias.inputAutoSearch(map,'endPlaceDetail','result2','resultItem2');
    
    return {};
});