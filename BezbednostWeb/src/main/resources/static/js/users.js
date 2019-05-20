
var table_users;

$(document).ready(function(e){
  
  table_users = $('#id-table-users');

  
  fill_table();

});

function fill_table(){

  customAjax({
    method: 'GET',
    url: '/user/all',
    success: function(users, status, xhr){
      var html;
      html = '<tr id="id-tr-header">' + 
                '<th>ID</th>' + 
                '<th>Email</th>' + 
                '<th>Certificate</th>' +  
              '</tr>';
      table_users.append(html);
      users.forEach(user => {
        html = '<tr class="class-tr-user">' + 
                  '<td>'+ user.id +'</td>' + 
                  '<td>'+ user.email +'</td>' + 
                  '<td>'+ 
                    '<input id="id-checkbox-active-'+ user.id +'" type="checkbox" '+ ((user.active?'checked':'')) +'> active <br>' + 
                    '<input id="id-checkbox-admin-'+ user.id +'" type="checkbox" '+ ((user.admin?'checked':'')) +'> admin <br>' + 
                  '</td>' + 
                '</tr>';
        table_users.append(html);

        $('#id-checkbox-active-'+ user.id).on('click', function(e){
          customAjax({
            method: 'POST',
            url: '/user/setActive',
            data: { userId: user.id, 'active': $(this).is(':checked') }
          });
        });

        $('#id-checkbox-admin-'+ user.id).on('click', function(e){
          customAjax({
            method: 'POST',
            url: '/user/setAdmin',
            data: { userId: user.id, 'admin': $(this).is(':checked') }
          });
        });
      });
    },
    error: function(xhr, status, error){

    }
  });
}