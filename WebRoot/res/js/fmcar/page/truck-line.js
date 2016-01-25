/**
 * Created by shijiuwei on 2015/3/1.
 */
define(function (require) {
    require("bootstrap");    
    require('plugins/cityplugin/cityplug');
    //城市选择
    $('.addrselector').addrDropmenu({ level: 3 });
    return {};
});