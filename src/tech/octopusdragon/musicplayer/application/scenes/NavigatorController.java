package tech.octopusdragon.musicplayer.application.scenes;

import tech.octopusdragon.musicplayer.application.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.application.tools.NavigatorView;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class NavigatorController {
	
	// --- UI components ---
	private DirectoryView directoryView;	// Directory view
	private TrackView trackView;			// Track view
	
	@FXML private StackPane navigator;
	
	@FXML
	private void initialize() {
		
		// Instantiate the directory and track view
		directoryView = new DirectoryView();
		trackView = new TrackView();
		
		// Switch to directory/track view whenever navigator view value or
		// current directory changes
		MusicPlayerApplication.navigatorViewProperty().addMutateListener(
				(oldValue, newValue) -> {
			if (newValue == NavigatorView.DIRECTORY) {
				directoryView.newDirectory
				(MusicPlayerApplication.getMusicPlayer().getCurDirectory().get());
				switchToDirectoryView();
			}
			else if (newValue == NavigatorView.TRACK) {
				trackView.newDirectory(
						MusicPlayerApplication.getMusicPlayer().getCurDirectory().get());
				switchToTrackView();
			}
		});
	}
	
	/**
	 * Switches to folder view
	 */
	private void switchToDirectoryView() {
		if (!navigator.getChildren().contains(directoryView)) {
			navigator.getChildren().remove(trackView);
			navigator.getChildren().add(directoryView);
		}
	}
	
	/**
	 * Switches to track view
	 */
	private void switchToTrackView() {
		if (!navigator.getChildren().contains(trackView)) {
			navigator.getChildren().remove(directoryView);
			navigator.getChildren().add(trackView);
		}
	}

}
