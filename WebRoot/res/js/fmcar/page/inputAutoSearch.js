/**
 * Created by shijiuwei on 2015/3/5.
 */
define(function (require) {
    require('amap');
    return {
        inputAutoSearch: function (map,id,resultId,resultItem) {
//            var map = new AMap.Map("mapContainer",{
//                resizeEnable: true,
//                center:[116.397428,39.90923],//地图中心点
//                zoom:13,//地图显示的缩放级别
//                keyboardEnable:false
//            });
            document.getElementById(id).onkeyup = keydown;
            //输入提示
            function autoSearch() {
                var keywords = document.getElementById(id).value;
                var auto;
                var cityinput = $("#" + resultId).parent().prev().find("input");
                var thisCity = "";
                if (!cityinput.eq(3).val()) {
                    if (!cityinput.eq(1).val()) {
                        thisCity = "";
                    } else {
                        thisCity = cityinput.eq(1).val();
                    }
                } else {
                    thisCity = cityinput.eq(3).val();
                }
                //console.log(thisCity);
                
                //加载输入提示插件
                map.plugin(["AMap.Autocomplete"], function() {
                    var autoOptions = {
                        city: thisCity //城市，默认全国
                    };
                    auto = new AMap.Autocomplete(autoOptions);
                    //查询成功时返回查询结果
                    if ( keywords.length > 0) {
                        AMap.event.addListener(auto,"complete",autocomplete_CallBack);
                        auto.search(keywords);
                    }
                    else {
                        document.getElementById(resultId).style.display = "none";
                    }
                });
            }
            function autocomplete_CallBack(data) {
                var resultStr = "";
                var tipArr = data.tips;
                //var len=tipArr.length;
                if (tipArr&&tipArr.length>0) {
                    for (var i = 0; i < tipArr.length; i++) {
//                    	console.info(tipArr[i]);
                    	resultStr += "<div item='" + i + "' class=" + resultItem + " id='divid" + resultId + (i + 1) + "' style=\"font-size: 13px;cursor:pointer;padding:5px 5px 5px 5px;\">" + tipArr[i].name + "<span style='color:#C1C1C1;'>" + tipArr[i].district + "</span></div>";
                    }
                }
                else  {
                    resultStr = " π__π 亲,人家找不到结果!<br />要不试试：<br />1.请确保所有字词拼写正确<br />2.尝试不同的关键字<br />3.尝试更宽泛的关键字";
                }

                document.getElementById(resultId).curSelect = -1;
                document.getElementById(resultId).tipArr = tipArr;
                document.getElementById(resultId).innerHTML = resultStr;
                document.getElementById(resultId).style.display = "block";
            }

            $("body").on("mouseover","."+resultItem, function () {
                openMarkerTipById(parseInt($(this).attr('item'))+1,this);
            }).on("click","."+resultItem, function () {
                selectResult(parseInt($(this).attr('item')))
            }).on("mouseout","."+resultItem, function () {
                onmouseout_MarkerStyle(parseInt($(this).attr('item'))+1,this);
            });

            //鼠标移入时样式
            function openMarkerTipById(pointid, thiss) {
                thiss.style.background = '#CAE1FF';
            }
            //鼠标移开后样式恢复
            function onmouseout_MarkerStyle(pointid, thiss) {
                thiss.style.background = "";
            }
            //选择输入提示关键字
            function selectResult(index) {
                if (navigator.userAgent.indexOf("MSIE") > 0) {
                    document.getElementById(id).onpropertychange = null;
                    document.getElementById(id).onfocus = focus_callback;
                }
                //截取输入提示的关键字部分
                var text = document.getElementById("divid" + resultId + (index + 1)).innerHTML.replace(/<[^>].*?>.*<\/[^>].*?>/g, "");
                document.getElementById(id).value = text;
                document.getElementById(resultId).style.display = "none";
                addMarker(index);
            }
            //将选择的地址呈现到地图上面
            function addMarker(index){
            	var cityArr = document.getElementById(resultId).tipArr;
            	var obj = cityArr[index];
            	var text = obj.name;
            	var lat = obj.location.lat;
            	var lng = obj.location.lng;
            	var marker = new AMap.Marker({
            		map:map,
            		icon:"http://webapi.amap.com/images/marker_sprite.png",
            		position:[lng,lat]
            	});
            	marker.setMap(map);// 在地图上添加点
            	marker.setLabel({
                    content:text,
                    offset: new AMap.Pixel(0, -20),
                });
            	map.setFitView();
            }
            

            function focus_callback() {
                if (navigator.userAgent.indexOf("MSIE") > 0) {
                    document.getElementById(id).onpropertychange = autoSearch;
                }
            }
            function keydown(event){
                var key = (event || window.event).keyCode;
                var result = document.getElementById(resultId);
                var cur = result.curSelect;
                if(key===40){//down key
                    if(cur + 1 < result.childNodes.length){
                        if(result.childNodes[cur]){
                            result.childNodes[cur].style.background='';
                        }
                        result.curSelect=cur+1;
                        result.childNodes[cur+1].style.background='#CAE1FF';
                        document.getElementById(id).value = result.tipArr[cur+1].name;
                    }
                }else if(key===38){//up key
                    if(cur-1>=0){
                        if(result.childNodes[cur]){
                            result.childNodes[cur].style.background='';
                        }
                        result.curSelect=cur-1;
                        result.childNodes[cur-1].style.background='#CAE1FF';
                        document.getElementById(id).value = result.tipArr[cur-1].name;
                    }
                }else if(key === 13){
                    var res = document.getElementById(resultId);
                    if(res && res['curSelect'] !== -1){
                        selectResult(document.getElementById(resultId).curSelect);
                    }

                }else{
                    autoSearch();
                }
            }
        }
    }
});