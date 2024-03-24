package tech.octopusdragon.musicplayer.components;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tech.octopusdragon.musicplayer.MusicPlayerApplication;

/**
 * A context menu for a track row
 * @author Alex Gill
 *
 */
public class TrackRowContextMenu extends ContextMenu {

	/**
	 * Creates a context menu for a track row that interacts with it
	 * @param trackRow The track row
	 */
	public TrackRowContextMenu(TrackRow trackRow) {
		
		// Menu item that plays the selected song(s)
		MenuItem playMenuItem = new MenuItem("Play");
		playMenuItem.setOnAction(event -> {
			if (trackRow.getTableView().getSelectionModel().getSelectedItems().size() == 1) {
				MusicPlayerApplication.getMusicPlayer().newMediaReload(trackRow.getItem());
			}
			else {
				MusicPlayerApplication.getMusicPlayer().newMediaReload();
			}
			MusicPlayerApplication.getMusicPlayer().playMedia();
		});
		getItems().add(playMenuItem);
		
		// Menu item that opens the song's directory in the system explorer
		MenuItem openFileLocationMenuItem = new MenuItem("Open File Location");
		openFileLocationMenuItem.setOnAction(event -> {
			File songDir = new File(trackRow.getItem().getPath()).getParentFile();
			try {
				Desktop.getDesktop().open(songDir);
			} catch (IOException e) {
				System.out.println("Error opening song file location");
				e.printStackTrace();
			}
		});
		getItems().add(openFileLocationMenuItem);
	}

}
