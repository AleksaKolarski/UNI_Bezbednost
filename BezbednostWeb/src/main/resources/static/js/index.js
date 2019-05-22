
var button_download_certificate;
var button_browse_images;
var button_upload_images;

$(document).ready(function(e){

  button_download_certificate = $('#id-button-certificate');
  button_browse_images = $('#id-button-browse-images');
  button_upload_images = $('#id-button-upload-images');

  button_download_certificate.on('click', certificate_download);
  button_upload_images.on('click', upload_images);
});

function upload_images(){
  
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