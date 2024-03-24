package tech.octopusdragon.musicplayer.window;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;

/**
 * Allows the user to choose the root folder for the music library
 * @author Alex Gill
 *
 */
public class LibraryLocationDialog extends Dialog<String> {
	
	private final GridPane grid;
    private final Label label;
    private final Button browseButton;
    private final TextField textField;
    
    /**
     * Constructor
     * @param rootFolderPath The path of the initial folder of the directory
     * chooser
     */
	public LibraryLocationDialog(String rootFolderPath) {
		final DialogPane dialogPane = getDialogPane();
		
		textField = new TextField();
		textField.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(textField, Priority.ALWAYS);
		GridPane.setFillWidth(textField, true);
		
		label = new Label();
		label.setPrefWidth(Region.USE_COMPUTED_SIZE);
		label.textProperty().bind(dialogPane.contentTextProperty());
		
		browseButton = new Button("Browse...");
		browseButton.setOnAction(event -> {
			String libraryLocation = browseLibraryLocation(rootFolderPath);
			textField.setText(libraryLocation == null ? null : libraryLocation);
		});
		
		grid = new GridPane();
		grid.setHgap(10);
		grid.setPrefWidth(400);
		grid.setMaxWidth(Double.MAX_VALUE);
		grid.setAlignment(Pos.CENTER_LEFT);
		grid.add(label, 0, 0);
		grid.add(textField, 1, 0);
		grid.add(browseButton, 2, 0);
		getDialogPane().setContent(grid);
		
		this.setTitle("Music Library Location");
		dialogPane.setContentText("Directory:");
		dialogPane.setHeaderText("Choose the location of your music library");
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		Platform.runLater(() -> browseButton.requestFocus());
		
		setResultConverter((dialogButton) -> {
			ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			return data == ButtonData.OK_DONE ? textField.getText() : null;
		});
	}
	
	
	/**
	 * Opens a directory chooser and returns the chosen directory
	 * @param rootFolderPath The folder for the chooser to start out in
	 * @return The user's chosen directory
	 */
	public String browseLibraryLocation(String rootFolderPath) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose music library folder");
		directoryChooser.setInitialDirectory(new File(rootFolderPath));
		File selectedDirectory = directoryChooser.showDialog(getOwner());
		if (selectedDirectory == null) return null;
		return selectedDirectory.getPath();
	}
}
