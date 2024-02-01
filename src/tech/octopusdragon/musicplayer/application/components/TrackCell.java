package tech.octopusdragon.musicplayer.application.components;

import tech.octopusdragon.musicplayer.Song;
import tech.octopusdragon.musicplayer.application.MusicPlayerApplication;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.control.TableCell;

/**
 * A table cell that automatically updates its pseudo class when the song it is
 * referencing is being played.
 * @author Alex Gill
 *
 */
public class TrackCell<T> extends TableCell<Song, T> {
	
	public TrackCell() {
		super();
		this.getStyleClass().add("song-cell");
		
		MusicPlayerApplication.getMusicPlayer().getCurSong().addListener(
				(observable, oldValue, newValue) -> {
			updatePlaying();
		});
	}
	
    @Override
    protected void updateItem(T item, boolean empty) {
        if (item == getItem()) return;

        super.updateItem(item, empty);

		// If empty, set text to nothing
        if (empty || item == null) {
            super.setText(null);
            super.setGraphic(null);
            
			
    	// Otherwise, set item text
        } else {
            super.setText(item.toString());
            super.setGraphic(null);
        }
        
        // Update playing pseudo class
        Platform.runLater(() -> {
    		updatePlaying();
        });
    }
    
    /**
     * Updates the playing pseudo class to match whether or not the song
     * referenced by this cell is playing or not
     */
    private void updatePlaying() {
    	Song curSong = MusicPlayerApplication.getMusicPlayer().getCurSong().get();
		if (curSong != null &&
				this.getIndex() >= 0 &&
				this.getIndex() < this.getTableView().getItems().size() &&
				curSong.equals(this.getTableRow().getItem())) {
			this.pseudoClassStateChanged(
					PseudoClass.getPseudoClass("playing"), true);
		}
		else {
			this.pseudoClassStateChanged(
					PseudoClass.getPseudoClass("playing"), false);
		}
    }
}
