/* global angular, _ */

(function () {
	"use strict";

	angular.module("app.rest", ["ngResource"])
			.config(function ($httpProvider) {
				$httpProvider.interceptors.push(function () {
					return {
						request: function (config) {
							if ($("base").attr("href").endsWith("/") && config.url.startsWith("/")) {
								config.url = config.url.substring(1);
							}
							config.url = $("base").attr("href") + config.url;
							return config;
						}
					};
				});
			})
			.factory("RestApi", function ($resource) {
				return $resource("/api/", null, {
					ping: {method: "GET", url: "/api/ping"}
				});
			});

})();