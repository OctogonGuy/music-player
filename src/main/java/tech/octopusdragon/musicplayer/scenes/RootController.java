package tech.octopusdragon.musicplayer.scenes;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.tools.View;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class RootController {
	
	@FXML private BorderPane mainPane;	// Pane with everything but control box

	private ExplorerView explorerView;
	private CurrentlyPlayingView currentlyPlayingView;
	
	@FXML
	private void initialize() {
		explorerView = new ExplorerView();
		currentlyPlayingView = new CurrentlyPlayingView();
		
		// Change view upon application view changing
		MusicPlayerApplication.currentViewProperty().addListener((obs, oldVal, newVal) -> {
			switchView(newVal);
		});
		switchView(MusicPlayerApplication.getCurrentView());
	}

	/**
	 * Switches the view to a different view
	 * @param view The new view to show
	 */
	private void switchView(View view) {
		Region newView = null;
		switch (view) {
		case EXPLORER:
			newView = explorerView;
			break;
		case CURRENTLY_PLAYING:
			newView = currentlyPlayingView;
			break;
		}
		mainPane.setCenter(newView);
	}

}
