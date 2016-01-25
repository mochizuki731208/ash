(function($){
    $.extend({
        namespace: function(ns){
            if(typeof ns != 'string'){
                throw new Error('namespace must be a string');
            }

            var ns_arr = ns.split('.');
            var parent = window;
            for(var i in ns_arr){
                parent[ns_arr[i]] = parent[ns_arr[i]] || {};
                parent = parent[ns_arr[i]];
            }
        }
    });

})(jQuery);

(function($){
    $.fn.serializeJson=function(){
        var serializeObj={};
        var array=this.serializeArray();
        var str=this.serialize();
        $(array).each(function(){
            if(serializeObj[this.name]){
                if($.isArray(serializeObj[this.name])){
                    serializeObj[this.name].push(this.value);
                }else{
                    serializeObj[this.name]=[serializeObj[this.name],this.value];
                }
            }else{
                serializeObj[this.name]=this.value;
            }
        });
        return serializeObj;
    };
})(jQuery);

var ZXB = {};
// 添加Cookie
function addCookie(name, value, options) {
    if (arguments.length > 1 && name != null) {
        if (options == null) {
            options = {};
        }
        if (value == null) {
            options.expires = -1;
        }
        if (typeof options.expires == "number") {
            var time = options.expires;
            var expires = options.expires = new Date();
            expires.setTime(expires.getTime() + time * 1000);
        }
        document.cookie = encodeURIComponent(String(name)) + "=" + encodeURIComponent(String(value)) + (options.expires ? "; expires=" + options.expires.toUTCString() : "") + (options.path ? "; path=" + options.path : "") + (options.domain ? "; domain=" + options.domain : ""), (options.secure ? "; secure" : "");
    }
}

// 获取Cookie
function getCookie(name) {
    if (name != null) {
        var value = new RegExp("(?:^|; )" + encodeURIComponent(String(name)) + "=([^;]*)").exec(document.cookie);
        return value ? decodeURIComponent(value[1]) : null;
    }
}

// 移除Cookie
function removeCookie(name, options) {
    addCookie(name, null, options);
}

function hasHistory(){
    var historyValue= window.history.length;
    if (historyValue== undefined || historyValue ==null || historyValue <= 1) {
        return false;
    }else{
        return true;
    }
}

(function($) {

    $(document).ajaxComplete(function(event, request, settings) {
        var loginStatus = request.getResponseHeader("loginStatus");
        var tokenStatus = request.getResponseHeader("tokenStatus");

        if (loginStatus == "accessDenied") {
            $.redirectLogin(location.href, "请登录后再进行操作");
        } else if (tokenStatus == "accessDenied") {
            var token = getCookie("token");
            if (token != null) {
                $.extend(settings, {
                    global: false,
                    headers: {token: token}
                });
                $.ajax(settings);
            }
        }
    });

    /**
     * 获取url参数
     * @param name
     * @returns {*}
     * @constructor
     */
    $.GetQueryString = function (name)
    {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
        var r = window.location.search.substr(1).match(reg);
        if (r!=null) return (r[2]); return null;
    }

})(jQuery);
