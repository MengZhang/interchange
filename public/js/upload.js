
//-------------------------------------------------behavior of checkboxes
$(".current_data_selector").click(function(){

	if( !$(this).is(":checked") ){
	
		//when deselecting a checkbox
		//make all checkboxes enabled
		$(".current_data_selector").prop('disabled', false);
		$("#doneBtn").addClass('disabled');
	}
	else{
	
		//when selecting a checkbox
		//make all checkboxes disbaled if it is not checked
		var check_boxes     = $(".current_data_selector");
		$("#doneBtn").removeClass('disabled');

		$(check_boxes).each(function(index) {

		//checks if any checkbox is not selected, then disable it
		if( !$(this).is(":checked") )
		{
			$(check_boxes[index] ).prop('disabled', true);
		}

		});
	}


});
//-------------------------------------------------behavior of checkboxes