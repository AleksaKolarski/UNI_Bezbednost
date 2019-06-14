
var button_download_certificate;
var button_browse_images;
var button_upload_images;
var div_main;

$(document).ready(function(e){

  button_download_certificate = $('#id-button-certificate');
  button_browse_images = $('#id-button-browse-images');
	button_upload_images = $('#id-button-upload-images');
	div_main = $('#id-div-main');

  button_download_certificate.on('click', certificate_download);
	button_upload_images.on('click', upload_images);
	
	fill_main_div();
});

function upload_images(){
	var file = button_browse_images.prop('files')[0];
	if(file == undefined){
		return;
	}
	var data = new FormData();
	data.append('file', file);
	customAjax({
		method: 'POST', 
		url: '/image/upload/', 
		data: data, 
		cache: false, 
		contentType: false, 
		processData: false,
		success: function(data, status, xhr){

		}
	});
}

function certificate_download(){
	var xhr = new XMLHttpRequest();
	xhr.open('GET', "/certificate/download", true);
	xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwt'));
	xhr.responseType = 'blob';
	xhr.onload = function(e) {
		if (this.status == 200) {
			var blob = this.response;
			var a = document.createElement('a');
			var url = window.URL.createObjectURL(blob);
			a.href = url;
			a.download = xhr.getResponseHeader('filename');
			a.click();
			window.URL.revokeObjectURL(url);
		}
	};
	xhr.send();
}

function fill_main_div(){
	customAjax({
		method: 'GET',
		url: '/user/currentUser',
		success: function(user, status, xhr){
			var images = user.imagePackages;
			images.forEach(image => {
				var html = '<div>'+ image +'</div>';
				div_main.append(html);
				console.log(html);
			});
    }
	});
}
