(function () {
	"use strict";

	angular.module("removeit.imgCropper", [])
			.controller("removeit.imgCropper.Ctrl", function ($scope) {
				$(function () {//for img crop

					'use strict';

					
					var $image = $('#image');
					var options = {
						aspectRatio: 16 / 9,
						preview: '.img-preview',
						checkCrossOrigin: false, //attention
						viewMode: 2,
						dragMode: 'move',
						responsive: true,
						restore: true,
						modal: true,
						guides: true,
						center: true,
						highlight: true,
						background: true,
						autoCrop: true,
						autoCropArea: .5,
						movable: true,
						rotatable: true
					};

					// Cropper
					$image.on().cropper(options);


					// Methods for buttons work
					$('.docs-buttons').on('click', '[data-method]', function () {
						var $this = $(this);
						var data = $this.data();
						var $target;
						var result;

						if ($this.prop('disabled') || $this.hasClass('disabled')) {
							return;
						}

						if ($image.data('cropper') && data.method) {
							data = $.extend({}, data); // Clone a new one

							if (typeof data.target !== 'undefined') {
								$target = $(data.target);

								if (typeof data.option === 'undefined') {
									try {
										data.option = JSON.parse($target.val());
									} catch (e) {
										console.log(e.message);
									}
								}
							}
							result = $image.cropper(data.method, data.option, data.secondOption);

							switch (data.method) {
								case 'scaleX':
								case 'scaleY':
									$(this).data('option', -data.option);
									break;

								case 'getCroppedCanvas':
									if (result) {

										// Bootstrap's Modal
										$('#getCroppedCanvasModal').modal().find('.modal-body').html(result);

//										if (!$download.hasClass('disabled')) {
//											$download.attr('href', result.toDataURL());
//										}
									}

									break;
							}

							if ($.isPlainObject(result) && $target) {
								try {
									$target.val(JSON.stringify(result));
								} catch (e) {
									console.log(e.message);
								}
							}

						}
					});


					// Methods for import image
					var $inputImage = $('#inputImage');
					var URL = window.URL || window.webkitURL;
					var blobURL;
					if (URL) {
						$inputImage.change(function () {
							var files = this.files;
							var file;

							if (!$image.data('cropper')) {
								return;
							}

							if (files && files.length) {
								file = files[0];

								if (/^image\/\w+$/.test(file.type)) {
									blobURL = URL.createObjectURL(file);
									$image.one('built.cropper', function () {

										// Revoke when load complete
										URL.revokeObjectURL(blobURL);
									}).cropper('reset').cropper('replace', blobURL);
									$inputImage.val('');
								} else {
									window.alert('Please choose an image file.');
								}
							}
						});
					} else {
						$inputImage.prop('disabled', true).parent().addClass('disabled');
					}

				});
			});
})();