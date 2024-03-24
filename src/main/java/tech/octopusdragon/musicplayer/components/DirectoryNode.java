package tech.octopusdragon.musicplayer.components;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import tech.octopusdragon.musicplayer.model.Album;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.tools.Userdata;
import tech.octopusdragon.musicplayer.util.Resource;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Represents a node in the directory view. This node represents a subdirectory
 * in the current directory.
 * @author Alex Gill
 *
 */
public class DirectoryNode extends GridPane {
	
	// --- Variables ---
	private Directory directory;	// The directory this node represents
	private DirectoryGrid dirGrid;	// The directory grid of this node
	
	// --- UI components ---
	@FXML private ImageView imageView;
	@FXML private Rectangle imageMask;
	@FXML private StackPane imagePane;
	@FXML private Label label;
	private ContextMenu contextMenu;	// The context menu to be called
	
	// Load resources once that should be available for all nodes
	private static final Image directoryImage;
	private static final Image folderImage;
	private static final Image albumImage;
	private static final Image artistImage;
	private static final Image genreImage;
	private static final Image playlistImage;
	private static final Image albumCollectionImage;
	static {
		directoryImage = new Image(Resource.DIRECTORY_IMAGE.getResourceAsStream());
		folderImage = new Image(Resource.FOLDER_IMAGE.getResourceAsStream());
		albumImage = new Image(Resource.ALBUM_IMAGE.getResourceAsStream());
		artistImage = new Image(Resource.ARTIST_IMAGE.getResourceAsStream());
		genreImage = new Image(Resource.GENRE_IMAGE.getResourceAsStream());
		playlistImage = new Image(Resource.PLAYLIST_IMAGE.getResourceAsStream());
		albumCollectionImage = new Image(Resource.ALBUM_COLLECTION_IMAGE.getResourceAsStream());
	}
	

	
	/**
	 * Creates a directory node with no name that leads to the given directory
	 * @param directoryGrid The directory grid this node is a part of
	 * @param directory The directory to lead to
	 */
	public DirectoryNode(DirectoryGrid directoryGrid, Directory directory) {
		this(directoryGrid, directory, null);
	}


	/**
	 * Creates a directory node that leads to the given directory
	 * @param directoryGrid The directory grid this node is a part of
	 * @param directory The directory to lead to
	 * @param name The name of the directory that will show up in the navigator
	 */
	public DirectoryNode(DirectoryGrid directoryGrid, Directory directory, String name) {
		try {
			FXMLLoader loader = new FXMLLoader(DirectoryNode.class.getResource("/view/DirectoryNode.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		this.dirGrid = directoryGrid;
		
		// Create the image and resize it so that the height or width,
		// whichever is larger (or both if square), is equal to the image
		// height or width constant. Wrap it in a pane so that it can be
		// given padding.
		imageView.fitHeightProperty().bind(imagePane.prefHeightProperty());
		imageView.fitWidthProperty().bind(imagePane.prefWidthProperty());
		
		// Add a mask that clips the image that may be used instead of the image
		// view
		ImageView imageViewCopy = new ImageView();
		imageViewCopy.setPreserveRatio(true);
		imageViewCopy.imageProperty().bind(imageView.imageProperty());
		imageViewCopy.fitHeightProperty().bind(imagePane.prefHeightProperty());
		imageViewCopy.fitWidthProperty().bind(imagePane.prefWidthProperty());
		imageMask.setClip(imageViewCopy);
		imageMask.heightProperty().bind(imagePane.prefHeightProperty());
		imageMask.widthProperty().bind(imagePane.prefWidthProperty());
		
		// Set initial directory
		setDirectory(directory, name);
	}
	
	
	/**
	 * Sets the directory and updates the image and label to match
	 * @param directory
	 */
	public void setDirectory(Directory directory) {
		setDirectory(directory, null);
	}
	
	
	/**
	 * Sets the directory and updates the image and label to match
	 * @param directory The directory
	 * @param name The name if the directory
	 */
	public void setDirectory(Directory directory, String name) {
		this.directory = directory;
		
		// Set image/mask
		if (directory instanceof Album && ((Album) directory).getCoverArtData() != null) {
			final Image coverArt;
			if (Userdata.getCoverArt((Album) directory) != null) {
				coverArt = Userdata.getCoverArt((Album) directory);
			}
			else {
				coverArt = new Image(new ByteArrayInputStream(((Album)directory).getCoverArtData()));
			}
			Platform.runLater(() -> {
				setImage(coverArt);
			});
		}
		else {
			Image image =  null;
			switch(directory.getType()) {
			case DIRECTORY:
				image = directoryImage;
				break;
			case FOLDER:
				image = folderImage;
				break;
			case ALBUM:
				image = albumImage;
				break;
			case ARTIST:
				image = artistImage;
				break;
			case GENRE:
				image = genreImage;
				break;
			case PLAYLIST:
				image = playlistImage;
				break;
			case ALBUM_COLLECTION:
				image = albumCollectionImage;
				break;
			default:
				break;
			}
			setMask(image);
		}
		
		// Set label
		if (name == null) {
			if (this.getChildren().contains(label)) {
				this.getChildren().remove(label);
				this.getStyleClass().add("no-text");
			}
		}
		else {
			label.setText(name);
			if (!this.getChildren().contains(label)) {
				this.getChildren().add(label);
				this.getStyleClass().remove("no-text");
			}
		}

		// Add a context menu whenever this node is right clicked
		contextMenu = new DirectoryNodeContextMenu(this, dirGrid);
		this.setOnContextMenuRequested(event -> {
			contextMenu.show(this, event.getScreenX(), event.getScreenY());
		});
	}
	
	
	/**
	 * Sets the image of this directory node
	 * @param image The image to set
	 */
	public void setImage(Image image) {
		imageView.setImage(image);
		imageView.setVisible(true);
		imageMask.setVisible(false);
	}
	
	
	/**
	 * @return The image of this directory node
	 */
	public Image getImage() {
		return imageView.getImage();
	}
	
	
	/**
	 * Sets the mask of this directory node
	 * @param image An image to mask
	 */
	public void setMask(Image image) {
		imageView.setImage(image);
		imageMask.setVisible(true);
		imageView.setVisible(false);
	}
	
	
	/**
	 * Sets the text of the label of this directory node
	 * @param text The text to set
	 */
	public void setText(String text) {
		label.setText(text);
	}
	
	
	/**
	 * @return The directory this node leads to
	 */
	public Directory getDirectory() {
		return directory;
	}
}