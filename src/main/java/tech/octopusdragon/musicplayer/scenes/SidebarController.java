package tech.octopusdragon.musicplayer.scenes;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.components.SidebarCell;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.tools.Userdata;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SidebarController {
	
	// --- Constants ---
	// Default width of the sidebar
	private static final double DEFAULT_WIDTH = 235.0;
	// Minimum width of the sidebar
	private static final double MIN_WIDTH = 75.0;
	// Maximum width of the sidebar as a proportion of the window size
	private static final double MAX_WIDTH_PROPORTION = 0.5;
	
	// --- Variables ---
	private ObservableList<Directory> rootDirectories;	// The root directories

	// --- UI components ---
	@FXML private ListView<Directory> sidebar;
	
	@FXML
	private void initialize() {
		
		// Initialize root directories
		rootDirectories = MusicPlayerApplication.getRootDirectories();
		
		// Set text for the sidebar list items so that it shows the names of
		// the corresponding directories
		sidebar.setCellFactory(
				new Callback<ListView<Directory>, ListCell<Directory>>() {
			@Override
			public ListCell<Directory> call(ListView<Directory> lv) {
				return new SidebarCell();
			}
		});
		
		// Set initial width and listen and save width to userdata
		sidebar.setMaxWidth(Double.parseDouble(
				Userdata.readProperty("sidebar-width",
					String.valueOf(DEFAULT_WIDTH))));
		sidebar.widthProperty().addListener(
				(observable, oldValue, newValue) -> {
			Userdata.writeProperty(
					"sidebar-width",
					String.valueOf(newValue));
		});
		
		// Set min width
		sidebar.setMinWidth(MIN_WIDTH);
		
		// Bind max width to half of scene
		// Prevent scene from shrinking if at this size
		sidebar.sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> obs, Scene oldVAl, Scene newVal) {
				if (newVal == null) return;
				Platform.runLater(() -> {
					Scene scene = sidebar.getScene();
					Stage stage = (Stage)scene.getWindow();
					sidebar.maxWidthProperty().bind(
							scene.widthProperty().multiply(MAX_WIDTH_PROPORTION));
					// Get the window decoration width
					double decorationWidth = stage.getWidth() - scene.getWidth();
					// Get existing min width
					double minWidth = stage.getMinWidth();
					stage.minWidthProperty().bind(Bindings.max(
							sidebar.widthProperty().multiply(2).add(decorationWidth),
							minWidth));
				});
				sidebar.sceneProperty().removeListener(this);
			}
		});
		
		// Change directories in the navigator on click
		sidebar.setOnMouseClicked(event -> {
			Directory newVal = sidebar.getSelectionModel().getSelectedItem();
			if (newVal == null  ||
					newVal == MusicPlayerApplication.getMusicPlayer().getCurDirectory().get())
				return;
			
			MusicPlayerApplication.newRootDirectory(
					sidebar.getSelectionModel().getSelectedItem());
		});
		
		// Change directories in the sidebar on root directory changed
		MusicPlayerApplication.currentRootDirectoryProperty().addListener(
				(obs, oldVal, newVal) -> {
			if (newVal == null  ||
					newVal == sidebar.getSelectionModel().getSelectedItem())
				return;
			sidebar.getSelectionModel().select(newVal);
		});
		sidebar.getSelectionModel().select(
				MusicPlayerApplication.getCurrentRootDirectory());
		
		
		// Set root directories as items
		rootDirectories.addListener((ListChangeListener<Directory>)(c) -> {
			sidebar.setItems(rootDirectories);
		});
		sidebar.setItems(rootDirectories);
	}
}
