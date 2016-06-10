(function () {
	"use strict";

	angular.module("removeit.share", [])
			.controller("removeit.share.Ctrl", function ($scope) {
				$scope.master = {
					url: "http://google.com/",
					title: "Это тестовый заголовок",
					description: "Это тестовое описание",
					image: "http://placehold.it/150x150"
				};
			});

})();