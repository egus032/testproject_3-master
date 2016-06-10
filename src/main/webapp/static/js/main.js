function closePopup(elem) {
	$("#" + elem).removeClass("show");
}
;
function showPopup(elem) {
	$("#" + elem).addClass("show");
}
;


var eventOn = function (elem, event, func) {
	$(elem).off(event);
	$(elem).on(event, func);
};

var count = 0;
eventOn("html.index .man .helper", "click", function () {
	count++;
	if (count >= 10) {
		$("html").addClass("egs");
	}
});

eventOn(".faq .wrapper .content .list .head", "click", function () {
	$(this).toggleClass("show");
	$(this).parent().find(".text").slideToggle();
});

