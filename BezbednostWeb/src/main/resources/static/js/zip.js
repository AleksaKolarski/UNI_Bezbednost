
var div_images;
var zip_id;

$(document).ready(function(e){

  div_images = $('#id-div-images');

  // get zip ID from url
  zip_id = new URLSearchParams(window.location.search).get('id');

  // fill div_images with images
  fill_div_images();
});

function fill_div_images(){
  // get images/show/{zip_id}

  customAjax({
    method: 'GET',
    url: '/images/show/' + zip_id,
    success: function(){
      
    }
  });
}