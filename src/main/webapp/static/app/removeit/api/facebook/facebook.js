(function () {
	"use strict";

	angular.module("removeit.api.facebook", ["openapi", "oauth", "mgPopup"])
			.controller("removeit.api.facebook.Ctrl", function ($scope, fbapi, fboauth2) {
				$scope.master = {
					method: "me",
					params: JSON.stringify({fields: "id,first_name,last_name,birthday,gender,email"})
				};

				$scope.js = function () {
					if ($scope.form.$invalid)
						return;
					fbapi.call($scope.master.method, JSON.parse($scope.master.params), function (r) {
						showDialog($scope.master.method, r);
					});
				};

				$scope.oauth2 = function () {
					if ($scope.form.$invalid)
						return;
					fboauth2.call($scope.master.method, JSON.parse($scope.master.params), function (r) {
						showDialog($scope.master.method, r);
					});
				};

				function showDialog(title, body) {
					$scope.response = JSON.stringify(body, null, 2);
					showPopup("responsePopup");
					$scope.form.$setPristine();
				}
			});

})();