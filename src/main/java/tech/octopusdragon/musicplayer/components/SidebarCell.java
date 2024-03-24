package tech.octopusdragon.musicplayer.components;

import javafx.scene.control.ListCell;
import tech.octopusdragon.musicplayer.model.Directory;

/**
 * A list cell to be used for the sidebar
 * @author Alex Gill
 *
 */
public class SidebarCell extends ListCell<Directory> {

	@Override
	public void updateItem(Directory item, boolean empty) {
		super.updateItem(item, empty);
		
		// If empty, set text to nothing
		if (item == null) {
			setText(null);
			
			// Consume event so that directory is not moved on click
			setOnMouseClicked(event -> event.consume());
			
			
		// Otherwise, set item text
		} else {
			setText(item.getName());
			
			// Do not consume event
			setOnMouseClicked(null);
		}
	}
}
