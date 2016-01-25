requirejs.config({
    baseUrl: 'res/js/fmcar/page',
    waitSeconds: 60,
    paths: {
        plugins:'../plugins',
        jquery:'../plugins/jquery/jquery.min',
        bootstrap:'../plugins/bootstrap/bootstrap.min',
        amap:"http://webapi.amap.com/maps?v=1.3&key=29159ad301e81162888780815e90f27f"
    },
    shim: {
        "bootstrap": ["jquery"],
        "plugins/formValidation/formValidation.min":["bootstrap"],
        "plugins/formValidation/framework/bootstrap.min":["plugins/formValidation/formValidation.min"],
        "plugins/formValidation/zh_CN":["plugins/formValidation/framework/bootstrap.min"],
        "plugins/bootstrap/bootstrap-datepicker":["bootstrap"],
        "plugins/jqueryJson/jquery.json.min": ["jquery"],
        "plugins/cityplugin/cityplug":['jquery','cityjson.min'],
        "amap":[],
        "plugins/cityplugin/city-select":['jquery'],
        "plugins/cityplugin/city-select-zh-CN":['jquery','plugins/cityplugin/city-select.min']
    }
});