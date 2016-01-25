/**
 * Created by shijiuwei on 2015/2/28.
 */
define(function (require) {
    require("bootstrap");
    require("plugins/formValidation/zh_CN");
    require("plugins/cityplugin/city-select-zh-CN");
    loadingSelectForJson("#runcity","请输入并选择常跑城市",0,5);
    /*2015-3-8*/

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
    	debugger;
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
    return {};
});