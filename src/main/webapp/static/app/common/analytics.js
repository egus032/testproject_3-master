/* global angular, _ */

(function () {
	"use strict";

	var firstView = false,
			firstState;

	angular.module("analytics", [])
			.provider("analytics", function () {
				var useYandexMetrika = false;
				var yaCounterId;
				this.setYandexMetrikaId = function (id) {
					useYandexMetrika = true;
					yaCounterId = id;
				};
				this.useYandexMetrika = function (value) {
					if (value && !yaCounterId)
						return;
					useYandexMetrika = !!value;
				};

				var useGoogleAnalytics = false;
				this.useGoogleAnalytics = function (value) {
					useGoogleAnalytics = !!value;
				};

				var useGoogleTagManager = false;
				this.useGoogleTagManager = function (value) {
					useGoogleTagManager = !!value;
				};

				this.$get = ["$log", "$window", "$location", function ($log, $window, $location) {
						return {
							event: function (category, event) {
								if (useGoogleAnalytics) {
									try {
										$window.ga("send", "event", category, event);
										$log.debug("Track GA event: Category: {0}. Event: {1}".format(category, event));
									} catch (e) {
										$log.error("Cannot track GA event");
									}
								}
								if (useYandexMetrika) {
									try {
										$window["yaCounter" + yaCounterId].reachGoal(category, {event: event});
										$log.debug("Track Yandex.Metrika event: Category: {0}. Event: {1}".format(category, event));
									} catch (e) {
										$log.error("Cannot track Yandex.Metrika event");
									}
								}
								if (useGoogleTagManager) {
									try {
										$window.dataLayer.push({"title": category, "event": event});
										$log.debug("Track GTM event: Title: {0}. Event: {1}".format(category, event));
									} catch (e) {
										$log.error("Cannot track GTM event");
									}
								}
							},
							pageView: function (toState, fromState) {
								if (useGoogleAnalytics) {
									$window.ga("send", "pageview", {page: toState.url});
								}
								if (useYandexMetrika) {
									$window["yaCounter" + yaCounterId].hit(toState.url, toState.name, fromState.name ? fromState.url : _.noop());
								}
								if (useGoogleTagManager) {
									$window.dataLayer.push({event: "virtualPageView", virtualUrl: toState.url});
								}
							}
						};
					}];
			})
			.run(function ($rootScope, analytics) {
				$rootScope.$on("$stateChangeStart", function (event, toState, toParams, fromState, fromParams) {
					if (!fromState.name) {
						firstState = toState;
					}
				});
				$rootScope.$on("$stateChangeSuccess", function (event, toState, toParams, fromState, fromParams) {
					if (firstState == toState && !firstView) {
						firstView = true;
						return;
					}

					analytics.pageView(toState, fromState);
				});
			});

})();

