/* global angular */

(function () {
	"use strict";

	angular.module("mgPopup", [])
			.directive("mgPopup", function ($rootScope, $timeout, $window, $q) {
				$rootScope.showPopup = $window.showPopup = function (id) {
					return $q(function (resolve, reject) {
						var $elem = _.isString(id) ? $("#" + id) : $(id),
								$directive = $elem.parents(".wrapper-outer").parent(),
								$wrapperOuter = $directive.children(".wrapper-outer"),
								$wrapperInner = $wrapperOuter.children(".wrapper-inner"),
								$overlap = $directive.children(".overlap");
						$elem.addClass("show");
						$wrapperInner.scrollTop(0);
						$overlap.addClass("show");
						$("html").css("overflow-y", "hidden");
						$wrapperOuter.addClass("show");
						$timeout(function () {
							$overlap.addClass("anim");
							$wrapperInner.off("click");
							$elem.on("click", function (e) {
								e.stopPropagation();
								e.preventDefault();
							});
							$wrapperInner.on("click", function (e) {
								e.stopPropagation();
								e.preventDefault();
								$window.closePopup(id);
							});
							$($window).on("keydown", function (e) {
								if ((e.keyCode ? e.keyCode : e.which) === 27)
									$window.closePopup(id);
							});
							$elem.addClass("anim-in");
							resolve();
							$rootScope.$broadcast(NG_EVENTS.POPUP.OPENED, $elem.attr("id"));
						}, 100);
						$wrapperInner.focus();
					});
				};

				$rootScope.closePopup = $window.closePopup = function (id) {
					return $q(function (resolve) {
						var $elem = _.isString(id) ? $("#" + id) : $(id),
								$directive = $elem.parents(".wrapper-outer").parent(),
								$wrapperOuter = $directive.children(".wrapper-outer"),
								$overlap = $directive.children(".overlap");
						$elem.addClass("anim-out");
						$overlap.removeClass("anim");
						$timeout(function () {
							$elem.removeClass("show anim-in anim-out");
							$("html").css("overflow-y", "auto");
							$wrapperOuter.removeClass("show");
							$overlap.removeClass("show");
							resolve();
							$rootScope.$broadcast(NG_EVENTS.POPUP.CLOSED, $elem.attr("id"));
						}, 500);
					});
				};

				return {
					restrict: "A",
					templateUrl: "static/app/common/mg-popup/mg-popup.html",
					transclude: true,
					scope: {
						id: "@mgPopup",
						show: "=show",
						youtubeStopOnClose: "=youtubeStopOnClose", // add &enablejsapi=1 to video URL,
						closeOnTop: "=closeOnTop"
					},
					link: function (scope, elem) {
						scope.close = function () {
							if (scope.youtubeStopOnClose) {
								elem.find("iframe").each(function (i) {
									this.contentWindow.postMessage("{\"event\":\"command\",\"func\":\"stopVideo\",\"args\":\"\"}", "*");
								});
							}
							$window.closePopup(scope.id).then(function () {
								if (scope.id.startsWith("mgPopupTexting"))
									elem.remove();
							});
						};
						if (scope.show) {
							setTimeout(function () {
								showPopup(scope.id);
							});
						}
					}
				};
			})
			.service("mgPopup", function ($rootScope, $compile) {
				return function (text) {
					var date = +new Date();
					var templateElement = angular.element(
							["<div data-mg-popup=\"mgPopupTexting" + date + "\" data-show=\"true\">",
								"<div data-ng-bind-html=\"text\"></div>",
								"</div>"]
							.join(""));
					var cloned = $compile(templateElement)($rootScope.$new(true), function (clonedElement, scope) {
						scope.text = text;
						$("body").append(clonedElement);
					});
					return function () {
						cloned.remove();
					};
				};
			});

})();

