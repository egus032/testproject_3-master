(function () {
	"use strict";

	angular.module("removeit", [
		"removeit.authorization", "removeit.api", "removeit.share", "removeit.imgCropper"
	])
			.config(function ($stateProvider) {

				$stateProvider.state("removeitAuthorizationJavascript", {
					url: "/removeit-authorization-javascript",
					templateUrl: "static/app/removeit/authorization/javascript/javascript.html"
				});

				$stateProvider.state("removeitAuthorizationOauth", {
					url: "/rm/removeit-authorization-oauth",
					templateUrl: "static/app/removeit/authorization/oauth/oauth.html"
				});

				$stateProvider.state("removeitAuthorizationEmail", {
					url: "/removeit-authorization-email",
					templateUrl: "static/app/removeit/authorization/email/email.html"
				});
                                
                                $stateProvider.state("removeitAuthorizationLogin", {
					url: "/removeit-authorization-login",
					templateUrl: "static/app/removeit/authorization/login/login.html"
				});
                                
                                $stateProvider.state("removeitAuthorizationReg", {
					url: "/removeit-authorization-reg",
					templateUrl: "static/app/removeit/authorization/reg/reg.html"
				});

				$stateProvider.state("removeitApiVk", {
					url: "/removeit-api-vk",
					templateUrl: "static/app/removeit/api/vk/vk.html",
					controller: "removeit.api.vk.Ctrl"
				});

				$stateProvider.state("removeitApiFacebook", {
					url: "/removeit-api-facebook",
					templateUrl: "static/app/removeit/api/facebook/facebook.html",
					controller: "removeit.api.facebook.Ctrl"
				});

				$stateProvider.state("removeitApiOk", {
					url: "/removeit-api-ok",
					templateUrl: "static/app/removeit/api/ok/ok.html",
					controller: "removeit.api.ok.Ctrl"
				});

				$stateProvider.state("removeitShare", {
					url: "/removeit-share",
					templateUrl: "static/app/removeit/share/share.html",
					controller: "removeit.share.Ctrl"
				});
				
				$stateProvider.state("removeitImgCropper", {
					url: "/removeit-img-cropper",
					templateUrl: "static/app/removeit/img-cropper/img-cropper.html",
					
					controller: "removeit.imgCropper.Ctrl"
				});


			});

})();