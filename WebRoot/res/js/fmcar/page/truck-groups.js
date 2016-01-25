/**
 * Created by mochizuki on 2015/10/2.
 */
define(function (require) {
    require("bootstrap");
    require("plugins/formValidation/zh_CN");
    require("plugins/cityplugin/city-select-zh-CN");
    
    delThisGroup = function (id) {
        var dtd = $.Deferred(),
            myId = id || null;
        $.ajax({
            url: "/ab/fmcargroup/deletebyid?id="+id,
            cache:false,
            data:{
            	carId:myId
            },
            dataType:"json"
        }).done(function(json) {
            dtd.resolve(json);
        });
        return dtd.promise();
    };


$(".delThisGroup").off("click").on("click", function() {
	var $thisdel = $(this), mmd = $("#myModalDel"), mmdmb = mmd
			.find('.modal-body'), mmldy = $("#myModalLabelDelYes");

	var groupId = $(this).attr("id");//$(this).parent().parent().find("td:eq(0)").text();
	debugger;
	mmd.modal();
	mmldy.off("click").on("click", function() {
		$.when(delThisGroup(groupId)).then(function(json) {
			if (json) {
				window.location.reload();
			} else {
				alert("删除失败.请重试！");
			}
		});
	});

});
  
    return {};
});