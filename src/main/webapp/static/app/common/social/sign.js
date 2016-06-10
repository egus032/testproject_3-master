/* global angular, user, VK, FB, _ */

(function () {
	"use strict";

	angular.module("sign", ["openapi", "oauth", "ngCookies"])
			.provider("signSettings", function () {
				var settings = {
					autologin: true
				};
				this.$get = function () {
					return settings;
				};
			})
			.run(function ($injector, thirdPartyCookieEnabled, settings) {
				if (!user && isIframe) {
					settings.THIRD_PARTY_COOKIE_FIX && thirdPartyCookieEnabled(function (enabled) {
						if (!enabled) {
							window.top.location = window.location.protocol + settings.HOST + "api/auth/third_party_cookie_fix.html?redirect=" + encodeURIComponent(document.referrer);
						}
					});

					document.referrer.contains("://vk.com/") && $injector.get("signVKIframe")();

					document.referrer.contains("://ok.ru/") && $injector.get("signOKIframe")();

					document.referrer.startsWith("https://apps.facebook.com") && $injector.get("signFB")();
				}
			})
			.factory("signVKIframe", function ($rootScope, vkapi, AuthApi, utils, signSettings) {
				return function () {
					vkapi.call("users.get", {fields: "sex,bdate,photo_100"}, function (r) {
						AuthApi.openapiSignIn({net: "vk"}, {
							session: utils.serializeUrlParams(),
							user_info: r.response[0]
						}, function (r) {
							$rootScope.user = r.user;
							$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
						});
					});
				};
			})
			.factory("signOKIframe", function ($rootScope, okapi, AuthApi, utils, signSettings) {
				return function () {
					okapi.call("users.getCurrentUser", {fields: "first_name,last_name,birthday,gender,pic128x128"}, function (r) {
						AuthApi.openapiSignIn({net: "ok"}, {
							user_info: r,
							session_key: utils.serializeUrlParams().session_key,
							auth_sig: utils.serializeUrlParams().auth_sig
						}, function (r) {
							$rootScope.user = r.user;
							$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
						});
					});
				};
			})
			.factory("signVK", function ($rootScope, vkapi, AuthApi, signSettings) {
				return function () {
					vkapi.call("users.get", {fields: "sex,bdate,photo_100"}, function (r) {
						AuthApi.openapiSignIn({net: "vk"},
								{
									session: VK.Auth.getSession(),
									user_info: r.response[0],
									autologin: signSettings.autologin
								}, function (r) {
							$rootScope.user = r.user;
							$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
						});
					});
				};
			})
			.directive("signVkBtn", function (signVK) {
				return {
					restrict: "A",
					scope: {},
					link: function (scope, elem) {
						elem.on("click", function () {
							signVK();
						});
					}
				};
			})
			.factory("signFB", function ($rootScope, fbapi, AuthApi, signSettings) {
				return function () {
					fbapi.call("/me", {}, function (r) {
						AuthApi.openapiSignIn({net: "fb"},
								{
									access_token: FB.getAuthResponse().accessToken,
									autologin: signSettings.autologin
								}, function (r) {
							$rootScope.user = r.user;
							$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
						});
					});
				};
			})
			.directive("signFbBtn", function (signFB) {
				return {
					restrict: "A",
					scope: {},
					link: function (scope, elem) {
						elem.on("click", function () {
							signFB();
						});
					}
				};
			})
			.factory("signOut", function ($rootScope, AuthApi) {
				return function () {
					$rootScope.$evalAsync(function () {
						AuthApi.signOut({});
						var tmpUser = $rootScope.user;
						$rootScope.user = _.noop();
						$rootScope.$broadcast(NG_EVENTS.SIGN.OUT, tmpUser);
					});
				};
			})
			.directive("signOutBtn", function (signOut) {
				return {
					restrict: "A",
					scope: {},
					link: function (scope, elem) {
						elem.on("click", function () {
							signOut();
						});
					}
				};
			})
			.directive("signVkOauth2Btn", function ($rootScope, AuthApi, utils, vkoauth2, signSettings) {
				return {
					restrict: "A",
					scope: {},
					link: function (scope, elem) {
						elem.on("click", function () {
							vkoauth2.loginDialog(function () {
								AuthApi.oauthSignIn({net: "vk"}, {}, function (r) {
									$rootScope.user = r.user;
									$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
								});
							});
						});
					}
				};
			})
			.directive("signFbOauth2Btn", function ($rootScope, AuthApi, utils, fboauth2, signSettings) {
				return {
					restrict: "A",
					scope: {},
					link: function (scope, elem) {
						elem.on("click", function () {
							fboauth2.loginDialog(function () {
								AuthApi.oauthSignIn({net: "fb"}, {}, function (r) {
									$rootScope.user = r.user;
									$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
								});
							});
						});
					}
				};
			})
			.directive("signOkOauth2Btn", function ($rootScope, AuthApi, utils, okoauth2, signSettings) {
				return {
					restrict: "A",
					scope: {},
					link: function (scope, elem) {
						elem.on("click", function () {
							okoauth2.loginDialog(function () {
								AuthApi.oauthSignIn({net: "ok"}, {}, function (r) {
									$rootScope.user = r.user;
									$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
								});
							});
						});
					}
				};
			})
			.controller("SignUpCtrl", function ($scope, AuthApi, $rootScope) {
				$scope.master = {};
				$scope.submit = function () {
					if ($scope.form.$invalid)
						return;
					AuthApi.signUp({
                                                lname: $scope.master.lname,
                                                fname: $scope.master.fname,
                                                sname: $scope.master.sname,
						email: $scope.master.email,
						phone: $scope.master.phone,
						repeatPhone: $scope.master.repeatPhone,
						password: md5($scope.master.password),
						autologin: $scope.master.autologin,
                                                bdate: $scope.master.bdate,
                                                region: '1',
                                                city: $scope.master.city
					}, function (r) {
						$rootScope.user = r.user;
						$rootScope.$broadcast(NG_EVENTS.SIGN.UP, $rootScope.user);
						$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
						$scope.master = {};
						$scope.form.$setPristine();
					}, function (e) {
						switch (e.data.error.code) {
							case 9:
								$scope.form.email.$setValidity("already_exists", false);
								$scope.form.email.$parsers.push(function (value) {
									$scope.form.email.$setValidity("already_exists", true);
									return value;
								});
								break;
						}
					});
				};
			})
			.controller("SignInCtrl", function ($scope, AuthApi, $rootScope, mgPopup) {
				$scope.master = {};
				$scope.submit = function () {
					if ($scope.form.$invalid)
						return;
					AuthApi.signIn({
						email: $scope.master.email,
						password: md5($scope.master.password),
						autologin: $scope.master.autologin,
					}, function (r) {
						$rootScope.user = r.user;
						$rootScope.$broadcast(NG_EVENTS.SIGN.IN, $rootScope.user);
						$scope.master = {};
						$scope.form.$setPristine();
					}, function (e) {
						switch (e.data.error.code) {
							case 1:
								mgPopup("Неверная комбинация e-mail и пароля");
								break;
						}
					});
				};
			})
			.service("AuthApi", function ($resource) {
				return $resource("/api/auth/", null, {
					signIn: {method: "PUT", url: "/api/auth/sign_in"},
					signUp: {method: "POST", url: "/api/auth/sign_up"},
					signOut: {method: "DELETE", url: "/api/auth/sign_out"},
					getUser: {method: "GET", url: "/api/auth/get_user"},
					openapiSignIn: {method: "PUT", url: "/api/auth/openapi/sign_in/:net"},
					oauthSignIn: {method: "PUT", url: "/api/auth/oauth2/sign_in/:net"}
				});
			});

})();