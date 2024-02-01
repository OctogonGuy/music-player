package tech.octopusdragon.musicplayer.application.components;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import tech.octopusdragon.musicplayer.Album;
import tech.octopusdragon.musicplayer.AlbumCollection;
import tech.octopusdragon.musicplayer.Directory;
import tech.octopusdragon.musicplayer.application.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.application.tools.Userdata;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
 
/**
 * A context menu for a directory node
 * @author Alex Gill
 *
 */
public class DirectoryNodeContextMenu extends ContextMenu {
	
	private DirectoryNode dirNode;	// The directory node this is attached to
	private Directory dir;			// The directory the directory node points to

	/**
	 * Creates a context menu for a directory node that interacts with it
	 * @param node The directory node
	 * @param grid The directory grid
	 */
	public DirectoryNodeContextMenu(DirectoryNode node, DirectoryGrid grid) {
		dirNode = node;
		dir = node.getDirectory();
		
		// Menu item to open the directory
		MenuItem openMenuItem = new MenuItem("Open");
		openMenuItem.setOnAction(event -> {
			MusicPlayerApplication.goForward(dir);
		});
		getItems().add(openMenuItem);
		
		// Menu item that sets the directory of the music player to the selected
		// director(ies) and starts playing it.
		MenuItem playMenuItem = new MenuItem("Play");
		playMenuItem.setOnAction(event -> {
			MusicPlayerApplication.getMusicPlayer().newDirectories(true, grid.getSelectionModel().getItems());
			MusicPlayerApplication.getMusicPlayer().newMediaReload();
			MusicPlayerApplication.getMusicPlayer().playMedia();
		});
		getItems().add(playMenuItem);
		
		// If the directory is an album in an album collection, add menu items
		// that can rearrange the albums in the album collection
		if (dir.getParent() != null &&
				dir.getParent() instanceof AlbumCollection) {
			MenuItem moveToTopAlbumCollectionAlbumItem = new MenuItem("Move to Top");
			moveToTopAlbumCollectionAlbumItem.setOnAction(event -> {
				moveToTopAlbumCollectionAlbum((Album)dir);
			});
			getItems().add(moveToTopAlbumCollectionAlbumItem);
		}
		if (dir.getParent() != null &&
				dir.getParent() instanceof AlbumCollection) {
			MenuItem moveUpAlbumCollectionAlbumItem = new MenuItem("Move Up");
			moveUpAlbumCollectionAlbumItem.setOnAction(event -> {
				moveUpAlbumCollectionAlbum((Album)dir);
			});
			getItems().add(moveUpAlbumCollectionAlbumItem);
		}
		if (dir.getParent() != null &&
				dir.getParent() instanceof AlbumCollection) {
			MenuItem moveDownAlbumCollectionAlbumItem = new MenuItem("Move Down");
			moveDownAlbumCollectionAlbumItem.setOnAction(event -> {
				moveDownAlbumCollectionAlbum((Album)dir);
			});
			getItems().add(moveDownAlbumCollectionAlbumItem);
		}
		if (dir.getParent() != null &&
				dir.getParent() instanceof AlbumCollection) {
			MenuItem moveToBottomAlbumCollectionAlbumItem = new MenuItem("Move to Bottom");
			moveToBottomAlbumCollectionAlbumItem.setOnAction(event -> {
				moveToBottomAlbumCollectionAlbum((Album)dir);
			});
			getItems().add(moveToBottomAlbumCollectionAlbumItem);
		}
		
		// If the directory is an album in an album collection, add a menu item
		// that can delete the album from the album collection
		if (dir.getParent() != null &&
				dir.getParent() instanceof AlbumCollection) {
			MenuItem deleteAlbumCollectionAlbumItem = new MenuItem("Delete");
			deleteAlbumCollectionAlbumItem.setOnAction(event -> {
				deleteAlbumCollectionAlbum((Album)dir);
			});
			getItems().add(deleteAlbumCollectionAlbumItem);
		}
		
		// If the directory is an album collection, add a menu item that can
		// rename the album collection
		if (dir.getClass() == AlbumCollection.class) {
			MenuItem renameAlbumCollectionItem = new MenuItem("Rename");
			renameAlbumCollectionItem.setOnAction(event -> {
				renameAlbumCollection((AlbumCollection)dir);
			});
			getItems().add(renameAlbumCollectionItem);
		}
		
		// If the directory is an album collection, add a menu item that can
		// delete the album collection
		if (dir.getClass() == AlbumCollection.class) {
			MenuItem deleteAlbumCollectionItem = new MenuItem("Delete");
			deleteAlbumCollectionItem.setOnAction(event -> {
				deleteAlbumCollection((AlbumCollection)dir);
			});
			getItems().add(deleteAlbumCollectionItem);
		}
	}
	
	
	/**
	 * Gives the user a confirmation message, and if the answer is yes, deletes
	 * the given album from its parent album collection
	 */
	private void deleteAlbumCollectionAlbum(Album album) {
		AlbumCollection albumCollection = (AlbumCollection)album.getParent();
		albumCollection.getDirectories().remove(album);
		if (dirNode.getParent().getClass().equals(FlowPane.class)) {
			((FlowPane)dirNode.getParent()).getChildren().remove(dirNode);
		}
		
		// Save album collection
		Userdata.saveAlbumCollection(albumCollection);
	}
	
	
	/**
	 * Shows the user a dialog to rename the given album collection
	 * @param albumCollection The album collection to rename
	 */
	private void renameAlbumCollection(AlbumCollection albumCollection) {
		TextInputDialog nameDialog = new TextInputDialog();
		nameDialog.setContentText("New Name");
		nameDialog.setHeaderText("What would you like to rename " +
				albumCollection.getName() + " to?");
		nameDialog.setTitle("Rename Album Collection");
		nameDialog.getEditor().setText(albumCollection.getName());
		Optional<String> result = nameDialog.showAndWait();
		if (result.isPresent() && !result.get().trim().isEmpty()) {
			Userdata.renameAlbumCollection(
					albumCollection, result.get());
			dirNode.setText(result.get());
		}
	}
	
	
	/**
	 * Gives the user a confirmation message, and if the answer is yes, deletes
	 * the given album collection from the user's library
	 * @param albumCollection The album collection to delete
	 */
	private void deleteAlbumCollection(AlbumCollection albumCollection) {
		Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
		confirmAlert.getButtonTypes().clear();
		confirmAlert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		confirmAlert.setHeaderText(String.format(
				"Are you sure you want to delete %s from your library?",
				albumCollection.getName()));
		confirmAlert.setTitle("Confirmation");
		
		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {
			Userdata.deleteAlbumCollection(albumCollection);
			if (dirNode.getParent().getClass().equals(FlowPane.class)) {
				((FlowPane)dirNode.getParent()).getChildren().remove(dirNode);
			}
		}
	}
	
	
	/**
	 * Moves the album one step up in its album collection
	 * @param albumCollection The album collection to move
	 */
	private void moveUpAlbumCollectionAlbum(Album album) {
		AlbumCollection albumCollection = (AlbumCollection)album.getParent();
		List<Directory> albumCollectionDirectories = albumCollection.getDirectories();
		int albumIndex = albumCollectionDirectories.indexOf(album);
		Collections.swap(albumCollectionDirectories, albumIndex, albumIndex - 1);
		if (dirNode.getParent().getClass().equals(FlowPane.class)) {
			FlowPane flowPane = (FlowPane)dirNode.getParent();
			flowPane.getChildren().remove(dirNode);
			flowPane.getChildren().add(albumIndex - 1, dirNode);
		}

		// Save album collection
		Userdata.saveAlbumCollection(albumCollection);
	}
	
	
	/**
	 * Moves the album one step down in its album collection
	 * @param albumCollection The album collection to move
	 */
	private void moveDownAlbumCollectionAlbum(Album album) {
		AlbumCollection albumCollection = (AlbumCollection)album.getParent();
		List<Directory> albumCollectionDirectories = albumCollection.getDirectories();
		int albumIndex = albumCollectionDirectories.indexOf(album);
		Collections.swap(albumCollectionDirectories, albumIndex, albumIndex + 1);
		if (dirNode.getParent().getClass().equals(FlowPane.class)) {
			FlowPane flowPane = (FlowPane)dirNode.getParent();
			flowPane.getChildren().remove(dirNode);
			flowPane.getChildren().add(albumIndex + 1, dirNode);
		}

		// Save album collection
		Userdata.saveAlbumCollection(albumCollection);
	}
	
	
	/**
	 * Moves the album all the way up in its album collection
	 * @param albumCollection The album collection to move
	 */
	private void moveToTopAlbumCollectionAlbum(Album album) {
		AlbumCollection albumCollection = (AlbumCollection)album.getParent();
		List<Directory> albumCollectionDirectories = albumCollection.getDirectories();
		albumCollectionDirectories.remove(album);
		albumCollectionDirectories.add(0, album);
		if (dirNode.getParent().getClass().equals(FlowPane.class)) {
			FlowPane flowPane = (FlowPane)dirNode.getParent();
			flowPane.getChildren().remove(dirNode);
			flowPane.getChildren().add(0, dirNode);
		}

		// Save album collection
		Userdata.saveAlbumCollection(albumCollection);
	}
	
	
	/**
	 * Moves the album all the way down in its album collection
	 * @param albumCollection The album collection to move
	 */
	private void moveToBottomAlbumCollectionAlbum(Album album) {
		AlbumCollection albumCollection = (AlbumCollection)album.getParent();
		List<Directory> albumCollectionDirectories = albumCollection.getDirectories();
		albumCollectionDirectories.remove(album);
		albumCollectionDirectories.add(album);
		if (dirNode.getParent().getClass().equals(FlowPane.class)) {
			FlowPane flowPane = (FlowPane)dirNode.getParent();
			flowPane.getChildren().remove(dirNode);
			flowPane.getChildren().add(flowPane.getChildren().size() - 1, dirNode);
		}

		// Save album collection
		Userdata.saveAlbumCollection(albumCollection);
	}

}
