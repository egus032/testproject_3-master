/* global angular */

(function () {
	"use strict";

	angular.module("oauth", [])
			.factory("oauthFactory", function (OauthApi, $log) {
				return function (net) {
					var self = this;
					this.loginDialog = function () {
						$log.error("LoginDialog not implemented yet");
					};
					this.isAuthorized = false;
					OauthApi.isAuthorized({net: net}, function (r) {
						self.isAuthorized = r.is_authorized;
					});
					this.call = function (method, params, callback) {
						function doCall() {
							OauthApi.call({
								net: net,
								method: method
							}, params, function (r) {
								callback && callback(r);
							}, function (r) {
								switch (r.error.code) {
									case "8":
//                                            self.loginDialog();
										break;
								}
							});
						}
						if (self.isAuthorized) {
							doCall();
						} else {
							self.loginDialog(function () {
								OauthApi.isAuthorized({net: net}, function (r) {
									self.isAuthorized = r.is_authorized;
									doCall();
								});
							});
						}
					};
				};
			})
			.service("vkoauth2", function (oauthFactory, utils, settings) {
				var api = new oauthFactory("vk");
				api.loginDialog = function (closeCallback) {
					utils.openDialog("https://oauth.vk.com/authorize?client_id={0}&scope={1}&redirect_uri={2}&response_type=code&v={3}&state={4}".format(
							settings.VK_APP_WEBSITE_ID,
							settings.VK_APP_WEBSITE_PERMISSIONS,
							encodeURIComponent("http:" + settings.HOST + "api/auth/oauth2.html"),
							settings.VK_APP_API_VERISON,
							"vk"
							), closeCallback);
				};
				return api;
			})
			.service("fboauth2", function (oauthFactory, utils, settings) {
				var api = new oauthFactory("fb");
				api.loginDialog = function (closeCallback) {
					utils.openDialog("https://www.facebook.com/dialog/oauth?client_id={0}&response_type=code&scope={1}&redirect_uri={2}&state={3}".format(
							settings.FB_APP_ID,
							settings.FB_APP_PERMISSIONS,
							encodeURIComponent("http:" + settings.HOST + "api/auth/oauth2.html"),
							"fb"
							), closeCallback);
				};
				return api;
			})
			.service("okoauth2", function (oauthFactory, utils, settings) {
				var api = new oauthFactory("ok");
				api.loginDialog = function (closeCallback) {
					utils.openDialog("http://www.odnoklassniki.ru/oauth/authorize?client_id={0}&scope={1}&response_type=code&redirect_uri={2}&state={3}".format(
							settings.OK_APP_ID,
							settings.OK_APP_SCOPE,
							encodeURIComponent("http:" + settings.HOST + "api/auth/oauth2.html"),
							"ok"
							), closeCallback);
				};
				return api;
			})
			.service("OauthApi", function ($resource) {
				return $resource("/api/auth/", null, {
					call: {method: "POST", url: "/api/auth/oauth2/call/:net/:method"},
					isAuthorized: {method: "GET", url: "/api/auth/oauth2/is_authorized/:net"}
				});
			});

})();