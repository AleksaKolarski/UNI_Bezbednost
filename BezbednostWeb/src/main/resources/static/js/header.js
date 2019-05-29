var header;

$(document).ready(function(e){
  header = $('#id-header');


  customAjax({
    method: 'GET',
    url: '/user/currentUser',
    success: function(user, status, xhr){
      if(user.admin == true){
        header.append('<button id="id-button-main">Main</button>');
        header.append('<button id="id-button-users">Users</button>');

        $('#id-button-main').on('click', function(e){
          window.location.href = '/index.html';
        });
        $('#id-button-users').on('click', function(e){
          window.location.href = '/users.html';
        });
      }

      header.append('<button id="id-button-logout">Logout</button>');
      $('#id-button-logout').on('click', function(e){
        window.location.href = '/login.html';
      });
    }
  });
});