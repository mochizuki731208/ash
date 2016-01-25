var local = null;

function get_geo_Address(address, callback) {
    var myGeo = new BMap.Geocoder();
    myGeo.getPoint(address, function (point) {
        callback(point);
    }, $('#current-city').text());
}
var MapService = function (input, result, cityName, lat, lng) {
    this.init = function () {
        var me = this;
        // 百度地图API功能
        if (!local) {
            local = new BMap.LocalSearch(map);
            local.setLocation(cityName);
            local.setPageCapacity(9);
            local.setSearchCompleteCallback(function (rs) {
                $('#' + result).empty();
                if (local.getStatus() == BMAP_STATUS_SUCCESS) {
                    var size=rs.getCurrentNumPois()>10?10:rs.getCurrentNumPois();
                    for (j = 0; j < size; j++) {
                        $('#' + result).show();
                        me.addResult(rs.getPoi(j),window.put);
                    }
                }
            });
            $(window).resize(
                function () {
                    var $container = $('#' + result);
                    var $input = window.tmpInput;
                    if ($input) {
                        var offset = $input.offset();
                        $container.css({
                            width: $input.width() + 'px',
                            top: offset.top + $input.outerHeight() + 10 + 'px',
                            left: offset.left + 'px'
                        });
                    }
                }
            )
        }

        $('#' + input).keyup(function () {
            var kw = $(this).val();
            window.put=input;
            local.search(kw, {forceLocal: true});
            window.tmpInput = $('#' + input);
            var $container = $('#' + result);
            var offset = $('#' + input).offset();
            $container.css({
                width: $('#' + input).width() + 'px',
                top: offset.top + $('#' + input).outerHeight() + 10 + 'px',
                left: offset.left + 'px'
            });

            if ($('#' + result).children().length == 0) {
                $('#' + result).hide();
            } else {
                $('#' + result).show();
            }
        })
    },
        this.get_geo_info = function (lng, lat, callback) {
            var myGeo = new BMap.Geocoder();
            myGeo.getLocation(new BMap.Point(lng, lat), function (res) {
                if (null != res) {
                    callback && callback(res["addressComponents"]);
                }
            });
        },
        this.addResult = function (position,bingInput) {
            var me = this;
            var title = position.title;
            var address = position.address || "";
            var lat = position.point.lat;
            var lng = position.point.lng;
            var province = position.province;
            var city = position.city;
            var obj = {};
            obj.title = title;
            obj.city = city;
            obj.province = province;
            obj.lat = lat;
            obj.lng = lng;

            switch (position.type) {
                case BMAP_POI_TYPE_NORMAL : // 一般位置点
                    if (!address) {
                        me.get_geo_info(lng, lat, function (res) {
                            if (null != res) {
                                var str = res["city"] + res["district"] + res["street"] + res["streetNumber"];
                                obj.address = str;
                                var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                                $('#' + result).append(html);
                            }
                        });
                    } else if (new RegExp("^(.*市)(.*[区县])$").test(address)) {
                        me.get_geo_info(lng, lat, function (res) {
                            if (null != res) {
                                var str = res["city"] + res["district"] + res["street"] + res["streetNumber"];
                                obj.address = str;
                                var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                                $('#' + result).append(html);
                            }
                        });
                    } else if (new RegExp("^(.*市)(.*[区县]).+").test(address)) {
                        obj.address = address;
                        var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                        $('#' + result).append(html);
                    } else if (new RegExp(".*(?!市).*[区县]").test(address)) {
                        me.get_geo_info(lng, lat, function (res) {
                            if (null != res) {
                                obj.address = res["city"] + address;
                                var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                                $('#' + result).append(html);
                            }
                        });
                    } else if (!(new RegExp("^(.*市).*[区县]").test(address))) {
                        me.get_geo_info(lng, lat, function (res) {
                            if (null != res) {
                                obj.address = res["city"] + res["district"] + address;
                                var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                                $('#' + result).append(html);
                            }
                        });
                    } else {
                        obj.address = address;
                        var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                        $('#' + result).append(html);
                    }
                    break;
                case BMAP_POI_TYPE_BUSSTOP : // 公交车站位置点
                    me.get_geo_info(lng, lat, function (res) {
                        if (null != res) {
                            var str = res["city"] + res["district"] + res["street"] + res["streetNumber"];
                            obj.address = str;
                            var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                            $('#' + result).append(html);
                        }
                    });
                    break;
                case BMAP_POI_TYPE_SUBSTOP : // 地铁车站位置点
                    me.get_geo_info(lng, lat, function (res) {
                        if (null != res) {
                            obj.address = res["city"] + res["district"] + address + title + '地铁站';
                            var html = '<li data-input='+bingInput+' class="li" data-lng=' + lng + ' data-lat=' + lat + '>' + title + '<span style="color: gray;">' + obj.address + '</span></li>';
                            $('#' + result).append(html);
                        }
                    });
                    break;
            }
        }
}