
var button_download_certificate;
var button_browse_images;
var button_upload_images;
var div_main;

$(document).ready(function(e){

  button_download_certificate = $('#id-button-certificate');
  button_browse_images = $('#id-button-browse-images');
	button_upload_images = $('#id-button-upload-images');
	div_main = $('#id-div-main');

  button_download_certificate.on('click', function(){file_download('/certificate/download');});
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
			location.reload();
		}
	});
}

function fill_main_div(){
	customAjax({
		method: 'GET',
		url: '/image/allFromCurrentUser',
		success: function(zips, status, xhr){
			zips.forEach(zip => {
				var html = '<div class="class-zip-card-wrapper col-lg-2 col-md-4 col-sm-12">' + 
											'<div class="class-zip-card">' + 
												'<h4>'+ zip.id +'</h4>' + 
												'<div class="class-div-datetime">' + 
													'<p>'+ zip.date +'</p>' + 
													'<p>'+ zip.time +'</p>' + 
												'</div>' + 
												'<div class="class-div-links">' + 
													'<a id="id-download-zip-' + zip.id +'" href="#"><p>download</p></a>' + 
													//'<a href="/zip.html?id='+ zip.id +'"><p>show</p></a>' + 
												'</div>' + 
											'</div>' + 
      							'</div>';
				div_main.append(html);
				//na link za download nakaciti funkciju download
				$('#id-download-zip-' + zip.id).on('click', function(){file_download('/image/download/' + zip.id);});
			});
    }
	});
}
