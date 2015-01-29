function openUploadPopUp(){
	$('#uploadWindow').show().fadeIn(100);
	$('#back').addClass('black_overlay').fadeIn(100);
}

function closeUploadWindow(){
	$('#uploadWindow').hide();
	$('#back').removeClass('black_overlay').fadeIn(100);
	$('input[name=file]').replaceWith($('input[name=file]').clone(true));
}