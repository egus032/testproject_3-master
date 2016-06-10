/* global angular, VK, FB, FAPI */

(function () {
	"use strict";

	angular.module("openapi", [])
			.provider("openapiSettings", function () {
				var openapiSettings;
				this.setOpenapiSettings = function (settings) {
					openapiSettings = settings;
				};

				this.$get = ["settings", function (settings) {
						if (!openapiSettings)
							openapiSettings = settings;
						return openapiSettings;
					}];
			})
			.factory("apiFactory", function ($rootScope, utils, $log) {
				return function () {
					var self = this;
					this.init = false;
					this.logged = false;
					this.setLogged = function (logged) {
						$rootScope.$evalAsync(function () {
							self.logged = logged;
							self.init = true;
						});
					};
					this.loginDialog = function () {
						$log.error("LoginDialog not implemented yet");
					};
					this.exec = function (callback) {
						self.init && !self.logged && (self.loginDialog());
						$rootScope.$evalAsync(function () {
							utils.watchOnce($rootScope, function () {
								return self.init;
							}, function () {
								utils.watchOnce($rootScope, function () {
									return self.logged;
								}, function () {
									callback && callback();
								});
							});
						});
					};
				};
			})
			.service("vkapi", function (apiFactory, openapiSettings, $rootScope, utils) {
				var api = new apiFactory();

				if (isIframe) {
					vkIframeTransport(utils, openapiSettings, function (logged) {
						api.setLogged(logged);
					});
				}
				else {
					vkTransport(openapiSettings, function (logged) {
						api.setLogged(logged);
					});
				}

				api.call = function (method, params, callback) {
					api.exec(function () {
						if (isIframe) {
							VK.api(method, params, function (r) {
								callback && $rootScope.$evalAsync(function () {
									callback(r);
								});
							});
						}
						else {
							VK.Api.call(method, params, function (r) {
								callback && $rootScope.$evalAsync(function () {
									callback(r);
								});
							});
						}
					});
				};
				api.loginDialog = function () {
					var permissionsArray = [];
					var permissionsMask = 0;
					for (var i = 0; i < permissionsArray.length; i++) {
						permissionsMask = permissionsMask | VK.access[permissionsArray[i].toUpperCase()];
					}
					VK.Auth.login(function (response) {
					}, permissionsMask);
				};

				return api;
			})
			.service("fbapi", function (apiFactory, openapiSettings, $rootScope, utils) {
				var api = new apiFactory();

				fbTransport(openapiSettings, function (logged) {
					api.setLogged(logged);
				});

				api.call = function (method, params, callback) {
					api.exec(function () {
						FB.api(method, params, function (r) {
							callback && $rootScope.$evalAsync(function () {
								callback(r);
							});
						});
					});
				};
				api.loginDialog = function () {
					if (navigator.userAgent.match("CriOS")) {
						utils.openDialog("https://www.facebook.com/dialog/oauth?client_id={0}&response_type=granted_scopes&scope={1}&redirect_uri={2}&state={3}".format(
								openapiSettings.FB_APP_ID,
								openapiSettings.FB_APP_PERMISSIONS,
								encodeURIComponent("http:" + openapiSettings.HOST + "api/auth/oauth2.html"),
								"fb_fix_openapi"
								), function () {
							FB.getLoginStatus(function (response) {
								api.setLogged(response.status === "connected");
							}, true);
						});

					} else {
						FB.login(function (response) {
						}, {scope: openapiSettings.FB_APP_PERMISSIONS});
					}
				};


				return api;
			})
			.service("okapi", function (apiFactory, openapiSettings, $rootScope, utils) {
				var api = new apiFactory();

				okIframeTransport(utils, openapiSettings, function (logged) {
					api.setLogged(logged);
				});

				api.call = function (method, params, callback) {
					api.exec(function () {
						var paramsCloned = angular.copy(params);
						paramsCloned.method = method;
						FAPI.Client.call(paramsCloned, function (method, result, data) {
							callback && $rootScope.$evalAsync(function () {
								callback(result);
							});
						});
					});
				};


				return api;
			});




	function vkTransport(openapiSettings, callback) {
		window.vkAsyncInit = function () {
			VK.init({
				apiId: openapiSettings.VK_APP_WEBSITE_ID
			});

			VK.Auth.getLoginStatus(function (response) {
				callback && callback(!!response.session);
			});
			VK.Observer.subscribe("auth.login", function () {
				callback && callback(true);
			});
			VK.Observer.subscribe("auth.logout", function () {
				callback && callback(false);
			});
		};

		setTimeout(function () {
			$("body").append("<div id=\"vk_api_transport\"></div>");
			var el = document.createElement("script");
			el.type = "text/javascript";
			el.src = "//vk.com/js/api/openapi.js";
			el.async = true;
			document.getElementById("vk_api_transport").appendChild(el);
		}, 0);
	}

	function vkIframeTransport(utils, openapiSettings, callback) {
		utils.loadScript("//vk.com/js/api/xd_connection.js?2", function () {
			if (!VK.External)
				return;
			VK.init(function (r) {
				VK.callMethod("resizeWindow", 1000, 2000);
				callback && callback(true);
			}, function () {
			}, openapiSettings.VK_APP_API_VERISON);
		});
	}

	function fbTransport(openapiSettings, callback) {
		window.fbAsyncInit = function () {
			FB.init({
				appId: openapiSettings.FB_APP_ID,
				xfbml: true,
				status: true,
				version: openapiSettings.FB_API_VERSION
			});

			FB.getLoginStatus(function (response) {
				callback && callback(response.status === "connected");
			});
			FB.Event.subscribe("auth.login", function () {
				callback && callback(true);
			});
			FB.Event.subscribe("auth.logout", function () {
				callback && callback(false);
			});

			FB.Canvas.setAutoGrow();
		};

		(function (d, s, id) {
			$("body").append("<div id=\"fb-root\"></div>");
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id)) {
				return;
			}
			js = d.createElement(s);
			js.id = id;
			js.src = "//connect.facebook.net/en_US/sdk.js";
			fjs.parentNode.insertBefore(js, fjs);
		}(document, "script", "facebook-jssdk"));
	}

	function okIframeTransport(utils, openapiSettings, callback) {
		utils.loadScript("//api.odnoklassniki.ru/js/fapi5.js", function () {
			var rParams = FAPI.Util.getRequestParameters();
			rParams.api_server && rParams.apiconnection && FAPI.init(rParams.api_server, rParams.apiconnection,
					function (r) {
						callback && callback(true);
					}
			);
		});
	}
})();
