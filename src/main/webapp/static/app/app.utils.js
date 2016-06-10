/* global angular, _ */

(function () {
	"use strict";

		angular.module("app.utils", ["ngSanitize"])
		.factory("utils", function () {
				return {
					watchOnce: function (scope, param, callback) {
						var unregister = scope.$watch(param, function (newValue, oldValue, scope) {
							if (!newValue) {
								return;
							}
							unregister();
							callback && callback(newValue, oldValue, scope);
						});
					},
					watchCollectionOnce: function (scope, param, callback) {
						var unregister = scope.$watch(param, function (newValue, oldValue, scope) {
							if (!newValue) {
								return;
							}
							unregister();
							callback && callback(newValue, oldValue, scope);
						});
					},
					serializeUrlParams: function () {
						var search = location.search.substring(1);
						var obj = {};
						var pairs = search.split("&");
						for (var i in pairs) {
				var split = pairs[i].split("=");
							obj[decodeURIComponent(split[0])] = decodeURIComponent(split[1]);
						}
						return obj;
					},
					loadScript: function (src, callback) {
						var s, r, t;
						r = false;
						s = document.createElement("script");
						s.type = "text/javascript";
						s.src = src;
						s.async = true;
						s.onload = s.onreadystatechange = function () {
						if (!r && (!this.readyState || this.readyState == "complete"))
							{
								r = true;
								callback && callback();
							}
						};
						t = document.getElementsByTagName("script")[0];
						t.parentNode.insertBefore(s, t);
					},
					openDialog: function (uri, closeCallback, name, options) {
						!name && (name = "_blank");
						!options && (options = "directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=750,height=450,top=200,left=200");
						var win = window.open(uri, name, options);
						function checkClose() {
							try {
								if (win === null || win.closed || win.closed === undefined) {
									closeCallback && closeCallback(win);
									return;
								}
							}
							catch (e) {
//									alert("error " + e.toString());
							}
							setTimeout(checkClose, 500);
						}
						checkClose();
						return win;
					},
					parseUrl: function (url) {
						!url && (url = window.location.href);
						var a = document.createElement("a");
						a.href = url;
						var parser = _.pick(a, "href",
								"protocol", // => "http:"
								"host", // => "example.com:3000"
								"hostname", // => "example.com"
								"port", // => "3000"
								"pathname", // => "/pathname/"
								"hash", // => "#hash"
								"search" // => "?search=test"
								);
						parser.page = parser.pathname.substring(parser.pathname.lastIndexOf("/") + 1);
						return parser;
					}
				};
			})
		.filter("mgMaskFilter", function () {
				return function (val, mask) {
					return $.inputmask.format(val, {mask: mask});
				};
			})
		.directive("mgInputMask", function () {
				return {
		restrict: "A",
				require: "^ngModel",
					scope: {
				mask: "@mgInputMask",
						placeholder: "@inputPlaceholder",
						regex: "@inputRegex"
					},
					link: function (scope, elem, attrs, ngModel) {
						if (scope.regex)
						scope.mask = "Regex";
						!scope.placeholder && (scope.placeholder = "_");

						ngModel.$setValidity("mask", false);

						elem.inputmask(scope.mask, {
							placeholder: scope.placeholder,
							regex: scope.regex,
							oncomplete: function (a, b, c) {
								scope.$evalAsync(function () {
									ngModel.$setValidity("mask", true);
								});
							},
							onincomplete: function () {
								scope.$evalAsync(function () {
									ngModel.$setValidity("mask", false);
								});
							},
							oncleared: function () {
								scope.$evalAsync(function () {
									ngModel.$setValidity("mask", false);
								});
							}
						});

						ngModel.$parsers.unshift(function (val) {
							if (!scope.regex)
								return val.substring(0, val.indexOf("_") != - 1 ? val.indexOf("_") : val.length);
							else
								return val;
						});

						ngModel.$formatters.unshift(function (val) {
							setTimeout(function () {
								scope.$evalAsync(function () {
									ngModel.$setValidity("mask", elem.inputmask("isComplete"));
								});
							});
							return val;
						});
					}
				};
			})
		.directive("mgRepeatField", function () {
				return {
		restrict: "A",
				require: "?ngModel",
					scope: {
				origin: "=mgRepeatField",
						caseSensitive: "="
					},
					link: function (scope, elem, attrs, ngModel) {
				scope.$watch("origin", function () {
							ngModel.$setValidity("repeated", compare(scope.origin, ngModel.$viewValue));
						});
						ngModel.$parsers.unshift(function (val) {
							ngModel.$setValidity("repeated", compare(scope.origin, val));
							return val;
						});

						function compare(aStr, bStr) {
							if (scope.caseSensitive || !aStr || !bStr) {
								return aStr == bStr;
							}
							return aStr.toLowerCase() == bStr.toLowerCase();
						}
					}
				};
			})
		.directive("mgPaginator", [function () {
					return {
		restrict: "A",
						scope: {
				currentPage: "=mgPaginator",
						items: "=",
						itemsOnPage: "=",
						cssStyle: "@"
						},
						link: function (scope, elem, attrs) {
							$(elem).pagination({
								items: scope.items,
								itemsOnPage: scope.itemsOnPage,
								cssStyle: scope.cssStyle,
								displayedPages: 3,
								prevText: " ",
								nextText: " ",
								onPageClick: function (pageNumber) {
									scope.$evalAsync(function () {
										scope.currentPage = pageNumber;
									});
								}
							});

						scope.$watch("items", function (items) {
						$(elem).pagination("updateItems", items);
								if ($(elem).pagination("getPagesCount") < $(elem).pagination("getCurrentPage")) {
						$(elem).pagination("selectPage", 1);
									scope.$evalAsync(function () {
										scope.currentPage = 1;
									});
								}
							});

							var hash = window.location.hash || "#page-1";
							hash = hash.match(/#page-(\d+)/);
							if (hash) {
								elem.pagination("selectPage", parseInt(hash[1]));
							}
						}
					};
				}])
		.directive("mgBackgroundImage", function () {
				return {
		restrict: "A",
					scope: {
						backgroundImage: "@mgBackgroundImage"
					},
					link: function (scope, element, attrs) {
				scope.$watch("backgroundImage", function (url) {
							if (!url)
								return;
							element.css({
						"background-image": "url(" + url + ")",
								"background-size": "cover"
							});
						});
					}
				};
			})
		.directive("mgAjaxForm", function ($http) {	// required elem attrs: method="post" action="some url"
				return {
		restrict: "A",
					scope: {
				fileChangeSubmit: "=",
						method: "@",
						action: "@",
						callback: "&"
					},
					link: function (scope, elem, attrs) {
							if ($(elem).find("input[type=\"file\"]").length > 0) {
				elem.attr("enctype", "multipart/form-data");
						}

						var uploading = false;
						elem.on("submit", function (e) {
							if (uploading)
								return;
							uploading = true;
							if (window.FormData) {	// HTML5
								e.preventDefault();
								$http[scope.method.toLowerCase()](scope.action, new FormData(this), {
									transformRequest: angular.identity,
								headers: {"Content-Type": undefined}
								}).success(function (r) {
									uploading = false;
									scope.callback() && scope.callback()(r);
								}).error(function (e) {
									uploading = false;
									scope.callback() && scope.callback()(e);
								});
							} else {				// iFrame
						$(this).attr("target", createIframe(scope.callback()));
							}
								scope.fileChangeSubmit && elem.find("input[type=\"file\"]").val("");
						});

						scope.fileChangeSubmit && elem.find("input[type=\"file\"]").on("change", function () {
							elem.submit();
						});


						function createIframe(callback) {
						var iframeId = "ajax_form_" + new Date().getTime();
							var iframe = $("<iframe src=\"javascript:false; \" name=\"" + iframeId + "\" />");
							iframe.hide();
								iframe.appendTo("body");
							iframe.load(function (e) {
								uploading = false;
								var doc = getDoc(iframe[0]);
								var docRoot = doc.body ? doc.body : doc.documentElement;
								var data = docRoot.innerHTML;
										var el = $("<div></div>");
								el.html(data);
								try {
										callback && callback(JSON.parse($("pre", el).html()));
								} catch (ex) {
								callback && callback({error: $("pre", el).html()});
								}
							});
							return iframeId;
						}
						function getDoc(frame) {
							var doc = null;
							try {
								frame.contentWindow && (doc = frame.contentWindow.document);
								if (doc) {
									return doc;
								}
							} catch (err) {
							}

							try {
								return  frame.contentDocument ? frame.contentDocument : frame.document;
							} catch (err) {
								return  frame.document;
							}
						}
					}
				};
			})
		.directive("mgStyler", function () {	// used for checkboxes and selects in jquery-form-styler (http://dimox.name/jquery-form-styler/)
				return {
		restrict: "A",
				require: "^ngModel",
					scope: {},
					link: function (scope, elem, attrs, ngModel) {
				if (elem.attr("type") == "checkbox") {
							elem.change(function (val) {
								ngModel.$setViewValue(elem[0].checked);
							});
						}

						ngModel.$formatters.unshift(function (val) {
							setTimeout(function () {
				elem.trigger("refresh");
							});
							return val;
						});
					}
				};
			})
		.service("thirdPartyCookieEnabled", function ($rootScope, utils, $cookies) { // check third party cookie enabled
		var thirdPartyCookieEnabled = $cookies.get("third_party_cookie_fixed");
				!thirdPartyCookieEnabled && utils.loadScript("api/auth/third_party_cookie_fix_step1");
				window.thirdPartyCookieStep1 = function () {
				utils.loadScript("api/auth/third_party_cookie_fix_step2");
				};
				window.thirdPartyCookieStep2 = function (result) {
					thirdPartyCookieEnabled = result;
				};

				return function (callback) {
					var unwatch = $rootScope.$watch(function () {
						return thirdPartyCookieEnabled;
					}, function (thirdPartyCookieEnabled) {
						if (thirdPartyCookieEnabled === _.noop())
							return;
						unwatch();
						callback(thirdPartyCookieEnabled);
					});
				};
			});

})();