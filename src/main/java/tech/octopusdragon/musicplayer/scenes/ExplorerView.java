package tech.octopusdragon.musicplayer.scenes;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 * Allows the user to navigate through directories and tracks to play songs and
 * collections of songs
 * @author Alex Gill
 *
 */
public class ExplorerView extends BorderPane {

	public ExplorerView() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ExplorerView.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
