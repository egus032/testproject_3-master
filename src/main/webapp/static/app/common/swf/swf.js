/* global angular, swfobject, buildNumber */

(function () {
	"use strict";

	angular.module("swf", [])
			.service("embedSwf", function () {
				return function (elementId, swfPath, width, height, flashvars, params, attributes) {
					var el = document.getElementById(elementId);

					!flashvars && (flashvars = {
					});

					!params && (params = {
						menu: "false",
						scale: "noScale",
						allowFullscreen: "true",
						allowScriptAccess: "always",
						bgcolor: "",
						wmode: "opaque" // can cause issues with FP settings & webcam
					});

					!attributes && (attributes = {});
					swfobject.embedSWF(swfPath + "?" + buildNumber, el, width, height, "10.0.0", "static/expressInstall.swf", flashvars, params, attributes);
				};
			})
			.service("getSwfObject", function () {
				return function (elementId) {
					var M$ = navigator.appName.indexOf("Microsoft") != -1;
					return (M$ ? window : document)[elementId];
				};
			});

});