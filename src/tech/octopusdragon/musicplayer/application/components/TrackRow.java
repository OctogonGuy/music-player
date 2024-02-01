package tech.octopusdragon.musicplayer.application.components;

import tech.octopusdragon.musicplayer.Song;
import tech.octopusdragon.musicplayer.application.MusicPlayerApplication;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;

/**
 * A table cell that automatically updates its pseudo class when the a cell is
 * selected or clicked.
 * @author Alex Gill
 *
 */
public class TrackRow extends TableRow<Song> {
	
	private ContextMenu contextMenu;	// The context menu for this row

	public TrackRow() {
		super();
		this.getStyleClass().add("song-row");
		
		// Add context menu
		contextMenu = new TrackRowContextMenu(this);
		
		// Clear selection upon clicking in the directory view but not on a node
		this.setOnMouseClicked(event -> {
			this.getTableView().getSelectionModel().clearSelection();
		});
	}
	
    @Override
    protected void updateItem(Song item, boolean empty) {
        if (item == getItem()) return;
        super.updateItem(item, empty);

        if (empty || item == null) {
        	
        	// Do not display context menu on request
        	this.setContextMenu(null);
        	
    		// Clear selection upon clicking in the directory view but not on a
        	// node
    		this.setOnMouseClicked(event -> {
    			this.getTableView().getSelectionModel().clearSelection();
    		});
            
        } else {
        	
        	// Display context menu on request
        	this.setContextMenu(contextMenu);
        	
    		// Clear selection upon clicking in the directory view but not on a
        	// node
    		this.setOnMouseClicked(new ClickEventHandler());
        }
    }
	
	
	/**
	 * If double click, plays the song at the index corresponding to the row of
	 * the cell in the track list.
	 * @author Alex Gill
	 *
	 */
	private class ClickEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			TrackList trackNavigator = (TrackList)((TrackRow)event.getSource()).getTableView();
			
			// If a row is double clicked, play the song
			if (event.getClickCount() == 2) {
				// Start new media
				MusicPlayerApplication.getMusicPlayer().newMediaReload(trackNavigator.songs.indexOf(
						trackNavigator.getSelectionModel().getSelectedItem()));
				MusicPlayerApplication.getMusicPlayer().playMedia();
				
				// Deselect the row
				trackNavigator.getSelectionModel().clearSelection();
			}
			
			// Consume event so row is not deselected in parent
			event.consume();
		}
	}
	
}
