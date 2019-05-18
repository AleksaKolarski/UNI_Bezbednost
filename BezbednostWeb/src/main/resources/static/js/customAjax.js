/* Custom jquery ajax call that forwards jwt token from localstorage:
**
** Author: Aleksa Kolarski (SF 27/2016)
** 2018
*/

function customAjax(params){
  var jwt = localStorage.getItem('jwt');
  $.ajax({
    async: params.async,
    cache: params.cache,
    complete: function(xhr, status){
      console.log(params.url + ': Server returned ' + xhr.status + '; status is ' + status);
      if(params.complete != null){
        params.complete(xhr, status);
      }
    },
    contentType: params.contentType,
    data: params.data,
    dataType: params.dataType,
    error: function(xhr, status, error){
      if(xhr.status == 401){
        localStorage.removeItem('jwt');
        window.location.href = 'login.html';
      }
      if(params.error != null){
        params.error(xhr, status, error);
      }
    },
    method: params.method,
    mimeType: params.mimeType,
    processData: params.processData,
    success: params.success,
    url: params.url,
    beforeSend: function (xhr) {
      if(jwt != null){
        xhr.setRequestHeader("Authorization", 'Bearer '+ jwt)
      }
      if(params.beforeSend != null){
        params.beforeSend(xhr);
      }
    }
  });
}