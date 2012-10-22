
jQuery(function(){	
	jQuery('.remove_comment').one('click', function(e){
		e.preventDefault();
		var comment = jQuery(this).parent();
		
		if (confirm("Você deseja mesmo remover este comentário?")) {
	  		jsRoutes.controllers.Feed.removeComment(comment.attr('comment_id')).ajax({
				success : function(data) {
					var response = data.split("||");
					if(response[0] == 0){
						var sizeTag = comment.parent().prev().children('.comment_lnk').children('.comments_size');
						var newSize = parseInt(sizeTag.html()) - 1;
						sizeTag.html(newSize);
						comment.remove();
					}else{
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
							.addClass('alert-error')
							.html(response[1]);
					}
				},
				error : function() {
					jQuery('.page-alert').html("<div class='alert hide'> </div>");
					jQuery('.page-alert > .alert')
						.addClass('alert-error')
						.html("Erro no servidor, por favor tente novamente mais tarde");
	
				}
			});
		}
	});
})