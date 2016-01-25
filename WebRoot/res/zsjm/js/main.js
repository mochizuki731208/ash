
$.fn.radioClass = function(className,type,callback){
    if(typeof callback !== 'function')
        callback = $.noop
    var _this = this
    if(type === 'hover'){
        _this.on('mouseenter',function(){
            if($(this).hasClass(className))return;
            if(callback.call(this)===false)return
            _this.removeClass(className)
            $(this).addClass(className)
        })
    }else{
        _this.on('click',function(){
            if($(this).hasClass(className))return;
            if(callback.call(this)===false)return
            _this.removeClass(className)
            $(this).addClass(className)
        })
    }
}
if($('#i-wrap').length>0){
    var ip6ctrlidx = 0
    var ip6ctrls = $('.ctrlpoints>span')
    var cellWidth = $('#i-wrap').children('img').width()
    var iCount = $('#i-wrap').children('img').length
    var d_si
    clearInterval(d_si)
    d_si = setInterval(function(){
        exchange()
    },2000)
}
function exchange(){
    var ctrl = $('#i-wrap')
    if(ctrl.is(':animated'))return;
    ctrl.animate({marginLeft:-cellWidth},function(){
        ctrl.css('marginLeft',0).children(':first').appendTo(ctrl)
    })
    ip6ctrls.eq(ip6ctrlidx).removeClass('active')
    if(++ip6ctrlidx>iCount-1)ip6ctrlidx=0
    ip6ctrls.eq(ip6ctrlidx).addClass('active')
}
if($('.banner-wrap').length>0){
    var bannerWrap = $('.banner-wrap')
    var banners = bannerWrap.find('._banner')
    var ctrls = bannerWrap.children('.ctrls').children('span')
    var b_count = banners.length
    var idx = 0
    var setTo = null

    setTo=setTimeout(fadeInOut,3000)

    ctrls.radioClass('active','hover',function(){
        for(var i=0; i<b_count; i++){
            if(banners.eq(i).is(':animated'))return false;
        }
        fadeInOut($(this).index())
    })
}
function fadeInOut(showwho){
    clearTimeout(setTo)
    banners.eq(idx).fadeOut(1000)
    ctrls.eq(idx).removeClass('active')
    if(++idx>=b_count)idx=0
    if(typeof showwho === 'number')idx=showwho
    banners.eq(idx).delay(500).fadeIn(1000,function(){
        setTo=setTimeout(fadeInOut,3000)
    })
    ctrls.eq(idx).addClass('active')
}
if($('.sidebar').length>0){
    var sidebar = $('.sidebar')
    var cells = sidebar.children('.cell')
    cells.hover(function(){
        $(this).children('.txt').stop().animate({width:120},400)
    },function(){
        $(this).children('.txt').stop().animate({width:0},400)
    })
    cells.children('.ico-arrowup').on('click',function(){
        $('html,body').animate({scrollTop:0})
    })
}
if($('.upbar').length>0){
    var upbar = $('.upbar')
    upbar.css('right',($(window).width()-1200)/2-70+'px')
    upbar.on('click',function(){
        $('html,body').animate({scrollTop:0})
    })
}
if($('.ui-tabs').length>0&&$('.ui-content').length>0){
    var tabs = $('.ui-tabs>.ui-tab')
    var contents = $('.ui-content')
    tabs.radioClass('active','hover',function(){
        contents.hide()
        contents.eq($(this).index()).show()
    })
}
if($.fn.iCheck){
    $('input').iCheck({
        radioClass: 'iradio_minimal-green',
        checkboxClass: 'icheckbox_minimal-green',
        increaseArea: '20%' // optional
    })
}
var bodyheader = $('body header')
if(bodyheader.hasClass('recruitPage')){
    var isRecruitPage = true
    var htmlwrap = bodyheader.children('div')
    var oldhtml = htmlwrap.html()
    var newhtml = '<img src="images/recruit-tit-head.png" style="float:left;margin:12px 0 0 70px"><a style="position:static;float:right;margin:8px 70px 0 0" href="#anchor-msbm"><img src="images/recruit-head-btn.png"></a>'
}
$(window).on('scroll',function(){
    var sT = $(this).scrollTop()
    if(isRecruitPage){
        if(sT>400&&$(this).scrollTop()<2000){
            htmlwrap.empty().append(newhtml)
        }else{
            htmlwrap.empty().append(oldhtml)
        }
    }
    if(sT > 0){
        bodyheader.css('boxShadow','0 5px 5px rgba(0,0,0,.1)')
    }else{
        bodyheader.css('boxShadow','')
    }
    if(upbar){
        if(sT < 100){
            upbar.hide()
        }else{
            upbar.show()
        }
    }
})
if ($('.city-line').length > 0) {
    var ct_line = $('.city-line')
    var ct_box = ct_line.find('.cities')
    var ct_left = ct_line.find('.point-left')
    var ct_right = ct_line.find('.point-right')
    var ct_cities = ct_box.children('.city')
    var ct_count = ct_cities.length
    var idx_center = 6
    var idx_min = 0
    var idx_max = ct_count - 1
    var idx_move_min = 6
    var idx_move_max = idx_max - idx_move_min
    var ct_width = ct_cities.width()

    ct_left.on('click', function () {
        ctMove(idx_center - 6)
    })
    ct_right.on('click', function () {
        ctMove(idx_center + 6)
    })

    function ctMove(idx, q) {
        if (ct_box.is(':animated')) return
        if (idx < idx_min)
            idx = idx_min
        if (idx > idx_max)
            idx = idx_max
        var idx_move = idx
        if (idx_move < idx_move_min)
            idx_move = idx_move_min
        if (idx > idx_move_max)
            idx_move = idx_move_max

        idx_center = idx_move
        var box_left = -ct_width * (idx_center - idx_move_min)
        ct_box.animate({ marginLeft: box_left }, function () {
            if (q === true) {
                ct_cities.removeClass('active')
                ct_cities.eq(idx).addClass('active')
            }
        })
    }
}
if ($('.querycity').length > 0) {
    var querycity = $('.querycity')
    var citytags = querycity.find('.city')
    var allcities = []
    for (var i = 0; i < ct_cities.length; i++) {
        allcities.push(ct_cities.eq(i).text())
    }
    querycity.find('.search').on({
        blur: function () {
            qCity($(this).val())
        },
        keypress: function (e) {
            qCity($(this).val())
        }
    })
    querycity.find('.search-ico').on({
        click: function (e) {
            qCity($(this).prev().val())
        }
    })
    function qCity(city) {
        if (city == '') {
            querycity.find('.txt.hasnt').hide()
            querycity.find('.txt.has').hide()
            citytags.removeClass('active')
        } else if (allcities.indexOf($.trim(city)) > -1) {
            for (var idx = 0; idx < citytags.length; idx++) {
                if (citytags.eq(idx).text() == $.trim(city)) {
                    //citytags.removeClass('active')
                    //citytags.eq(idx).addClass('active')
                    ctMove(idx, true)
                }
            }
            querycity.find('.txt.hasnt').hide()
            querycity.find('.txt.has').show()
        } else {
            querycity.find('.txt.has').hide()
            querycity.find('.txt.hasnt').show()
            citytags.removeClass('active')
        }
    }
}
if($('.oldusers').length>0){
    var oldusers = $('.oldusers')
    var o_wrap = oldusers.find('[am-row]')
    oldusers.children('.left').on('click',function(){
        oMove(-1)
    })
    oldusers.children('.right').on('click',function(){
        oMove(1)
    })
    var o_si = setTimeout(function(){
        oMove(1)
    },4000)
    function oMove(direct){
        if(o_wrap.is(':animated'))return
        if(direct==1){
            o_wrap.animate({left:'-400px'},function(){
                o_wrap.children(':first').appendTo(o_wrap)
                o_wrap.css('left',0)
            })
        }else if(direct==-1){
            o_wrap.children(':last').prependTo(o_wrap)
            o_wrap.css('left','-400px')
            o_wrap.animate({left:0})
        }
        clearTimeout(o_si)
        o_si = setTimeout(function(){
          oMove(1)
          //澶勭悊涓�
          o_si = setTimeout(arguments.callee, 4000)  
        }, 4000)
    }
}
if($('.join-carts').length>0){
    var j_carts = $('.join-carts')
    j_carts.find('.cart').hover(function(){
        var _shim = $(this).children('.shim')
        _shim.stop().animate({height:'100%'})
            .children('.tit2').stop().animate({marginTop:50})
    },function(){
        var _shim = $(this).children('.shim')
        _shim.stop().animate({height:'40px'})
            .children('.tit2').stop().animate({marginTop:0})
    })
}
if($('select').length>0){
    $('select').selectOrDie()
}