(function () {
	"use strict";

	angular.module("removeit.api.ok", ["oauth", "mgPopup"])
			.controller("removeit.api.ok.Ctrl", function ($scope, okoauth2) {
				$scope.master = {
					method: "users.getCurrentUser",
					params: JSON.stringify({fields: "uid, locale, first_name, last_name, name, gender, age, birthday, has_email, current_status, current_status_id, current_status_date, online, photo_id, pic_1, pic_2, location"})
				};

				$scope.oauth2 = function () {
					if ($scope.form.$invalid)
						return;
					okoauth2.call($scope.master.method, JSON.parse($scope.master.params), function (r) {
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