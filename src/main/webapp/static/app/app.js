/* global angular, settings, debugMode, user, top, self */

(function () {
	"use strict";

	angular.module("app", [
		"app.router", "app.rest", "app.utils", "analytics", "ngTouch",
		"index", "404", "removeit"	// Pages
	])
			.provider("settings", function () {
				var settings = window.settings;
				this.set = function (key, value) {
					settings[key] = value;
				};

				this.$get = function () {
					return settings;
				};
			})
			.config(function ($logProvider, analyticsProvider) {
//				analyticsProvider.setYandexMetrikaId(123);
//				analyticsProvider.useGoogleAnalytics(true);
//				analyticsProvider.useGoogleTagManager(true);

				$logProvider.debugEnabled(!!debugMode);
			})
			.run(function ($rootScope) {
				user && ($rootScope.user = user);

				$rootScope.isIframe = isIframe;

				$rootScope.patterns = {
					email: /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i,
					cyrillic: /^[а-я]+$/i
				};

				$rootScope.now = new Date();
			});

})();

(function () {

	window.isIframe = (function () {
		return self !== top;
	})();

	if (typeof String.prototype.format != "function") {
		String.prototype.format = function () {
			var args = arguments;

			return this.replace(/\{(\d+)\}/g, function () {
				return args[arguments[1]];
			});
		};
	}
	if (typeof String.prototype.startsWith != "function") {
		String.prototype.startsWith = function (str) {
			return this.indexOf(str) === 0;
		};
	}
	if (typeof String.prototype.endsWith != "function") {
		String.prototype.endsWith = function (suffix) {
			return this.indexOf(suffix, this.length - suffix.length) !== -1;
		};
	}
	if (typeof String.prototype.contains != "function") {
		String.prototype.contains = function (test) {
			return this.indexOf(test) == -1 ? false : true;
		};
	}
	if (typeof String.prototype.capitalizeFirstLetter != "function") {
		String.prototype.capitalizeFirstLetter = function () {
			return this.charAt(0).toUpperCase() + this.slice(1);
		};
	}
	if (typeof String.prototype.toDash != "function") {
		String.prototype.toDash = function () {
			return this.replace(/([A-Z])/g, function ($1) {
				return "-" + $1.toLowerCase();
			});
		};
	}
	if (!window.console) {
		window.console = {};
	}
	// union of Chrome, FF, IE, and Safari console methods
	var m = [
		"log", "info", "warn", "error", "debug", "trace", "dir", "group",
		"groupCollapsed", "groupEnd", "time", "timeEnd", "profile", "profileEnd",
		"dirxml", "assert", "count", "markTimeline", "timeStamp", "clear"
	];
	// define undefined methods as noops to prevent errors
	for (var i = 0; i < m.length; i++) {
		if (!window.console[m[i]]) {
			window.console[m[i]] = function () {
			};
		}
	}

	if (!window.location.origin) {
		window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port : "");
	}

})();
