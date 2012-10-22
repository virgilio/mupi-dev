jQuery(function(){	
	jQuery('.remove_publication').one('click', function(e){
		e.preventDefault();
		var publication = jQuery(this).closest('.publication');
		
		if (confirm("Você deseja mesmo remover esta publicação? Fazendo isto, removerá todos os comentários ligados à ela!")) {
	  		jsRoutes.controllers.Feed.removePublication(publication.attr('pub_id')).ajax({
				success : function(data) {
					var response = data.split("||");
					if(response[0] == 0){
						publication.remove();
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