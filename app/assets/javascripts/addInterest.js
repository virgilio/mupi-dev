jQuery(function() {
	var validator = jQuery('#addInterestForm').validate({
		rules : {
			name : {
				required : true
			},
			description : {
				required:true,
				minlength : 20,
				maxlength : 3000
			}
		},
		messages: {
			name : {
				required : "Você precisa informar um nome para sua sugestão de interesse"
			},
			description : {
				required  : "Você precisa descrever o interesse sugerido",
				minlength : "A descrição deve ter ao menos {0} caracteres",
				maxlength : "A descrição deve ter no máximo {0} caracteres"
			}
		},
		highlight : function(label) {
			jQuery(label).closest('.control-group').removeClass('success').addClass('error');
		},
		success : function(label) {
			label.closest('.control-group').removeClass('error').addClass('success');
		}
	});
})