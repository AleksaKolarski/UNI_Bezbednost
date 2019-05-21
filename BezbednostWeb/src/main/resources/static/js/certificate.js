var xhr = new XMLHttpRequest();
	
xhr.open('GET', "/certificate/download", true);
xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwt'));
xhr.responseType = 'blob';

xhr.onload = function(e) {
	if (this.status == 200) {
		var blob = this.response;
		console.log(blob);
		var a = document.createElement('a');
		var url = window.URL.createObjectURL(blob);
		a.href = url;
		a.download = xhr.getResponseHeader('filename');
		a.click();
		window.URL.revokeObjectURL(url);
	}
};

xhr.send();