(function () {
	"use strict";

	angular.module("removeit.authorization", ["removeit.authorization.javascript", "removeit.authorization.oauth", 
            "removeit.authorization.email", 
            "removeit.authorization.reg", "removeit.authorization.login"]);

})();