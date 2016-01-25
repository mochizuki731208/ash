rooturl = '/';
jQuery.fn.uploadFile = function(d, g, fieldId) {
	var f = {
		path : "",
		isthumb : 0,
		iswater : 0,
		maxsize : 2048,
		types : "doc|docx|txt|xls|xlsx|pdf|ppt|png|jpg|gif|jpeg",
		limit_w : 0,
		limit_h : 0,
		image : "image",
		resize : [ 0 ]
	};
	if (typeof g != "undefined" & g instanceof Function) {
	} else {
		var g = function(h, i) {
			if (h == "1") {
				$("img." + f.image).attr("src", i)
			} else {
				return h
			}
		}
	}
	$.extend(f, d);
	var c = "";
	for ( var e in f) {
		c += "&" + e + "=" + f[e]
	}
	var a = $(this).parents("a");
	var b = function(h, i, j) {
		h.parent().parent().find("div.u_tb_red").hide();
		h.parent().find("font.jobphototips").remove();
		h.parent().find("span.waitupload").remove();
		h.parent().find("span.finishupload").remove();
		h.parent().find("span.otherupload").remove();
		if (i == "start") {
			h
					.before('<span class="waitupload"><img src="'
							+ (rooturl?rooturl:"/")
							+ 'res/images/loading_c.gif" height="24"/> \u8bf7\u8010\u5fc3\u7b49\u5f85\uff0c\u6b63\u5728\u4e0a\u4f20\u4e2d...</span>');
			h.hide()
		} else {
			if (i == "finish") {
				h
						.after('<span class="finishupload" style="line-height:45px;"><font color="#666666">'
								+ j + "</font></span>");
				h.show()
			} else {
				h
						.after('<span class="otherupload" style="line-height:45px;"><font color="#990000">'
								+ j + "</font></span>");
				h.show()
			}
		}
	};
	$(this)
			.on(
					"change",
					function() {
						b(a, "start");
						$
								.ajaxFileUpload({
									url : (rooturl?rooturl:"/")
											+ "common/upload2?_c=upload&_a=uploadAjax"
											+ c + "&random="
											+ parseInt(100000 * Math.random()),
									secureuri : false,
									fileElementId : fieldId,
									data : {
										name : "logan",
										id : "id"
									},
									success : function(i, h) {
										if (i.status == 1) {
											b(a, "finish",
													"\u4e0a\u4f20\u6210\u529f\uff01");
												g(i.data)
										} else {
											b(a, "error",
														"\u6587\u4ef6\u8d85\u51fa\u6700\u5927\u9650\u5236!")
										}
									},
									error : function(i, h, j) {
										alert("err...."+j)
									}
								})
					})
};