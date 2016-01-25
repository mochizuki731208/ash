(function (c) {
    c.fn.addrDropmenu = function (g) {
        function n(a) {
            var c = "";
            if (a) {
                for (var d in a) {
                    for (var e in a[d]) {
                        c += '<li><a c="' + e + '">' + a[d][e] + "</a></li>";
                    }
                }
            }
            return c
        }
        var b = {
            initTabStr: "请选择", delyTime: 50, overTime: 600, level: 3
        },
        b = c.extend({}, b, g), d = b.initTabStr, D = b.delyTime, E = b.overTime, w = b.level;
        g = ['<div class="addr form-control"><em>' + d + "</em></div>", '<dl class="dn"><dt>', '<a class="tab crt"><em>' + d + "</em><i></i></a>", '<a class="tab dn"><em>' + d + "</em><i></i></a>", '<a class="tab dn"><em>' + d + "</em><i></i></a>", '<a class="close" title="\u5173\u95ed"></a><a class="reset">\u91cd\u7f6e</a></dt><dd class="dib"></dd><dd class="dib dn"></dd><dd class="dib dn"></dd></dl>'].join("");
        this.addClass("addrselector"); this.prepend(g); this.find("dd:first").html(n(cityJson["00"]));
        return this.each(function () {
            var a = c(this),
                h = a.find(".addr"),
                b = a.find("dl"),
                e = a.find("dt"),
                l = e.find(".tab"),
                m = a.find("dd"),
                f = a.find("input:hidden"),
                g = l.eq(0),
                x = l.eq(1),
                p = l.eq(2),
                a = m.eq(0),
                q = m.eq(1),
                r = m.eq(2),
                y = f.eq(0),
                s = f.eq(1),
                z = f.eq(2),
                t = f.eq(3),
                u = f.eq(4),
                v = f.eq(5),
                A = y.val(),
                B = z.val(),
                C = u.val();
            e.on("click", ".close", function () {
                c(this).text();
                var k;
                if (v.val() == "" || v.val() == "不限") {
                    if (t.val() != "") {
                        k = s.val() + "-" + t.val();
                    } else {
                        k = s.val();
                    }
                } else {
                    k = t.val() + "-" + v.val();
                }
                h.removeClass("hover").find("em").text(k || d); b.hide(D)
            });
            a.on("click", "a",
                function () {
                    var k = c(this).attr("c"), a = c(this).text();
                    f.val("");
                    h.find("em").text(a);
                    y.val(k);
                    s.val(a);
                    p.addClass("dn");
                    g.find("em").text(a);
                    1 != w ? (k = n(cityJson[k]), q.html(k), x.trigger("click").find("em").text(d)) : e.find(".close").trigger("click");
                    if (a == "北京" || a == "天津" || a == "上海" || a == "重庆") {
                        z.val(0);
                        $(".close").click();
                    }
                });
            q.on("click", "a", function () {
                var a = c(this).attr("c"), b = c(this).text(), f = u.add(v); x.find("em").text(b); z.val(a); t.val(b); f.val("");
                b = s.val() + "-" + t.val();
                h.find("em").text(b); 2 != w ? (a = n(cityJson[a])) ? (r.html(a), p.trigger("click").find("em").text(d)) : e.find(".close").trigger("click") : e.find(".close").trigger("click")
            });
            r.on("click", "a", function () {
                var a = c(this).attr("c"), b = c(this).text(); p.find("em").text(b); u.val(a); v.val(b); e.find(".close").trigger("click")
            });
            e.on("click", ".tab", function () {
                var a = c(this), b = l.index(this); a.removeClass("dn").addClass("crt").siblings().removeClass("crt"); m.addClass("dn").eq(b).removeClass("dn")
            });
            A && a.find("a[c=" + A + "]").trigger("click"); B && q.find("a[c=" + B + "]").trigger("click"); C && r.find("a[c=" + C + "]").trigger("click");
            e.on("click", ".reset", function () {
                f.val("").trigger("change"); g.trigger("click").find("em").text(d); l.not(g).addClass("dn"); h.find("em").text(d)
            });
            m.on("click", "a", function () {
                f.trigger("change")
            });
            h.click(function () {
                b.trigger("mouseenter")
            }).mouseleave(function () {
                b.trigger("mouseleave")
            }); b.mouseenter(function () {
                h.addClass("hover"); b.stop(!0).show()
            }).mouseleave(function () {
                b.delay(E).hide(0, function () { h.removeClass("hover") })
            })
        })
    }
})(jQuery);