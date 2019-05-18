/* Input field validation:
** -validation for text field
** -validation for email field
** -validation for password reset/registration fields
** 
** -use add_validation_* for continous check
** -use check_* for on demand check
**
** Author: Aleksa Kolarski (SF 27/2016)
** 2018
*/

// Add validation to text input field
function add_validation_text(field, min, max) {
    field.on('input', function (e) {
        check_text(field, min, max);
    });
}

function add_validation_text_min(field, min){
    field.on('input', function(e){
        check_text_min(field, min);
    });
}

function add_validation_text_max(field, max){
    field.on('input', function(e){
        check_text_max(field, max);
    });
}

// Add validation to email input field
function add_validation_email(field, max) {
    field.on('input', function (e) {
        check_email(field, max);
    });
}

// Add validation to two password input fields that need to match
function add_validation_password_match(field1, field2) {
    field1.on('input', function (e) {
        check_password_match(field1, field2);
    });
    field2.on('input', function (e) {
        check_password_match(field1, field2);
    });
}

// ... plus min & max length
function add_validation_password_match(field1, field2, min, max) {
    field1.on('input', function (e) {
        check_password_match(field1, field2, min, max);
    });
    field2.on('input', function (e) {
        check_password_match(field1, field2, min, max);
    });
}

// Add validation to number field
function add_validation_number(field, min, max){
    field.on('input change', function(e){
        check_number(field, min, max);
    });
}

function add_validation_number_min(field, min){
    field.on('input change', function(e){
        check_number_min(field, min);
    });
}

function add_validation_number_max(field, max){
    field.on('input change', function(e){
        check_number_max(field, max);
    });
}

// One time check
function check_text(field, min, max) {
    var content_length = field.val().length;
    if (content_length >= min && content_length <= max) {
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}

function check_text_min(field, min){
    var content_length = field.val().length;
    if(content_length >= min){
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}

function check_text_max(field, max){
    var content_length = field.val().length;
    if(content_length <= max){
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}

// One time check
function check_email(field, max) {
    if (/.+\@.+\..+/.test(field.val().toLowerCase()) && field.val().length <= max) {
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}

// One time check
function check_password_match(field1, field2) {
    if (field1.val() == field2.val()) {
        field2.css('border', '');
        return true;
    }
    field2.css('border', '2px solid #f00');
    return false;
}

// ... plus min & max length
function check_password_match(field1, field2, min, max) {
    var content_length = field1.val().length;
    if (field1.val() == field2.val() && content_length >= min && content_length <= max) {
        field2.css('border', '');
        return true;
    }
    field2.css('border', '2px solid #f00');
    return false;
}

function check_number(field, min, max){
    var content = parseInt(field.val());
    if(Number.isInteger(content) && content >= min && content <= max){
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}

function check_number_min(field, min){
    var content = parseInt(field.val());
    if(Number.isInteger(content) && content >= min){
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}

function check_number_max(field, max){
    var content = parseInt(field.val());
    if(Number.isInteger(content) && content <= max){
        field.css('border', '');
        return true;
    }
    field.css('border', '2px solid #f00');
    return false;
}