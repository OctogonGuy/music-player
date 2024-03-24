package tech.octopusdragon.musicplayer.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.model.Album;
import tech.octopusdragon.musicplayer.model.AlbumCollection;
import tech.octopusdragon.musicplayer.model.AlbumCollectionDirectory;
import tech.octopusdragon.musicplayer.model.ChooseAlbumsDirectory;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.model.DirectoryType;
import tech.octopusdragon.musicplayer.model.Song;
import tech.octopusdragon.musicplayer.tools.Userdata;
import tech.octopusdragon.musicplayer.util.Resource;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;


/**
 * A component that navigates through directories to pick a group of tracks to
 * play.
 * @author Alex Gill
 *
 */
public class DirectoryGrid extends ScrollPane {
	
	// --- Items/Selection model ---
	private FilteredList<Directory> filteredItems;	// Filtered directory items
	private SortedList<Directory> sortedItems;		// Sorted directory items
	// Sorted directory items as object property to observe list change
	private ObjectProperty<ObservableList<Directory>> itemsProperty;
	// Property for comparator for directory items
	private ObjectProperty<Comparator<Directory>> comparatorProperty;
	private SelectionModel selectionModel;	// The selection model
	private Directory curDirectory;	// The current directory
	
	// --- GUI components ---
	private FlowPane flowPane;	// Flow pane showing nodes/folders
	// All Directory nodes including those not in the flow pane
	private List<DirectoryNode> allNodes;
	
	// --- Other ---
	// A separate thread on which nodes are to be added and updated
	private Task<Void> nodeTask;
	private Thread nodeThread;
	
	
	/**
	 * Instantiates a new folder navigator.
	 */
	public DirectoryGrid() {
		super();
		this.setMinSize(0.0, 0.0);
		this.setFitToWidth(true);
		this.setFitToHeight(true);
		this.setId("directory-view");
		
		// Instantiate selection model
		selectionModel = new SelectionModel();
		
		// Instantiate comparator property
		comparatorProperty = new SimpleObjectProperty<>();
		
		// Instantiate all nodes list
		allNodes = new ArrayList<DirectoryNode>();
		
		// Clear selection upon clicking in the directory view but not on a node
		this.setOnMouseClicked(event -> selectionModel.clear());
		
		// Create items
		itemsProperty = new SimpleObjectProperty<>();
		
		// Create the flow pane showing nodes/folders
		flowPane = new FlowPane();
		this.setContent(flowPane);
	}
	
	
	/**
	 * @return The list of directory items
	 */
	public ObservableList<Directory> getItems() {
		return itemsProperty.get();
	}
	
	
	/**
	 * Sets the directory items
	 * @param items The items
	 */
	private void setItems(ObservableList<Directory> items) {
		filteredItems = new FilteredList<>(items);
		sortedItems = new SortedList<>(filteredItems);
		itemsProperty.set(sortedItems);
		getSelectionModel().clear();
	}
	
	
	/**
	 * @return The items' comparator
	 */
	public ObjectProperty<Comparator<Directory>> comparatorProperty() {
		return comparatorProperty;
	}
	
	
	/**
	 * @return The items' comparator
	 */
	public Comparator<Directory> getComparator() {
		return comparatorProperty.get();
	}
	
	
	/**
	 * Sets the items' comparator
	 * @param comparator The items' comparator
	 */
	public void setComparator(Comparator<Directory> comparator) {
		comparatorProperty.set(comparator);
	}
	
	
	/**
	 * 
	 * @return The selection model
	 */
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	
	/**
	 * Opens a new directory, showing its folders in the flow pane.
	 * @param directory The directory
	 */
	public void newDirectory(Directory directory) {
		curDirectory = directory;
		setItems(FXCollections.observableArrayList(directory.getDirectories()));
		
		// Filter albums if in album chooser
		Directory tempDir = directory;
		boolean chooseDirectory = false;
		AlbumCollection albumCollection = null;
		while (tempDir.getParent() != null) {
			if (tempDir.getParent().getType() == DirectoryType.CHOOSE_ALBUMS_DIRECTORY) {
				chooseDirectory = true;
				albumCollection = (AlbumCollection) tempDir.getParent().getParent();
				break;
			}
			tempDir = tempDir.getParent();
		}
		if (chooseDirectory) {
			final AlbumCollection finalAlbumCollection = albumCollection;
			filteredItems.setPredicate(album -> {
				return !finalAlbumCollection.getDirectories().contains(album);
			});
		}
		else {
			filteredItems.setPredicate(dir -> true);
		}
		
		// Refresh the display
		refresh();
	}
	
	
	/**
	 * Sorts the existing directoriy nodes to the same order as the items
	 */
	public void sort() {
		sortedItems.setComparator(comparatorProperty.get());
		refresh();
	}
	
	
	/**
	 * Draws the track navigator's nodes to all the folders with content in the
	 * given directory.
	 */
	public void refresh() {
		
		// Stop the node thread if it is running
		if (nodeTask != null && nodeTask.isRunning()) {
			while (nodeTask.cancel(false));
			try {
				nodeThread.join();
			} catch (InterruptedException e) {
				System.out.println("Thread was unexpectedly interrupted");
				e.printStackTrace();
			}
		}
		
		// Load new nodes on a separate thread so that the GUI does not freeze
		// while they are loading
		nodeTask = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					
			List<Directory> items = getItems();
			
			int index = 0;
			
			// If the directory is a choose albums directory, set to add
			// album to playlist and remove node from pane
			AlbumCollection albumCollection = null;
			Directory curDir = curDirectory;
			while (curDir.getParent() != null) {
				if (curDir.getParent().getType() == DirectoryType.CHOOSE_ALBUMS_DIRECTORY) {
					albumCollection = (AlbumCollection) curDir.getParent().getParent();
					break;
				}
				curDir = curDir.getParent();
			}
			if (albumCollection != null) {
				for (; index < items.size(); index++) {
					if (isCancelled()) return null;
					Directory directory = items.get(index);
					DirectoryNode dirNode = putDirectoryNode(index, directory, directory.getName());
					if (directory instanceof Album) {
						dirNode.setOnMouseClicked(new ClickAddAlbumHandler(
								(Album)directory,
								albumCollection));
					}
					else {
						dirNode.setOnMouseClicked(new ClickMoveForwardHandler());
					}
				}
			}
			// Go forward to either track list or directory, whichever is
			// more appropriate, on click
			else {
				for (; index < items.size(); index++) {
					if (isCancelled()) return null;
					Directory directory = items.get(index);
					DirectoryNode dirNode = putDirectoryNode(index, directory, directory.getName());
					dirNode.setOnMouseClicked(new ClickMoveForwardHandler());
				}
			}
			
			// If there is media directly in the directory, create an extra node
			// to play the media in the directory
			if (curDirectory.hasSongs()) {
				if (isCancelled()) return null;
				Directory curDirTracks = new Directory("<Current Folder>", curDirectory);
				for (Song song : curDirectory.getSongs()) curDirTracks.addSong(song);
				DirectoryNode mediaNode = putDirectoryNode(index, curDirTracks, curDirTracks.getName());
				mediaNode.setOnMouseClicked(new ClickMoveForwardHandler());
				index++;
			}
			
			
			
			// If the directory is for album collections, create a plus node that
			// adds a new album collection
			if (curDirectory.getType() == DirectoryType.ALBUM_COLLECTION_DIRECTORY) {
				DirectoryNode addAlbumCollectionNode = putDirectoryNode(index, curDirectory);
				Platform.runLater(() -> {
					addAlbumCollectionNode.setMask(new Image(Resource.PLUS_IMAGE.getPath()));
					// Remove context pane
					addAlbumCollectionNode.setOnContextMenuRequested(null);
					addAlbumCollectionNode.setMaxHeight(Region.USE_PREF_SIZE);
				});
				addAlbumCollectionNode.setOnMouseClicked(new ClickAddAlbumCollectionHandler());
				index++;
			}
			
			// If the directory is an album collection, add a plus node that adds
			// a new album
			if (curDirectory.getType() == DirectoryType.ALBUM_COLLECTION) {
				DirectoryNode addAlbumCollectionAlbumNode = putDirectoryNode(index, curDirectory);
				Platform.runLater(() -> {
					addAlbumCollectionAlbumNode.setMask(new Image(Resource.PLUS_IMAGE.getPath()));
					// Remove context pane
					addAlbumCollectionAlbumNode.setOnContextMenuRequested(null);
					addAlbumCollectionAlbumNode.setMaxHeight(Region.USE_PREF_SIZE);
				});
				addAlbumCollectionAlbumNode.setOnMouseClicked(new ClickAddAlbumsHandler((AlbumCollection)curDirectory));
				index++;
			}
			
			
			// Hide all nodes that should no longer be showing
			for (; index < allNodes.size(); index++) {
				DirectoryNode node = allNodes.get(index);
				Platform.runLater(() -> {
					if (flowPane.getChildren().contains(node)) {
						flowPane.getChildren().remove(node);
					}
				});
			}
			
			return null;
		}};
		nodeThread = new Thread(nodeTask);
		nodeThread.start();
	}
	
	
	public DirectoryNode putDirectoryNode(int index, Directory directory) {
		return putDirectoryNode(index, directory, null);
	}
	
	
	public DirectoryNode putDirectoryNode(int index, Directory directory, String name) {
		DirectoryNode dirNode;
		if (index >= allNodes.size()) {
			dirNode = new DirectoryNode(this, directory, name);
			dirNode.setOnMouseEntered(new MouseEnterEventHandler());
			dirNode.setOnMouseExited(new MouseExitEventHandler());
			allNodes.add(dirNode);
			Platform.runLater(() -> {
				flowPane.getChildren().add(dirNode);
			});
		}
		else {
			dirNode = allNodes.get(index);
			Platform.runLater(() -> {
				dirNode.setDirectory(directory, name);
				if (!flowPane.getChildren().contains(dirNode)) {
					flowPane.getChildren().add(dirNode);
				}
			});
		}
		dirNode.setMaxHeight(Region.USE_COMPUTED_SIZE);
		return dirNode;
	}
	
	
	/**
	 * Parent class of all other click handlers
	 * @author Alex Gill
	 *
	 */
	private class MouseClickHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			DirectoryNode dirNode = (DirectoryNode)event.getSource();
			
			// Consume event so node is not deselected by directory view
			event.consume();
			
			// If shift is held down, select all nodes between this one and the
			// last one selected
			if (event.isShiftDown()) {
				int thisIndex = flowPane.getChildren().indexOf(dirNode);
				int lastIndex = flowPane.getChildren().indexOf(selectionModel.getLastSelected());
				selectionModel.selectRange(Math.min(thisIndex, lastIndex), Math.max(thisIndex, lastIndex));
			}
			
			// If control is held down, select this node
			else if (event.isControlDown()) {
				if (!selectionModel.contains(dirNode)) {
					selectionModel.add(dirNode);
				}
				else {
					selectionModel.remove(dirNode);
				}
			}
			
			// If right click, only clear selection if selecting unselected node
			else if (event.getButton() == MouseButton.SECONDARY) {
				if (!selectionModel.contains(dirNode)) {
					selectionModel.clearAndAdd(dirNode);
				}
			}
			
			// If nothing is held down and left click, clear selection and
			// select this node
			else {
				selectionModel.clearAndAdd(dirNode);
			}
		}
		
	}
	
	
	/**
	 * Opens a new navigation upon clicking a node.
	 * @author Alex Gill
	 *
	 */
	private class ClickMoveForwardHandler extends MouseClickHandler {
		@Override
		public void handle(MouseEvent event) {
			super.handle(event);
			
			// If double click, go to new directory
			if (event.getClickCount() % 2 == 0) {
				Directory dir = ((DirectoryNode)event.getSource()).getDirectory();
				MusicPlayerApplication.goForward(dir);
			}
		}
	}
	
	
	/**
	 * Adds an album collection
	 * @author Alex Gill
	 *
	 */
	private class ClickAddAlbumCollectionHandler extends MouseClickHandler {
		@Override
		public void handle(MouseEvent event) {
			super.handle(event);
			
			// If double click, show a dialog to name the new album collection. If
			// empty or canceled, do nothing. But if the user gave a name,
			// create a new album collection, add it to this directory, and
			// refresh the directory
			if (event.getClickCount() % 2 == 0) {
				TextInputDialog nameDialog = new TextInputDialog();
				nameDialog.setContentText("Name");
				nameDialog.setHeaderText("What would you like to call your " +
						"new album collection?");
				nameDialog.setTitle("New Album Collection");
				Optional<String> result = nameDialog.showAndWait();
				if (result.isPresent() && !result.get().trim().isEmpty()) {
					AlbumCollectionDirectory directory = 
					(AlbumCollectionDirectory) Userdata.
							getRootDirectories().getAlbumCollectionDirectory();
					
					AlbumCollection newAlbumCollection = new AlbumCollection(
							result.get(), directory);
					directory.addDirectory(newAlbumCollection);
					newDirectory(directory);
					
					// Save music library
					Userdata.saveMusicLibrary(false);
					
					refresh();
					
					// Save album collection
					Userdata.saveAlbumCollection(newAlbumCollection);
				}
			}
		}
	}
	
	
	/**
	 * Allows the user to add new albums to an album collection
	 * @author Alex Gill
	 *
	 */
	private class ClickAddAlbumsHandler extends MouseClickHandler {
		
		private AlbumCollection albumCollection;	// The root album collection
		
		/**
		 * Constructs a handler
		 * @param albumCollection The root album collection
		 */
		public ClickAddAlbumsHandler(AlbumCollection albumCollection) {
			this.albumCollection = albumCollection;
		}
		
		@Override
		public void handle(MouseEvent event) {
			super.handle(event);
			
			// If double click, go to all albums the user can add to the album
			// collection
			if (event.getClickCount() % 2 == 0) {
				ChooseAlbumsDirectory chooser = new ChooseAlbumsDirectory(
						"Choose Albums", albumCollection);
				MusicPlayerApplication.goForwardToDirectory(chooser);
			}
		}
	}
	
	
	/**
	 * Allows the user to add a specific album to an album collection
	 * @author Alex Gill
	 *
	 */
	private class ClickAddAlbumHandler extends MouseClickHandler {
		
		private Album album;					// The album to add
		private AlbumCollection albumCollection;	// The root album collection
		
		/**
		 * Constructs a handler
		 * @param albumCollection The root album collection
		 */
		public ClickAddAlbumHandler(Album album, AlbumCollection albumCollection) {
			this.album = album;
			this.albumCollection = albumCollection;
		}
		
		@Override
		public void handle(MouseEvent event) {
			super.handle(event);
			
			// If double click, create a copy of the album in order to set the
			// parent as this album collection. Then add the copy album to the
			// album collection and remove the node
			if (event.getClickCount() % 2 == 0) {
				Album copyAlbum = new Album(album);
				copyAlbum.setParent(albumCollection);
				albumCollection.getDirectories().add(copyAlbum);
				Platform.runLater(() -> {
					flowPane.getChildren().remove(event.getSource());
				});

				// Save album collection
				Userdata.saveAlbumCollection(albumCollection);
			}
		}
	}
	
	
	/**
	 * Indicates that the node is hovered over
	 * @author Alex Gill
	 *
	 */
	private class MouseEnterEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// Change the style of the hovered node
			((DirectoryNode)event.getSource()).pseudoClassStateChanged(
					PseudoClass.getPseudoClass("hover"), true);
		}
	}
	
	
	/**
	 * Indicates that the node is no longer hovered over
	 * @author Alex Gill
	 *
	 */
	private class MouseExitEventHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// Change the style of the hovered node
			((DirectoryNode)event.getSource()).pseudoClassStateChanged(
					PseudoClass.getPseudoClass("hover"), false);
		}
	}
	
	
	/**
	 * A selection model to be able to select directory nodes
	 * @author Alex Gill
	 *
	 */
	public class SelectionModel {
		
		// The current selection
		private Set<DirectoryNode> selection;
		
		// The last node selected
		private DirectoryNode lastSelected;
		
		// The current selected range
		private Set<DirectoryNode> curRange;
		
		/**
		 * Constructor
		 */
		public SelectionModel() {
			selection = new HashSet<>();
			curRange = new HashSet<>();
		}
		
		/**
		 * @return The most recently selected node
		 */
		public DirectoryNode getLastSelected() {
			return lastSelected;
		}
		
		/**
		 * Adds a node to the selection model
		 * @param dirNode The node to add
		 */
		public void add(DirectoryNode dirNode) {
			clearRange();
			dirNode.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
			selection.add(dirNode);
			lastSelected = dirNode;
		}
		
		/**
		 * Adds a node to the selection model without updating last selected
		 * @param dirNode The node to add
		 */
		private void addIgnore(DirectoryNode dirNode) {
			dirNode.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
			selection.add(dirNode);
		}
		
		/**
		 * Adds a node to the  range
		 * @param dirNode The node to add
		 */
		private void addToRange(DirectoryNode dirNode) {
			curRange.add(dirNode);
		}
		
		/**
		 * Removes a node from the selection model
		 * @param dirNode The node to remove
		 */
		public void remove(DirectoryNode dirNode) {
			dirNode.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
			selection.remove(dirNode);
		}
		
		/**
		 * Removes a node from the selection model and the range
		 * @param dirNode The node to remove
		 */
		private void removeFromRange(DirectoryNode dirNode) {
			curRange.remove(dirNode);
		}
		
		/**
		 * Removes all nodes from the selection model
		 */
		public void clear() {
			while (!selection.isEmpty()) {
				remove(selection.iterator().next());
			}
		}
		
		/**
		 * Removes all nodes from the selection model and the range
		 */
		private void clearRange() {
			while (!curRange.isEmpty()) {
				removeFromRange(curRange.iterator().next());
			}
		}
		
		/**
		 * Removes all nodes from the selection model and the range
		 */
		private void clearSelectionInRange() {
			for (DirectoryNode dirNode : curRange) {
				remove(dirNode);
			}
		}
		
		/**
		 * Removes all nodes from the selection model and adds the given node
		 * @param dirNode The node to add
		 */
		public void clearAndAdd(DirectoryNode dirNode) {
			clear();
			add(dirNode);
		}
		
		/**
		 * Selects all nodes in the range
		 */
		private void selectRange() {
			for (DirectoryNode dirNode : curRange) {
				addIgnore(dirNode);
			}
		}
		
		/**
		 * Adds all nodes within the given range to the selection model
		 * @param minIndex The lower index
		 * @param maxIndex The higher index
		 */
		public void selectRange(int minIndex, int maxIndex) {
			clearSelectionInRange();
			clearRange();
			int index = minIndex;
			while (index != maxIndex) {
				addToRange((DirectoryNode)flowPane.getChildren().get(index));
				index++;
			}
			addToRange((DirectoryNode)flowPane.getChildren().get(maxIndex));
			selectRange();
		}
		
		/**
		 * @return The selected directories
		 */
		public Directory[] getItems() {
			List<Directory> items = new ArrayList<>();
			for (DirectoryNode node : selection) {
				items.add(node.getDirectory());
			}
			items.sort(sortedItems.getComparator());
			return items.toArray(new Directory[0]);
		}
		
		/**
		 * Returns whether the selection model contains a given node
		 * @param dirNode The node to look up
		 * @return Whether the selection model contains the node
		 */
		public boolean contains(DirectoryNode dirNode) {
			return selection.contains(dirNode);
		}
	}
}
