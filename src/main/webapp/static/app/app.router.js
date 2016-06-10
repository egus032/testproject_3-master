/* global angular */

(function () {
	"use strict";

	angular.module("app.router", ["ui.router"])
			.config(function ($stateProvider, $urlRouterProvider, $locationProvider, $urlMatcherFactoryProvider) {
				$stateProvider.getResolveRequst = function (page) {
					return ["$http", function ($http) {
							return $http.get("page_data/{0}".format(page)).then(function (data) {
								return data.data;
							});
						}];
				};
				var aHost = document.createElement("a");
				aHost.href = settings.HOST;
//				if (aHost.hostname !== window.location.hostname) {
//					$("base").html("<base href=\"" + window.location + "\" />");
//				} else {
//					$("base").html("<base href=\"" + settings.HOST + "\" />");
//				}



				$stateProvider.state("index", {
					url: "/",
					templateUrl: "static/app/index/index.html"
				});

				$stateProvider.state("404", {
					url: "/404",
					templateUrl: "static/app/404/404.html"
				});

				$stateProvider.state("crawlertest", {
					url: "/crawlertest",
					templateUrl: "static/app/crawlertest/crawlertest.html"
				});
				

				$urlRouterProvider.when("", "/");
				$urlRouterProvider.otherwise("/404");

				$locationProvider.html5Mode({
					enabled: true,
					requireBase: false
				});
				$urlRouterProvider.rule(function ($injector, $location) {
					var path = $location.path(),
							normalized = path.toLowerCase();

					if (path !== normalized) {
						return normalized;
					}
				});
				$urlMatcherFactoryProvider.strictMode(false);
			})
			.run(function ($rootScope) {
//				$rootScope.$on("$stateChangeStart", function (event, toState, toParams, fromState, fromParams) {
//					debugger;
//				});

				$rootScope.$on("$stateChangeSuccess", function (event, toState, toParams, fromState, fromParams) {
					$rootScope.state = toState;
				});
			});

})();