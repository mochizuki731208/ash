var _car_type;
window.history.forward();
/*===================================程序运行入口==============================*/
$(function(){
    _car_type = $("#carImgBj, #carImgSh").attr("car_type");
    if (!_car_type) {
        $(".no-car-type").show();
        $(".order_car_type").parent().parent().hide();
        /*
        $("#saveInfo").addClass("next-disabled-btn");
        */
    }
    $('.address-info-name').live( "blur", function(event){
        var $this = $(this);
        if (!$.trim($this.val())) {
            on_input_error($this , {name: "姓名"}, true);
        } else {
            on_input_error($this , {name: "姓名"}, false);
        }
    });
    $('.address-info-phone').live( "blur", function(event){
        var $this = $(this);
        var phone = $this.val();
        if (!lxn_is_phone(phone)) {
            on_input_error($this , {name: "电话"} , true);
        } else {
            on_input_error($this , {name: "姓名"}, false);
        }
    });

    //判断收货地址和发货地址 xy
    $('.takeGoods-js').find(".address-info-address").each(function(){
        var $this = $(this);
        var address = $(this).val();
        if (address) {
            var $take_goods = $this.parents(".takeGoods-js");
            var position_x = $take_goods.attr("address-info-x");
            var position_y = $take_goods.attr("address-info-y");
            if (!check_address(position_x, position_y)) {
                on_input_error($this, { name: '地址'}, true);
                //return false;
            }
        }
    });
    /*================================== 初始化  ==================================*/
    infoInit();
    /*================================== add ==================================*/
    $('.plus').live('click',function(){
        plusClick(this);
    });
    /*================================== min  ==================================*/
    $('.reduction').live('click',function(){
        reductionClick(this);
    });
    /*================================== up  ==================================*/
    $('.up').live('click',function(){
        upClick(this);
    });
    /*================================== down  ==================================*/
    $('.down').live('click',function(){
        downClick(this);
    });

    /*================================== 车辆图标  ==================================*/
//     $("#carImgBj").hover(function(event){
//         $("#carImgBj>.carImg").children().children(".t").stop(true, false).animate({width:164});
//     }, function(event){
// //      event.stopPropagation();

//         $("#carImgBj>.carImg").stop(true, false).animate({'width':'328px'});
//         $("#carImgBj>.carImg").children().children(".t").stop(true, false).animate({width:100});

//         $("#carImgBj>.carImg").children().children(".over").stop(true, false).removeClass("over");
//     });
//     $("#carImgBj>.carImg").hover(function(event){
// //      event.stopPropagation();

//         var $this = $(this);

//         $this.children().children(".img").addClass("over");

//         $this.stop(true, false).animate({'width':'656px'});
//         $this.siblings('.carImg').stop(true, false).animate({'width':'164px'});
//     }, function(event){

// //      $this.find(".over").removeClass("over");
//     });
    $("#carImgBj>.carImg, #carImgSh>.carImg").click(function(){
        if ($(this).hasClass('car-no-open')) {
            return false;
        }
        //选择车型后，去掉标红
        $('#carImgBj').removeClass('order-info-no-choose');
        /*
        $("#saveInfo").removeClass("next-disabled-btn");
        */
        $(".no-car-type").hide();
        $(".order_car_type").parent().parent().show();
        // 修改汽车类型
        _car_type = $(this).attr("car_type");

        on_ui_update();
    });

    /*================================== 车辆图标 end  ===============================*/

    // 设置联系人姓名及电话号码
    $(".self").live('click', function(){
        if (!user || !user.phone) {
            return false;
        }
        $(this).parent().find('input').val(user.name);
        $(this).parents('tr').next().find('input').val(user.phone);

        $('.address-info-name').blur();
        $('.address-info-phone').blur();
    });

    /*===================================获取常用地址信息=====================================*/
    var curcity = get_location_city_name();
    var province_name = get_location_province_name();
    // baidu下拉提示框
    $(".address-info-address").each(function(index, elem) {
        $(this).localquery({cityCode: curcity, provFilter:province_name}, on_address_change, on_baidu_selected);
    });

    // 常用地址
    $(".com-address-a-js").each(function(index, elem) {
        var addr_species = $(this).attr('addr_species');
        $(this).commonaddress( ".takeGoods-js", addr_species, on_address_selected);
    });

    // 初始化距离，车型、费用信息
    on_ui_update();

    /*================================== 提交信息  ==================================*/
    order = order || {order_id: 0};
    var _is_saving = false;
    var on_save_success = function(res) {
        if (res.code !== 0) {
            new LXN_pop(res.code, res.msg);
            return;
        }
        // 信息保存成功之后跳转到下一页
        lxn_api.redirect("/OrderNew/orderDetail?orderId=" + order.order_id);
    };
    var save_info = function(order_id, car_type) {
        if (!order_id) {
            lxn_api.redirect_to_404();
        }

        var order_city = get_location_city_name();
        var pickup_info = get_pickup_info();
        if (!pickup_info) {
            return;
        }

        var deliver_list = get_deliver_list();
        if (!deliver_list) {
            return;
        }

        var distance = $('.order_distance').text();
        lxn_api.save_order_base(order_id, order_city, car_type,
            pickup_info, deliver_list, distance, on_save_success, function() {
                _is_saving = true;
            }, function() {
                _is_saving = false;
            });
    };
    var create_order = function(on_create_order) {
        lxn_api.create_order(function(res){
            if (res.code !== 0) {
                new LXN_pop(res.code, res.msg);
                return;
            }
            order.order_id = res.data.orderId;
            on_create_order && on_create_order();
        }, function() {
            _is_saving = true;
        }, function() {
            _is_saving = false;
        });
    };
    $("#saveInfo").click(function() {
        if($(this).hasClass("next-disabled-btn")){
            return false;
        }
        if (_is_saving) {
            return false;
        }

        if (!lxn_api.check_car_exist(_car_type)) {
            //如果车型未选择，车型标红，并且滚动到顶部
            $('#carImgBj').addClass('order-info-no-choose');
            $('html,body').animate({scrollTop:'0px'},500);
            /*
            new LXN_pop(null, '请选择车型！');
            */
            return false;
        }

        if (!order.order_id) {
            create_order(function() {
                save_info(order.order_id, _car_type);
            });
        } else {
            save_info(order.order_id, _car_type);
        }

    });
});

/*================================== input 错误提示信息  ==================================*/
function on_input_error($elem, data, is_error) {
    if (!is_error) {
        $elem.removeClass('inpError');
        $elem.parent().next().remove();
        return false;
    }

    if ($elem.parent().next('.error').size()>0) {
        return false;
    } else {
        $elem.addClass('inpError');
        $elem.parent().after('<p class="error">' + data.name +'格式错误！</p>');
    }
}

/*==================================  初始化     ==============================================================*/
function infoInit(){
    var num = $('.receipt-address-num');
    var up = $('.up');
    var plus = $('.plus');
    var reduction = $('.reduction');
    var down = $('.down');
    var tab2 = $('.tab-js');
    //num
    for (var i = 0; i < num.length; i++) {
        num.eq(i).attr('class', 'receipt-address-num num' + (i + 1));
    }
    //add plus
    if (num.length == 1) {
        plus.attr('class', 'plus');
        reduction.attr('class', 'reduction reduction-no');
        num.hide();
    } else if (num.length == 5) {
        plus.attr('class', 'plus plus-no');
        reduction.attr('class', 'reduction');
        num.show();
    } else {
        plus.attr('class', 'plus');
        reduction.attr('class', 'reduction');
        num.show();
    }
    //up down
    up.attr('class', 'up');
    down.attr('class', 'down');
    up.eq(0).attr('class', 'up up-no');
    down.eq(num.length - 1).attr('class', 'down down-no');
};
/*================================== plus 加 ==============================================================*/
function plusClick(ele) {
    /*=============================== 变量   =============================*/
    var tab_self_string = '';
    var tab_self_string_adress = '';
    if (user.name && user.phone) {
        tab_self_string = '<a href="javascript:void(0);" class="self">本人</a>';
        tab_self_string_adress = '<a href="javascript:void(0);" class="com-adress-a com-address-a-js" addr_species="2">常用</a>';
    }
    var tab = $('<div class="tab2 tab-js takeGoods-js">' +
                '<div class="receipt-address-num num1"></div>' +
                '<table width="100%" cellpadding="0" cellspacing="0">' +
                    '<tbody>' +
                        '<tr>' +
                            '<th>收货地址：</th>' +
                            '<td>' +
                                '<div class="address-wrap">' +
                                    '<div class="address">' +
                                        '<input type="text" placeholder="请输入收货地址" class="l address-info-address" />' +
                                        tab_self_string_adress +
                                    '</div>' +
                                '</div>' +
                            '</td>' +
                            '<td rowspan="3" width="47" class="sed-operation">' +
                                '<a href="javascript:void(0);" class="up"></a>' +
                                '<a href="javascript:void(0);" class="plus" ></a>' +
                                '<a href="javascript:void(0);" class="reduction"></a>' +
                                '<a href="javascript:void(0);" class="down"></a>' +
                            '</td>' +
                        '</tr>' +
                        '<tr style="display:none;">' +
                            '<th></th>' +
                            '<td><input type="text" placeholder="如：八王坟客运站 201" class="l address-info-remark"/></td>' +
                        '</tr>' +
                        '<tr>' +
                            '<th>联系人姓名：</th>' +
                            '<td>' +
                                '<p>' +
                                    '<input type="text" placeholder="请输入姓名" class="address-info-name"/>' +
                                    tab_self_string +
                                '</p>' +
                            '</td>' +
                        '</tr>' +
                        '<tr>' +
                            '<th>联系电话：</th>' +
                            '<td>' +
                                '<p><input type="text" placeholder="请输入联系电话" class="address-info-phone"/></p>' +
                            '</td>' +
                        '</tr>' +
                    '</tbody>' +
                '</table>' +
            '</div>');
    var num = $('.receipt-address-num');
    if(num.length < 5) {
        $(ele).parents('.tab-js').after(tab);
        // 初始化 baidu输入下拉提示框
        var curcity = get_location_city_name();
        var province_name = get_location_province_name();
        tab.find(".address-info-address").localquery({cityCode: curcity, provFilter:province_name}, on_address_change, on_baidu_selected);
        // 初始化 常用地址
        tab.find(".com-address-a-js").commonaddress( ".takeGoods-js", COMMON.ADDRESS_SPECIES.DELIVER, on_address_selected);
        on_ui_update();
    } else {
        new LXN_pop(null, '最多5个收货地址！');
    }
    infoInit();
    return false;
}
/*================================== reduction 减  ==============================================================*/
function reductionClick(ele) {
    var num = $('.receipt-address-num'), up = $('.up'), plus = $('.plus'), reduction = $('.reduction'), down = $('.down'), tab2 = $('.tab-js');
    if (num.length <= 1) {
        new LXN_pop(null, '最少1个收货地址！');
    } else {
        $(ele).parents('.tab-js').remove();
        on_ui_update();
    }
    infoInit();
    return false;
}
/*================================== up 上移  ==============================================================*/
function upClick(ele){
    var num = $('.receipt-address-num'),up = $('.up'),plus = $('.plus'),reduction = $('.reduction'),down = $('.down'),tab2 = $('.tab-js');
    var index = up.index(ele);

    if (index == 0) {
        new LXN_pop(null, '亲，已经是第一个，不能再上移了！');
    } else {
        tab2.eq(index-1).before($(ele).parents('.tab-js').clone(true));
        $(ele).parents('.tab-js').remove();
        on_ui_update();
    }
    infoInit();
    return false;
}

/*================================== down 下移  ==============================================================*/
function downClick(ele) {
    var num = $('.receipt-address-num');
    var up = $('.up');
    var plus = $('.plus');
    var reduction = $('.reduction');
    var down = $('.down');
    var tab2 = $('.tab-js');
    var index = down.index(ele);

    if (index == (down.length-1)) {
        new LXN_pop(null, '亲，已经是最后一个，不能下移了！');
    } else {
        tab2.eq(index+1).after($(ele).parents('.tab-js').clone(true));
        $(ele).parents('.tab-js').remove();
        on_ui_update();
    }
    infoInit();
    return false;
}

/*================================== cookie ==============================================================*/
function del_location_city() {
    lxn_del_cookie('locationCity');
}

function get_location_city_name() {
    var city_code = lxn_get_cookie('locationCity');

    var city_name = lxn_api.get_city_name(city_code);
    if (!city_name) {
        city_name = '北京';
    }

    return city_name;
}

function get_location_province_name() {
    var city_code = lxn_get_cookie('locationCity');

    var province_name = lxn_api.get_province_name(city_code);
    if (!province_name) {
        province_name = "北京";
    }

    return province_name;
}

/*================================== 判断地址是否为空   ==============================================================*/
function on_ui_update() {
    var car_type = _car_type;

    // 初始化费用
    $("#carImgBj>.carImg, #carImgSh>.carImg").each(function(){
        var $this = $(this);
        var _type = $this.attr("car_type");
        var _pay = _get_pay(0, _type);
        $this.find("em").text(_pay);
    });
    if (car_type) {
        var city_code = lxn_get_cookie('locationCity');
        $('.order_car_type').text(lxn_api.get_car_name(car_type, city_code));
    }
    $('.order_fare').text('0');

    if($("#carImgBj>.carImg[car_type='"+car_type+"'], #carImgSh>.carImg[car_type='"+car_type+"']").hasClass('car-no-open')){

    } else {
        // 清理车型选中状态
        $("#carImgBj>.carImg, #carImgSh>.carImg").removeClass("car_selected");
        // 给选中车型加上样式
        $("#carImgBj>.carImg[car_type='"+car_type+"'], #carImgSh>.carImg[car_type='"+car_type+"']").addClass("car_selected");
    }


    // 获取收发货坐标点
    var addr = new Array();
    var eles = $(".takeGoods-js");
    for (var i = 0; i < eles.length; i++) {
        var x = $(eles[i]).attr("address-info-x");
        var y = $(eles[i]).attr("address-info-y");
        if (!check_address(x, y)) {
            return;
        }
        // 数据库中存储的指标值与baidu地图中的指标相反
        var point = new BMap.Point(y, x);
        addr.push(point);
    }

    if (addr.length > 1) {
        var curcity = get_location_city_name();
        $.getDistance(curcity, addr, function(dis) {
            $("#carImgBj>.carImg, #carImgSh>.carImg").each(function(){
                var $this = $(this);
                var _type = $this.attr("car_type");
                var _pay = _get_pay(dis, _type);
                $this.find("em").text(_pay);
            });
            ui_update_order(dis, car_type);
        });
    }
}

function ui_verify_address($elem_wrap, x, y) {
    if (check_address(x, y)) {
        $elem_wrap.find('input').css({
            borderColor : "#ccc"
        });
        $elem_wrap.next(".error").remove();
    } else {
        $elem_wrap.find('input').css({
            borderColor : "#f00"
        });
        $elem_wrap.next(".error").remove();
        $elem_wrap.after("<p class='error'>请选择地址</p>");
    }
}
function ui_verify_contact($eleF)
{
    if($eleF.find('input').val())
    {
        $eleF.find('input').removeClass('inpError');
        $eleF.next('.error').hide();
    }else{
        $eleF.find('input').addClass('inpError');
        $eleF.next('.error').show();
    }
}
function ui_verify_phone($eleF)
{
    if($eleF.find('input').val())
    {
        $eleF.find('input').removeClass('inpError');
        $eleF.next('.error').hide();
    }else{
        $eleF.find('input').addClass('inpError');
        $eleF.next('.error').show();
    }
}


function check_address(x, y) {
    if (!x || !y) {
        return false;
    }
    return true;
}

function on_address_selected(eleF, id, province, city, address, address_remark, user_name, phone, x, y) {
    eleF.attr({
        'address-info-id': id,
        'address-info-x': x,
        'address-info-y': y,
        'address-info-province': province,
        'address-info-city': city,
        'address-info-address': address,
        'address-info-remark': address_remark,
        'address-info-name': user_name,
        'address-info-phone': phone
    });

    eleF.find('.address-info-address').val(address);
    eleF.find('.address-info-name').val(user_name);
    eleF.find('.address-info-phone').val(phone);
    eleF.find('.address-info-remark').val(address_remark);
    if (address_remark) {
        eleF.find('.address-info-remark').parents('tr').show();
    } else {
        eleF.find('.address-info-remark').parents('tr').hide();
    }

    ui_verify_address(
        $(eleF.find('.address-info-address')).parent(),
        x, y
    );
    /*检查联系人*/
    ui_verify_contact(
        $(eleF.find('.address-info-name')).parent()
    );
    /*检查联系人电话*/
    ui_verify_phone(
        $(eleF.find('.address-info-phone')).parent()
    );
    on_ui_update();
}
function on_address_change(ele) {
    eleF = ele.parents('.takeGoods-js');
    eleF.attr({
        'address-info-x': "",
        'address-info-y': ""
    });

    eleF.find('.address-info-remark').parents('tr').hide();

    ui_verify_address(
        $(eleF.find('.address-info-address')).parent(),
        "", ""
    );

    on_ui_update();
}

function on_baidu_selected(ele, province, city, address, address_remark, x, y) {
    eleF = ele.parents('.takeGoods-js');
    eleF.attr({
        'address-info-id': "",
        'address-info-x': x,
        'address-info-y': y,
        'address-info-province': province,
        'address-info-city': city,
        'address-info-address': address,
        'address-info-remark': address_remark
    });

    eleF.find('.address-info-address').val(address);
    eleF.find('.address-info-remark').val(address_remark);
    if (address_remark) {
        eleF.find('.address-info-remark').parents('tr').show();
    } else {
        eleF.find('.address-info-remark').parents('tr').hide();
    }

    ui_verify_address(
        $(eleF.find('.address-info-address')).parent(),
        x, y
    );

    on_ui_update();
}

// 更新订单：距离、费用、车型等相关信息
function ui_update_order(actual_distance, car_type) {
    $('.order_distance').text(actual_distance);

    if (!actual_distance || actual_distance == 0) {
        $('.order_fare').text(0);
    }

    // 检查车型是否存在
    if (!lxn_api.check_car_exist(car_type)) {
        //  new LXN_pop(null, '请选择车型！');
        $(".no-car-type").show();
        $(".order_car_type").parent().parent().hide();
        return;
    }

    var city_code = lxn_get_cookie('locationCity');
    $('.order_car_type').text(lxn_api.get_car_name(car_type, city_code));
    $("#carImgBj>.carImg, #carImgSh>.carImg").each(function(){
        var $this = $(this);
        var _type = $this.attr("car_type");
        var _pay = _get_pay(actual_distance, _type);
        $this.find("em").text(_pay);
    });

    var pay = _get_pay(actual_distance, car_type);
    $('.order_fare').text(pay);
    return;
}

function _get_pay(actual_distance, car_type) {
    var city_code = lxn_get_cookie('locationCity');

    var initial_distance = lxn_api.get_car_initial_distance(car_type);
    var initial_fare = lxn_api.get_car_initial_fare(car_type);
    var price_per_km = lxn_api.get_car_price_per_km(car_type, city_code);

    var has_night_surcharge = false;
    var premium = 0;

    var discount_initial = 1;
    var discount_base = 1;
    var discount_all = 1;

    var coupons_value = 0;

    var pay = new LXN_order(actual_distance,
            initial_distance, initial_fare, price_per_km,
            has_night_surcharge, premium,
            discount_initial, discount_base, discount_all, coupons_value).get_order_pay();
    return pay;
}

function get_pickup_info() {
    var $obj = $('.pickup');
    var name = lxn_trim($('.pickup').find('.address-info-name').val()); // 发货联系人姓名
    var phone = $('.pickup').find('.address-info-phone').val(); // 发货联系人电话
    var x = $('.pickup').attr('address-info-x'); // 发货地址的纬度 一般小于100  latitude   FIXME
    var y = $('.pickup').attr('address-info-y'); // 发货地址的经度 一般大于100  longitude
    var address = $('.pickup').find('.address-info-address').val(); // 发货地址
    var remark = $('.pickup').find('.address-info-remark').val(); // 发货地址备注
    var province = $('.pickup').attr('address-info-province'); // 发货地址对应的省
    var city = $('.pickup').attr('address-info-city'); // 发货地址对应的城市
    var cid = $('.pickup').attr('address-info-id'); // 常用地址对应的数据库记录id

    var top = $obj.offset().top;
    if (!check_address(x, y)) {
//      new LXN_pop(null, '请选择发货地址！');
        $("body").animate({scrollTop: top}, 800);
        on_input_error($obj.find('.address-info-address'), { name: '发货地址'}, true);
        return false;
    }

    if (!name.length) {
//      new LXN_pop(null, '请填写发货人姓名！');
        $("body").animate({scrollTop: top}, 800);
        on_input_error($obj.find('.address-info-name'), { name: '发货联系人姓名'}, true);
        return false;
    }

    if (!lxn_is_phone(phone)) {
//      new LXN_pop(null, '请检查发货人电话！');
        $("body").animate({scrollTop: top}, 800);
        on_input_error($obj.find('.address-info-phone'), { name: '发货联系人电话'}, true);
        return false;
    }

    return {
        name: name, phone: phone, x: x, y: y, city: city, cid: cid,
        address: address, remark: remark, province: province
    };
}

function get_deliver_list() {
    var deliver_list = [];
    var ret = true;
    $(".tab-js.takeGoods-js").each(function() {
        var $this = $(this);
        var name = lxn_trim($(this).find('.address-info-name').val());// 收货联系人姓名
        var phone = $(this).find('.address-info-phone').val(); // 收货联系人电话
        var x = $(this).attr('address-info-x'); // 收货地址的纬度 一般小于100  latitude    FIXME
        var y = $(this).attr('address-info-y'); // 收货地址的经度 一般大于100  longitude
        var address = $(this).find('.address-info-address').val(); // 收货地址
        var remark = $(this).find('.address-info-remark').val(); // 收货地址备注
        var province = $(this).attr('address-info-province'); // 收货地址对应的省
        var city = $(this).attr('address-info-city'); // 收货地址对应的城市
        var cid = $(this).attr('address-info-id'); // 常用地址对应的数据库记录id*/

        var top = $this.offset().top;
        if (!check_address(x, y)) {
//          new LXN_pop(null, '请检查收货地址！');
            $("body").animate({scrollTop: top}, 800);
            on_input_error($this.find('.address-info-address'), { name: '收货地址'}, true);
            ret = false;
            return false;
        }

        if (!name.length) {
//          new LXN_pop(null, '请检查收货人姓名！');
            $("body").animate({scrollTop: top}, 800);
            on_input_error($this.find('.address-info-name'), { name: '收货联系人姓名'}, true);
            ret = false;
            return false;
        }

        if (!lxn_is_phone(phone)) {
//          new LXN_pop(null, '请检查收货人电话！');
            $("body").animate({scrollTop: top}, 800);
            on_input_error($this.find('.address-info-phone'), { name: '收货联系人电话'}, true);
            ret = false;
            return false;
        }

        deliver_list.push({name: name, phone: phone, x: x, y: y, address: address, remark: remark,
            province: province, city: city, cid: cid
        });
    });

    if (!ret) {
        return false;
    }

    if (deliver_list.length == 0 ) {
        new LXN_pop(null, '请添加收货信息！');
        return false;
    }
    return deliver_list;

}


