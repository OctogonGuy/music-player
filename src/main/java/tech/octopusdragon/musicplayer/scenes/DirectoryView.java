package tech.octopusdragon.musicplayer.scenes;

import java.io.IOException;

import tech.octopusdragon.musicplayer.components.DirectoryGrid;
import tech.octopusdragon.musicplayer.model.Directory;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class DirectoryView extends VBox {
	
	// --- UI components ---
	@FXML private TableView<Directory> header;
	@FXML private DirectoryGrid directoryGrid;
	
	/**
	 * Constructs the track view
	 */
	public DirectoryView() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DirectoryView.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		// Make sure header does not extend past the height where it should
		this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
			StackPane tvHeader = (StackPane)header.getChildrenUnmodifiable().get(0);
			Platform.runLater(() -> {
				double tvHeaderHeight = tvHeader.getHeight() - 1;
				header.setMinHeight(tvHeaderHeight);
				header.setPrefHeight(tvHeaderHeight);
				header.setMaxHeight(tvHeaderHeight);
			});
		});
		
		
		// Name column
		TableColumn<Directory, String> nameColumn = new TableColumn<Directory, String>("Name");
		nameColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Directory, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Directory, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getName());
			}
		});
		header.getColumns().add(nameColumn);
		
		// Update items on sort
		header.setOnSort(e -> {
			directoryGrid.setComparator(header.getComparator());
			directoryGrid.sort();
		});
	}

	
	/**
	 * Moves to a new directory
	 * @param dir The directory
	 */
	public void newDirectory(Directory dir) {
		directoryGrid.newDirectory(dir);
	}
}
