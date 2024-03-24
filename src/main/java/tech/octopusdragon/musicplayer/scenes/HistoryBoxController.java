package tech.octopusdragon.musicplayer.scenes;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.model.DirectoryType;
import tech.octopusdragon.musicplayer.tools.Userdata;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

public class HistoryBoxController {
	
	@FXML private Button backButton;
	@FXML private Button forwardButton;
	@FXML private ToolBar directoryBox;
	@FXML private TextField searchBar;
	
	@FXML
	private void initialize() {
		
		// Disable back button when there is nothing previous in history
		backButton.disableProperty().bind(
				Bindings.createBooleanBinding(() -> {
			return (MusicPlayerApplication.getHistoryIndex() <= 0);
		}, MusicPlayerApplication.historyIndexProperty()));
		
		// Disable forward button when there is nothing next in history
		forwardButton.disableProperty().bind(
				Bindings.createBooleanBinding(() -> {
			return (MusicPlayerApplication.getHistoryIndex() ==
					MusicPlayerApplication.getHistory().size() - 1);
		}, MusicPlayerApplication.historyIndexProperty()));
		
		// Update directory box when history index is changed
		MusicPlayerApplication.historyIndexProperty().addListener(listener -> {
			if (MusicPlayerApplication.getHistoryIndex() < 0) return;
			if (MusicPlayerApplication.getHistoryIndex() <
					MusicPlayerApplication.getHistory().size()) {
				updateDirectoryBox(MusicPlayerApplication.getHistory().get(
						MusicPlayerApplication.getHistoryIndex()));
			}
		});
		
		// Search songs upon typing in search bar
		searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
			if (MusicPlayerApplication.getCurrentRootDirectory() ==
					Userdata.getRootDirectories().getSongDirectory() ||
					newVal.isEmpty()) {
				MusicPlayerApplication.search(newVal);
			}
			else {
				MusicPlayerApplication.searchAndGo(newVal);
			}
		});
		MusicPlayerApplication.currentRootDirectoryProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal == Userdata.getRootDirectories().getSongDirectory()) return;
			searchBar.setText("");
		});
	}
	
	
	@FXML
	private void back(ActionEvent event) {
		MusicPlayerApplication.back();
	}
	
	
	@FXML
	private void forward(ActionEvent event) {
		MusicPlayerApplication.forward();
	}
	
	
	/**
	 * Updates the directory box to contain the directories in the history.
	 * @param newDirectory The lowest level directory.
	 */
	private void updateDirectoryBox(Directory newDirectory) {
		
		// Clear existing contents
		directoryBox.getItems().clear();
		
		// Move up the directories, adding a button for each directory, until
		// the root directory is reached
		Directory curDirectory = newDirectory;
		while (curDirectory != null) {
			Button curDirButton = new Button(curDirectory.getName());
			curDirButton.setOnAction(new ChangeButtonHandler(curDirectory));
			directoryBox.getItems().add(0, curDirButton);
			curDirectory = curDirectory.getParent();
		}
	}
	
	
	/**
	 * Upon click, navigates to a specified directory.
	 * @author Alex Gill
	 */
	private class ChangeButtonHandler implements EventHandler<ActionEvent> {
		
		private Directory dir;
		
		public ChangeButtonHandler(Directory dir) {
			this.dir = dir;
		}
		
		@Override
		public void handle(ActionEvent event) {
			// If the directory is an album collection, do not go to tracks, but
			// instead to to directory
			if (dir.getType() == DirectoryType.ALBUM_COLLECTION) {
				MusicPlayerApplication.goForwardToDirectory(dir);
			}
			
			else if (dir.hasDirectories()) {
				MusicPlayerApplication.goForwardToDirectory(dir);
			}
			else {
				MusicPlayerApplication.goForwardToTracks(dir);
			}
		}
	}
	
}
