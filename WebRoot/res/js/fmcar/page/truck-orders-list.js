/**
 * Created by shijiuwei on 2015/3/1.
 */
define(function (require) {
    require("bootstrap");
    
    var $chakancheliang = $("#chakancheliang");
    $chakancheliang.find('.daichengjiao').on('click', function () {
        window.location.href = "/ab/fmcarorders/list/waiting";
    });
    $chakancheliang.find('.jinxingzhong').on('click', function () {
        window.location.href = "/ab/fmcarorders/list/working";
    });

    var delThisCar = function (id) {
        var dtd = $.Deferred();
        $.ajax({
            url: "/ab/fmcarorders/delete/" + id,
            cache:false,
            dataType:"json"
        }).done(function(json) {
            dtd.resolve(json);
        });
        return dtd.promise();
    };
    
    $(".delThisCar").on("click", function () {
        var $thisdel = $(this),
            mmd = $("#myModalDel"),
            mmdmb = mmd.find('.modal-body'),
            mmldy = $("#myModalLabelDelYes");
        mmd.modal();
        mmldy.off("click").on("click", function () {
            $.when(delThisCar($thisdel.attr('id')))
                .then(function (json) {
                	debugger;
                    if(json.result==="success"){
                        mmd.modal('hide');
                        $thisdel.parents('tr').fadeOut("normal");
                        console.log($thisdel.parents('tr'));
                    }else{
                        mmdmb.html('<p>删除失败，请重试！</p>')
                        mmldy.text("重 试");
                    }
                });
        })
    });
    
    $(".viewThisOrder").on("click", function () {
        var $thisdel = $(this);
        var id = $thisdel.attr('id');
        
        window.location.href = "/ab/fmcarorders/orderid-" + id;
        //$.post("ab/fmcarorders/" + id, {} ,function(data){},"json");
    });
    
    $(".editThisOrder").on("click", function () {
    	var $thisdel = $(this);
        var id = $thisdel.attr('id');
        
        window.location.href = "/ab/fmcarorders/orderid-" + id;
        //$.post("ab/fmcarorders/" + id, {} ,function(data){},"json");
    });
    
     return {};
});