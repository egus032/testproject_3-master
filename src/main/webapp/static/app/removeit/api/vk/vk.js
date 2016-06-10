(function () {
	"use strict";

	angular.module("removeit.api.vk", ["openapi", "oauth", "mgPopup"])
			.controller("removeit.api.vk.Ctrl", function ($scope, vkapi, vkoauth2) {
				$scope.master = {
					method: "users.get",
					params: JSON.stringify({fields: "sex, bdate, city, country, photo_50, photo_100, photo_200_orig, photo_200, photo_400_orig, photo_max, photo_max_orig, photo_id, online, online_mobile, domain, has_mobile, contacts, connections, site, education, universities, schools, can_post, can_see_all_posts, can_see_audio, can_write_private_message, status, last_seen, common_count, relation, relatives, counters, screen_name, maiden_name, timezone, occupation,activities, interests, music, movies, tv, books, games, about, quotes, personal, friends_status"}, null, 2)
				};

				$scope.js = function () {
					if ($scope.form.$invalid)
						return;
					vkapi.call($scope.master.method, JSON.parse($scope.master.params), function (r) {
						showDialog($scope.master.method, r);
					});
				};

				$scope.oauth2 = function () {
					if ($scope.form.$invalid)
						return;
					vkoauth2.call($scope.master.method, JSON.parse($scope.master.params), function (r) {
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