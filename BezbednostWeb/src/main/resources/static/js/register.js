
var input_email;
var input_password1;
var input_password2;
var button_register;

$(document).ready(function(e){
  
  input_email = $('#id-input-email');
  input_password1 = $('#id-input-password1');
  input_password2 = $('#id-input-password2');
  button_register = $('#id-button-register');

  add_validation_email(input_email, 30);
  add_validation_password_match(input_password1, input_password2);

  button_register.on('click', function(e){
    if(check_email(input_email, 30)){
      if(check_password_match(input_password1, input_password2)){

        var email = input_email.val();
        var password = input.input_password1.val();

        customAjax({
          method: 'POST',
          url: 'user/register',
          data: { 'email': email, 'password': password },
          success: function(data, status, xhr){
            
          },
          error: function(xhr, status, error){
            
          }
        });
      }
    }
  });
});