
var input_email;
var input_password;
var button_login;

$(document).ready(function(e){
  
  input_email = $('#id-input-email');
  input_password = $('#id-input-password');
  button_login = $('#id-button-login');

  add_validation_email(input_email, 30);
  add_validation_text(input_password, 5, 999);

  button_login.on('click', function(e){
    if(check_email(input_email, 30)){
      if(check_text(input_password, 5, 999)){

        var email = input_email.val();
        var password = input_password.val();

        customAjax({
          method: 'POST',
          url: 'auth/login',
          data: { 'email': email, 'password': password },
          success: function(jwt, status, xhr){
            if(xhr.status == 200){
              localStorage.setItem('jwt', jwt);
              window.location.href = "/index.html";
            }
          },
          error: function(xhr, status, error){

          }
        });
      }
    }
  });
});