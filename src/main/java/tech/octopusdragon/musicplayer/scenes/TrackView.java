package tech.octopusdragon.musicplayer.scenes;

import java.io.IOException;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.components.AlbumInfoBox;
import tech.octopusdragon.musicplayer.components.TrackList;
import tech.octopusdragon.musicplayer.model.Album;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.tools.NavigatorView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

/**
 * Shows the track list and the album info box
 * @author Alex Gill
 *
 */
public class TrackView extends VBox {
	
	// --- UI components ---
	@FXML private AlbumInfoBox albumInfoBox;
	@FXML private TrackList trackList;
	
	/**
	 * Constructs the track view
	 */
	public TrackView() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TrackView.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		// Clear selection upon clicking in the directory view but not on a node
		this.setOnMouseClicked(event -> trackList.getSelectionModel().clearSelection());
	}

	
	/**
	 * Moves to a new directory
	 * @param dir The directory
	 */
	public void newDirectory(Directory dir) {
		showHideAlbumInfoBox();
		trackList.newDirectory(dir);
	}
	
	
	/**
	 * Shows/hides the album info box depending on whether or not the current
	 * directory is an album
	 */
	public void showHideAlbumInfoBox() {
		if (MusicPlayerApplication.getNavigatorView() != NavigatorView.TRACK)
			return;
		Directory curDirectory = MusicPlayerApplication.getMusicPlayer().getCurDirectory().get();
		if (!this.getChildren().contains(albumInfoBox) &&
				curDirectory.getClass() == Album.class) {
			this.getChildren().add(0, albumInfoBox);
		}
		else if (this.getChildren().contains(albumInfoBox) &&
				!(curDirectory.getClass() == Album.class)) {
			this.getChildren().remove(albumInfoBox);
		}
	}
	
}
