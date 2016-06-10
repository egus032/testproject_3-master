/* global angular */

(function () {
	"use strict";

	angular.module("share", ["app.utils"])
			.factory("mgShareDialogFactory", function ($q, utils) {
				return function (url) {
					return $q(function (resolve, reject) {
						utils.openDialog(url, function () {
							resolve();
						});
					});
				};
			})
			.service("mgFacebookShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.facebook(params));
				};
			})
			.directive("mgFacebookShare", function (mgFacebookShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgFacebookShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgFacebookShare(scope.params);
						});
					}
				};
			})
			.service("mgVkShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.vk(params));
				};
			})
			.directive("mgVkShare", function (mgVkShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgVkShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgVkShare(scope.params);
						});
					}
				};
			})
			.service("mgOkShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.ok(params));
				};
			})
			.directive("mgOkShare", function (mgOkShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgOkShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgOkShare(scope.params);
						});
					}
				};
			})
			.service("mgTwitterShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.twitter(params));
				};
			})
			.directive("mgTwitterShare", function (mgTwitterShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgTwitterShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgTwitterShare(scope.params);
						});
					}
				};
			})
			.service("mgMailRuShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.mailRu(params));
				};
			})
			.directive("mgMailRuShare", function (mgMailRuShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgMailRuShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgMailRuShare(scope.params);
						});
					}
				};
			})
			.service("mgGooglePlusShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.googlePlus(params));
				};
			})
			.directive("mgGooglePlusShare", function (mgGooglePlusShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgGooglePlusShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgGooglePlusShare(scope.params);
						});
					}
				};
			})
			.service("mgLinkedInShare", function (mgShareDialogFactory) {
				return function (params) {
					return mgShareDialogFactory(ShareUrl.linkedIn(params));
				};
			})
			.directive("mgLinkedInShare", function (mgLinkedInShare) {
				return {
					restrict: "A",
					scope: {
						params: "=mgLinkedInShare"
					},
					link: function (scope, elem) {
						elem.on("click", function () {
							mgLinkedInShare(scope.params);
						});
					}
				};
			});

	var ShareUrl = {
		vk: function (params) {
			return ShareUrl._concat("https://vkontakte.ru/share.php", {
				url: params.url,
				title: params.title,
				description: params.description,
				image: params.image,
				noparse: "true"
			});
		},
		ok: function (params) {
			return ShareUrl._concat("https://connect.ok.ru/dk", {
				"st.shareUrl": params.url,
				"st.cmd": "WidgetSharePreview"
			});
		},
		facebook: function (params) {
			return ShareUrl._concat("https://www.facebook.com/sharer/sharer.php", {
				"u": params.url
			});
		},
		twitter: function (params) {
			return ShareUrl._concat("https://twitter.com/intent/tweet", {
				text: params.description,
				url: params.url,
				hashtags: params.hashtags, // comma separated
				via: params.via,
				related: params.related
			});
		},
		mailRu: function (params) {
			return ShareUrl._concat("https://connect.mail.ru/share", {
				url: params.url,
				title: params.title,
				description: params.description,
				imageurl: params.image
			});
		},
		googlePlus: function (params) {
			return ShareUrl._concat("https://plus.google.com/share", {
				url: params.url
			});
		},
		linkedIn: function (params) {
			return ShareUrl._concat("http://www.linkedin.com/shareArticle", {
				url: params.url,
				title: params.title,
				summary: params.description,
				mini: true
			});
		},
		_concat: function (url, params) {
			return url + "?" + $.param(params).replace(/&?[^&?]+=(?=(?:&|$))/g, "");
		}
	};

})();