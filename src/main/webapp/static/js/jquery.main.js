$(document).ready(function(){
	clearInputs();
	initTimer();
	initMask();
});

function initMask () {
	var date = $('.mask-date');
	var phone = $('.mask-phone');
	var datePlace = date.val();
	var phonePlace = phone.val();

	$.mask.definitions["j"]="[0123]";
	$.mask.definitions["k"]="[01]";
	$.mask.definitions["l"]="[12]";

	date.mask('j9.k9.l999',{placeholder:datePlace}).val(datePlace).blur(function() {
		if($(this).val() == '') $(this).val(datePlace);
	});
	phone.mask('+7 (999) 999-99-99',{placeholder:phonePlace}).val(phonePlace).blur(function() {
		if($(this).val() == '') $(this).val(phonePlace);
	});
}

function initTimer () {
	$('[data-timer]').each(function(){
		var hold = $(this);
		var data = hold.data('timer').split('|');
		var time = {
			day: data[0].split('.')[0]/1,
			month: data[0].split('.')[1]/1,
			year: data[0].split('.')[2]/1,
			hours: data[1].split(':')[0]/1,
			minutes: data[1].split(':')[1]/1,
			seconds: data[1].split(':')[2]/1,
		};
		var newDate = new Date(time.year, time.month-1, time.day, time.hours, time.minutes, time.seconds).getTime();
		var dom = {
			d: hold.find('.days span'),
			h: hold.find('.hours span'),
			m: hold.find('.minutes span'),
			s: hold.find('.seconds span')
		}
		var timer = {
			d:0,
			h:0,
			m:0,
			s:0
		}

		var set = function () {
			nowDate = new Date().getTime();
			if(newDate-nowDate <= 0) if(time) clearTimeout(time);
			timer.d = Math.floor((newDate-nowDate)/(1000*60*60*24));
			timer.h = Math.floor(((newDate-nowDate) - timer.d*(1000*60*60*24))/(1000*60*60));
			timer.m = Math.floor(((newDate-nowDate) - timer.d*(1000*60*60*24) - timer.h*(1000*60*60))/(1000*60));
			timer.s = Math.floor(((newDate-nowDate) - timer.d*(1000*60*60*24) - timer.h*(1000*60*60) - timer.m*(1000*60))/(1000));
			
			$.each(dom, function(key, val){
				timer[key] = timer[key].toString();
				if(timer[key].length < 2) timer[key] = '0'+timer[key];
				val.eq(0).text(timer[key].charAt(0));
				val.eq(1).text(timer[key].charAt(1));
			});
		}
		set();

		time = setInterval(set, 999);
		
	});
}

function clearInputs(){
	$('input.placeholder, textarea.placeholder').each(function(){
		var _el = $(this);
		var _val = _el.val();
		
		_el.bind('focus', function(){
			if (this.value == _val) {
				this.value = '';
				$(this).removeClass('placeholder');
			}
			$(this).addClass('input-focus');
		}).bind('blur', function(){
			if(this.value == '') {
				this.value = _val;
				$(this).addClass('placeholder');
			}
			$(this).removeClass('input-focus');
		});
	});
}


! function(a) {
	"function" == typeof define && define.amd ? define(["jquery"], a) : a("object" == typeof exports ? require("jquery") : jQuery)
}(function(a) {
	var b, c = navigator.userAgent,
		d = /iphone/i.test(c),
		e = /chrome/i.test(c),
		f = /android/i.test(c);
	a.mask = {
		definitions: {
			9: "[0-9]",
			a: "[A-Za-z]",
			"*": "[A-Za-z0-9]"
		},
		autoclear: true,
		dataName: "rawMaskFn",
		placeholder: " "
	}, a.fn.extend({
		caret: function(a, b) {
			var c;
			if (0 !== this.length && !this.is(":hidden")) return "number" == typeof a ? (b = "number" == typeof b ? b : a, this.each(function() {
				this.setSelectionRange ? this.setSelectionRange(a, b) : this.createTextRange && (c = this.createTextRange(), c.collapse(!0), c.moveEnd("character", b), c.moveStart("character", a), c.select())
			})) : (this[0].setSelectionRange ? (a = this[0].selectionStart, b = this[0].selectionEnd) : document.selection && document.selection.createRange && (c = document.selection.createRange(), a = 0 - c.duplicate().moveStart("character", -1e5), b = a + c.text.length), {
				begin: a,
				end: b
			})
		},
		unmask: function() {
			return this.trigger("unmask")
		},
		mask: function(c, g) {
			var h, i, j, k, l, m, n, o;
			if (!c && this.length > 0) {
				h = a(this[0]);
				var p = h.data(a.mask.dataName);
				return p ? p() : void 0
			}
			return g = a.extend({
				autoclear: a.mask.autoclear,
				placeholder: a.mask.placeholder,
				completed: null
			}, g), i = a.mask.definitions, j = [], k = n = c.length, l = null, a.each(c.split(""), function(a, b) {
				"?" == b ? (n--, k = a) : i[b] ? (j.push(new RegExp(i[b])), null === l && (l = j.length - 1), k > a && (m = j.length - 1)) : j.push(null)
			}), this.trigger("unmask").each(function() {
				function h() {
					if (g.completed) {
						for (var a = l; m >= a; a++)
							if (j[a] && C[a] === p(a)) return;
						g.completed.call(B)
					}
				}

				function p(a) {
					return g.placeholder.charAt(a < g.placeholder.length ? a : 0)
				}

				function q(a) {
					for (; ++a < n && !j[a];);
					return a
				}

				function r(a) {
					for (; --a >= 0 && !j[a];);
					return a
				}

				function s(a, b) {
					var c, d;
					if (!(0 > a)) {
						for (c = a, d = q(b); n > c; c++)
							if (j[c]) {
								if (!(n > d && j[c].test(C[d]))) break;
								C[c] = C[d], C[d] = p(d), d = q(d)
							}
						z(), B.caret(Math.max(l, a))
					}
				}

				function t(a) {
					var b, c, d, e;
					for (b = a, c = p(a); n > b; b++)
						if (j[b]) {
							if (d = q(b), e = C[b], C[b] = c, !(n > d && j[d].test(e))) break;
							c = e
						}
				}

				function u() {
					var a = B.val(),
						b = B.caret();
					if (a.length < o.length) {
						for (A(!0); b.begin > 0 && !j[b.begin - 1];) b.begin--;
						if (0 === b.begin)
							for (; b.begin < l && !j[b.begin];) b.begin++;
						B.caret(b.begin, b.begin)
					} else {
						for (A(!0); b.begin < n && !j[b.begin];) b.begin++;
						B.caret(b.begin, b.begin)
					}
					h()
				}

				function v() {
					A(), B.val() != E && B.change()
				}

				function w(a) {
					if (!B.prop("readonly")) {
						var b, c, e, f = a.which || a.keyCode;
						o = B.val(), 8 === f || 46 === f || d && 127 === f ? (b = B.caret(), c = b.begin, e = b.end, e - c === 0 && (c = 46 !== f ? r(c) : e = q(c - 1), e = 46 === f ? q(e) : e), y(c, e), s(c, e - 1), a.preventDefault()) : 13 === f ? v.call(this, a) : 27 === f && (B.val(E), B.caret(0, A()), a.preventDefault())
					}
				}

				function x(b) {
					if (!B.prop("readonly")) {
						var c, d, e, g = b.which || b.keyCode,
							i = B.caret();
						if (!(b.ctrlKey || b.altKey || b.metaKey || 32 > g) && g && 13 !== g) {
							if (i.end - i.begin !== 0 && (y(i.begin, i.end), s(i.begin, i.end - 1)), c = q(i.begin - 1), n > c && (d = String.fromCharCode(g), j[c].test(d))) {
								if (t(c), C[c] = d, z(), e = q(c), f) {
									var k = function() {
										a.proxy(a.fn.caret, B, e)()
									};
									setTimeout(k, 0)
								} else B.caret(e);
								i.begin <= m && h()
							}
							b.preventDefault()
						}
					}
				}

				function y(a, b) {
					var c;
					for (c = a; b > c && n > c; c++) j[c] && (C[c] = p(c))
				}

				function z() {
					B.val(C.join(""))
				}

				function A(a) {
					var b, c, d, e = B.val(),
						f = -1;
					for (b = 0, d = 0; n > b; b++)
						if (j[b]) {
							for (C[b] = p(b); d++ < e.length;)
								if (c = e.charAt(d - 1), j[b].test(c)) {
									C[b] = c, f = b;
									break
								}
							if (d > e.length) {
								y(b + 1, n);
								break
							}
						} else C[b] === e.charAt(d) && d++, k > b && (f = b);
					return a ? z() : k > f + 1 ? g.autoclear || C.join("") === D ? (B.val() && B.val(""), y(0, n)) : z() : (z(), B.val(B.val().substring(0, f + 1))), k ? b : l
				}
				var B = a(this),
					C = a.map(c.split(""), function(a, b) {
						return "?" != a ? i[a] ? p(b) : a : void 0
					}),
					D = C.join(""),
					E = B.val();
				B.data(a.mask.dataName, function() {
					return a.map(C, function(a, b) {
						return j[b] && a != p(b) ? a : null
					}).join("")
				}), B.one("unmask", function() {
					B.off(".mask").removeData(a.mask.dataName)
				}).on("focus.mask click.mask", function() {
					if (!B.prop("readonly")) {
						clearTimeout(b);
						var a;
						E = B.val(), a = A(), b = setTimeout(function() {
							z(), a == c.replace("?", "").length ? B.caret(0, a) : B.caret(a)
						}, 10)
					}
				}).on("blur.mask", v).on("keydown.mask", w).on("keypress.mask", x).on("input.mask paste.mask", function() {
					B.prop("readonly") || setTimeout(function() {
						var a = A(!0);
						B.caret(a), h()
					}, 0)
				}), e && f && B.off("input.mask").on("input.mask", u), A()
			})
		}
	})
});
