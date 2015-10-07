$( document ).ready( function() {
	
	if( $( "#is_game_over" ).val() !== "true" ) {
		$( ".board-row-tile.available" ).click( function( event ) {
			$( "#tile_id" ).val( event.target.id );
			$( "#form_mark_tile" ).submit();
		} );
	}
	
	$( "#btn-new-game" ).click( function( event ) {
		$( "#new_game" ).val( "yes" );
		$( "#form_mark_tile" ).submit();
	} );
	
} );