package tech.octopusdragon.musicplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.beans.property.*;
import javafx.scene.text.Font;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.model.MusicPlayer;
import tech.octopusdragon.musicplayer.model.Song;
import tech.octopusdragon.musicplayer.model.SongDirectory;
import tech.octopusdragon.musicplayer.tools.Balance;
import tech.octopusdragon.musicplayer.tools.NavigatorView;
import tech.octopusdragon.musicplayer.tools.OnSetProperty;
import tech.octopusdragon.musicplayer.tools.Rate;
import tech.octopusdragon.musicplayer.tools.Theme;
import tech.octopusdragon.musicplayer.tools.UISize;
import tech.octopusdragon.musicplayer.tools.Userdata;
import tech.octopusdragon.musicplayer.tools.View;
import tech.octopusdragon.musicplayer.util.Resource;
import tech.octopusdragon.musicplayer.window.LibraryLocationDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * The music player application is a media player for audio files. It allows
 * the user to navigate through folders and play collections of audio.
 * @author Alex Gill
 *
 */
public class MusicPlayerApplication extends Application {
	
	// --- Constants ---
	private static final double DEFAULT_WIDTH = 600.0;	// Default window width
	private static final double DEFAULT_HEIGHT = 400.0;	// Default window height
	// Path to root fxml file
	private static final String ROOT_FXML_PATH = "/view/Root.fxml";
	// The default root folder to load music from
	private static final String DEFAULT_ROOT_FOLDER_PATH = System.getProperty("user.home");

	// --- GUI components ---
	private static Stage primaryStage;	// Primary stage
	private static Scene scene;			// Scene
	private static Parent root;			// Root node
	
	// --- Directory components ---
	// The current root folder to load music from
	private static String rootFolderPath;
	// The root directory
	private static ObjectProperty<Directory> currentRootDirectory;
	
	// --- Music Player components ---
	// The music player itself
	private static MusicPlayer musicPlayer;
	// List of all root directories
	private static ObservableList<Directory> allRootDirectories;
	// Enum of particular rate value
	private static ObjectProperty<Rate> rateEnumProperty;
	// Enum of particular balance value
	private static ObjectProperty<Balance> balanceEnumProperty;
	
	// --- History components ---
	// Contains directory history
	private static ObservableList<Directory> history;
	// Contains whether the corresponding history is a track view 
	private static ArrayList<Boolean> historyTrackView;
	// The index of the current directory in the directory history
	private static IntegerProperty historyIndexProperty;
	
	// --- Other components ---
	// Property representing the state of the navigator view
	private static OnSetProperty<NavigatorView> navigatorViewProperty;
	// Property representing whether application is in the middle of reloading
	private static BooleanProperty reloadingProperty;
	// Current view
	private static ObjectProperty<View> currentViewProperty;
	// Current UI size
	private static UISize curUISize;
	// Current theme so that stylesheet path can be removed if changed
	private static Theme curTheme;
	// A string property showing the currently loading song, etc. so that other
	// components may listen to it
	private static StringProperty updateMessageProperty;
	// The progress (0.0 to 1.0) of library reload
	private static DoubleProperty reloadProgressProperty;
	
	@Override
	public void init() {
		
		// Create player
		musicPlayer = new MusicPlayer();
		
		// Read initial music player properties
		musicPlayer.setMute(Boolean.parseBoolean(
				Userdata.readProperty("mute", String.valueOf(false))));
		musicPlayer.muteProperty().addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("mute", String.valueOf(newVal));
		});
		
		musicPlayer.setShuffle(Boolean.parseBoolean(
				Userdata.readProperty("shuffle", String.valueOf(false))));;
		musicPlayer.shuffleProperty().addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("shuffle", String.valueOf(newVal));
		});
		
		musicPlayer.setRepeat(Boolean.parseBoolean(
				Userdata.readProperty("repeat", String.valueOf(false))));
		musicPlayer.repeatProperty().addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("repeat", String.valueOf(newVal));
		});
		musicPlayer.setRepeatSingle(Boolean.parseBoolean(
				Userdata.readProperty("repeat-single", String.valueOf(false))));
		musicPlayer.repeatSingleProperty().addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("repeat-single", String.valueOf(newVal));
		});
		
		musicPlayer.setVolume(Double.parseDouble(
				Userdata.readProperty("volume", String.valueOf(0.8))));
		musicPlayer.volumeProperty().addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("volume", String.valueOf(newVal));
		});
		
		rateEnumProperty = new SimpleObjectProperty<Rate>(Rate.valueOf
				(Userdata.readProperty("rate-enum", Rate.NORMAL.name())));
		musicPlayer.rateProperty().bind(Bindings.createDoubleBinding(() -> {
			double rate = 1.0;
			switch (rateEnumProperty.get()) {
			case QUARTER:
				rate = 0.25;
				break;
			case HALF:
				rate = 0.5;
				break;
			case NORMAL:
				rate = 1.0;
				break;
			case DOUBLE:
				rate = 2.0;
				break;
			case QUADRUPLE:
				rate = 4.0;
				break;
			}
			return rate;
		}, rateEnumProperty));
		rateEnumProperty.addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("rate-enum", newVal.name());
		});
		
		balanceEnumProperty = new SimpleObjectProperty<Balance>(Balance.valueOf(
				Userdata.readProperty("balance-enum", Balance.CENTER.name())));
		musicPlayer.balanceProperty().bind(Bindings.createDoubleBinding(() -> {
			double balance = 0.0;
			switch (balanceEnumProperty.get()) {
			case FULL_LEFT:
				balance = -1.0;
				break;
			case HALF_LEFT:
				balance = -0.5;
				break;
			case CENTER:
				balance = 0.0;
				break;
			case HALF_RIGHT:
				balance = 0.5;
				break;
			case FULL_RIGHT:
				balance = 1.0;
				break;
			}
			return balance;
		}, balanceEnumProperty));
		balanceEnumProperty.addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("balance-enum", newVal.name());
		});
		
		
		// Initialize history
		history = FXCollections.observableArrayList();
		historyTrackView = new ArrayList<Boolean>();
		historyIndexProperty = new SimpleIntegerProperty(-1);
		
		
		// Initialize view
		currentViewProperty = new SimpleObjectProperty<View>();
		currentViewProperty.set(View.valueOf(Userdata.readProperty("view", View.EXPLORER.name())));
		
		
		// Initialize UI Size
		curUISize = UISize.valueOf(Userdata.readProperty("ui-size", UISize.MEDIUM.name()));
		
		
		// Initialize theme
		curTheme =  Theme.valueOf(Userdata.readProperty("theme", Theme.BLUE.name()));
		
		
		// Initialize other components
		currentRootDirectory = new SimpleObjectProperty<Directory>();
		navigatorViewProperty = new OnSetProperty<NavigatorView>(
				NavigatorView.valueOf(Userdata.readProperty(
						"last-directory-navigator-view",
						String.valueOf(NavigatorView.DIRECTORY))));
		navigatorViewProperty.addListener((observable, oldValue, newValue) -> {
			Userdata.writeProperty(
					"last-directory-navigator-view",
					String.valueOf(newValue));
		});
		reloadingProperty = new SimpleBooleanProperty(false);
		updateMessageProperty = new SimpleStringProperty();
		reloadProgressProperty = new SimpleDoubleProperty(0.0);
	}
	
	@Override
	public void start(Stage primaryStage) {
		MusicPlayerApplication.primaryStage = primaryStage;
		
		
		// Initialize root directories
		allRootDirectories = FXCollections.observableArrayList();
		
		
		//  Initialize root folder. If no userdata is found or fetching data
		// results in an error, have the user select root folder
		try {
			rootFolderPath = Userdata.getRootDirectories().
					getFolderDirectory().getPath();
			
			// Go to initial directory
			Platform.runLater(() -> {
				allRootDirectories.addAll(
						Userdata.getRootDirectories().getAllDirectories());
				Directory directory = Userdata.getLastDirectory();
				
				if (directory != null) {
					switch (navigatorViewProperty.get()) {
					case DIRECTORY:
						goForwardToDirectory(directory);
						break;
					case TRACK:
						goForwardToTracks(directory);
						break;
					}
				} else {
					newRootDirectory(Userdata.
							getRootDirectories().defaultDirectory());
				}
			});
		}
		// If it is the first time opening the application...
		catch (NullPointerException e) {
			rootFolderPath = DEFAULT_ROOT_FOLDER_PATH;
			Optional<String> result = new LibraryLocationDialog(rootFolderPath).showAndWait();
			if (result == null || !result.isPresent()) {
				System.exit(0);
			}
			else {
				rootFolderPath = result.get();
				reload();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			rootFolderPath = DEFAULT_ROOT_FOLDER_PATH;
			selectRootFolder();
		}

		// Load fonts
		Font.loadFont(Resource.JUA.getResource().toExternalForm(), 0);
		Font.loadFont(Resource.KOSUGI_MARU.getResource().toExternalForm(), 0);
		Font.loadFont(Resource.VARELA_ROUND.getResource().toExternalForm(), 0);
		
		// Get the root node for the scene from an FXML file
		root = getFXMLRoot(ROOT_FXML_PATH);
		
		// Set the stage
		scene = new Scene(root);
		scene.getStylesheets().add(Resource.MAIN_STYLESHEET.getResource().toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Music Player");
		primaryStage.getIcons().add(new Image(Resource.ICON.getResourceAsStream()));
		
		// Set the initial UI size
		setUISize(curUISize);
		
		// Set the initial theme
		setTheme(curTheme);
		
		// Read properties relating to the application window.
		// Write these properties upon change.
		primaryStage.setWidth(Double.parseDouble(
				Userdata.readProperty("window-width",
						String.valueOf(DEFAULT_WIDTH))));
		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (!primaryStage.isMaximized()) {
				Userdata.writeProperty("window-width", String.valueOf(newVal));
			}
		});
		
		primaryStage.setHeight(Double.parseDouble(
				Userdata.readProperty("window-height",
						String.valueOf(DEFAULT_HEIGHT))));
		primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
			if (!primaryStage.isMaximized()) {
				Userdata.writeProperty("window-height", String.valueOf(newVal));
			}
		});
		
		if (Userdata.readProperty("window-x") != null) {
			primaryStage.setX(Double.parseDouble(
					Userdata.readProperty("window-x")));
		}
		primaryStage.xProperty().addListener((obs, oldVal, newVal) -> {
			if (!primaryStage.isMaximized()) {
				Userdata.writeProperty("window-x", String.valueOf(newVal));
				// X is set before the window is maximized, so create an old
				// value that will be restored if the window is maximized
				Userdata.writeProperty("old-window-x", String.valueOf(oldVal));
			}
		});
		
		if (Userdata.readProperty("window-y") != null) {
			primaryStage.setY(Double.parseDouble(
					Userdata.readProperty("window-y")));
		}
		primaryStage.yProperty().addListener((obs, oldVal, newVal) -> {
			if (!primaryStage.isMaximized()) {
				Userdata.writeProperty("window-y", String.valueOf(newVal));
				// Y is set before the window is maximized, so create an old
				// value that will be restored if the window is maximized
				Userdata.writeProperty("old-window-y", String.valueOf(oldVal));
			}
		});
		
		primaryStage.setMaximized(Boolean.parseBoolean(
				Userdata.readProperty("maximized",
						String.valueOf(false))));
		primaryStage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
			Userdata.writeProperty("maximized", String.valueOf(newVal));
			// Restore old X and Y values
			if (newVal == true) {
				Userdata.writeProperty("window-x",
						Userdata.readProperty("old-window-x"));
				Userdata.writeProperty("window-y",
						Userdata.readProperty("old-window-y"));
			}
		});
		
		
		// Show the stage
		primaryStage.show();
		
		
		// Set the minimum height and width of the window
		double decorationHeight = primaryStage.getHeight() - scene.getHeight();
		double decorationWidth = primaryStage.getWidth() - scene.getWidth();
		primaryStage.setMinWidth(root.minWidth(-1) + decorationWidth);
		primaryStage.setMinHeight(root.minHeight(-1) + decorationHeight);
	}
	
	
	
	@Override
	public void stop() {
		// Save userdata one more time on close
		Userdata.saveProperties(true);
		Userdata.saveLastDirectory(true);
		Userdata.saveMusicLibrary(true);
	}
	
	
	
	/**
	 * Loads and returns the root node of an FXML file
	 * @param fxmlPath The path to the FXML file
	 * @return The root node of the FXML file
	 */
	private static Parent getFXMLRoot(String fxmlPath) {
		Parent root = null;
		try {
			root = FXMLLoader.load(
					MusicPlayerApplication.class.getResource(fxmlPath));
		} catch (IOException e) {
			System.out.println("ERROR: Unable to load FXML");
			e.printStackTrace();
		}
		return root;
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	/**
	 * Changes the root directory
	 * @param directory The new root directory
	 */
	public static void newRootDirectory(Directory directory) {
		// If the root directory is the song directory, go forward to tracks
		if (directory.getClass() == SongDirectory.class) {
			goForwardToTracks(directory);
		}
		// Otherwise, go forward to directory
		else {
			goForwardToDirectory(directory);
		}
		currentRootDirectory.set(directory);
	}
	
	
	/**
	 * Changes the folder from which all directories and songs are loaded.
	 */
	public static void selectRootFolder() {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle("Choose root music directory");
		File rootFolder = new File(rootFolderPath);
		if (!rootFolder.exists()) {
			rootFolder = new File(DEFAULT_ROOT_FOLDER_PATH);
		}
		folderChooser.setInitialDirectory(rootFolder);
		File selectedFile = folderChooser.showDialog(primaryStage);
		if (selectedFile == null) return;
		rootFolderPath = selectedFile.getPath();
		reload();
	}
	
	
	/**
	 * Filters the list of songs by the given text
	 * @param text The text to search
	 */
	public static void search(String text) {
		musicPlayer.getFilteredDirSongs().setPredicate(song ->
			song.getTitle().toLowerCase().contains(text.toLowerCase())
		);
	}
	
	
	/**
	 * Switches to the song directory and filters the list of songs by the given
	 * text
	 * @param text The text to search
	 */
	public static void searchAndGo(String text) {
		search(text);
		newRootDirectory(Userdata.getRootDirectories().getSongDirectory());
	}
	
	
	/**
	 * Switches the view in the UI to display new information
	 * @param view The new view to show
	 */
	public static void switchView(View view) {
		
		// Save the property
		Userdata.writeProperty("view", view.name());
		
		// Set the new view as the current view
		currentViewProperty.set(view);
	}
	
	
	/**
	 * Sets the current theme
	 * @param theme The theme to set
	 */
	public static void setTheme(Theme theme) {
		// Remove old theme stylesheet
		scene.getStylesheets().remove(curTheme.getResource().toExternalForm());
		 
		// Add new theme stylesheet
		scene.getStylesheets().add(theme.getResource().toExternalForm());
		
		// Save the property
		Userdata.writeProperty("theme", theme.name());
		
		// Set new theme as current theme
		curTheme = theme;
	}
	
	
	/**
	 * Sets the current UI size
	 * @param uiSize The UI size
	 */
	public static void setUISize(UISize uiSize) {
		// Remove old UI size stylesheet
		scene.getStylesheets().remove(curUISize.getResource().toExternalForm());
		
		// Add new UI size stylesheet
		scene.getStylesheets().add(uiSize.getResource().toExternalForm());
		
		// Save the property
		Userdata.writeProperty("ui-size", uiSize.name());
		
		// Set new UI size as the current UI size
		curUISize = uiSize;
		
		
		// Set the minimum height and width of the window
		primaryStage.minWidthProperty().unbind();
		primaryStage.minHeightProperty().unbind();
		double decorationHeight = primaryStage.getHeight() - scene.getHeight();
		double decorationWidth = primaryStage.getWidth() - scene.getWidth();
		root.applyCss();
		primaryStage.setMinWidth(root.minWidth(-1) + decorationWidth);
		primaryStage.setMinHeight(root.minHeight(-1) + decorationHeight);
	}
	
	
	/**
	 * Rereads and reloads the user's music library in a different thread so
	 * that the user can continue using the app
	 */
	public static void reload() {
		Task<Void> reloadTask = new Task<Void>() {
			@Override
			protected Void call() {
				// Don't do anything if already reloading
				if (reloadingProperty.get()) return null;

				reloadingProperty.set(true);
				
				Userdata.scanMusicLibrary(rootFolderPath);
				
				Platform.runLater(() -> {
					allRootDirectories.clear();
					allRootDirectories.addAll(
							Userdata.getRootDirectories().getAllDirectories());
				});
				
				reloadingProperty.set(false);
				
				// Go to initial directory
				Platform.runLater(() -> {
					history.clear();
					historyTrackView.clear();
					historyIndexProperty.set(-1);
					newRootDirectory(Userdata.getRootDirectories().getAlbumDirectory());
				});
				
				return null;
			}
		};
		Thread reloadThread = new Thread(reloadTask);
		reloadThread.start();
	}
	
	
	/**
	 * Navigates backwards in the directory history
	 */
	public static void back() {
		
		// Decrement the index
		historyIndexProperty.set(historyIndexProperty.get() - 1);
		
		// Get the directory at the new index
		Directory newDirectory = history.get(historyIndexProperty.get());
		
		// Show the appropriate view
		if (historyTrackView.get(historyIndexProperty.get())) {
			goToTracks(newDirectory);
		}
		else {
			goToDirectory(newDirectory);
		}
	}
	
	
	/**
	 * Navigates forwards in the directory history
	 */
	public static void forward() {
		
		// Increment the index
		historyIndexProperty.set(historyIndexProperty.get() + 1);
		
		// Get the directory at the new index
		Directory newDirectory = history.get(historyIndexProperty.get());
		
		// Show the appropriate view
		if (historyTrackView.get(historyIndexProperty.get())) {
			goToTracks(newDirectory);
		}
		else {
			goToDirectory(newDirectory);
		}
	}
	
	
	/**
	 * Goes to the given folder as either a directory or tracks list, depending
	 * on which is more appropriate, and updates the history
	 * @param directory
	 */
	public static void goForward(Directory directory) {
		// If the directory has media and no more directories, set
		// change to track view on click
		if (directory.hasSongs() && (!directory.hasDirectories())) {
			goForwardToTracks(directory);
		}
		// If the directory has more directories, and there is some
		// media in it or one of its children, set new directory on
		// click
		else {
			goForwardToDirectory(directory);
		}
	}
	
	
	/**
	 * Goes to the given folder as a directory.
	 * @param directory The directory
	 */
	public static void goToDirectory(Directory directory) {
		musicPlayer.newDirectory(directory, true);
		navigatorViewProperty.set(NavigatorView.DIRECTORY);
		
		// Update root directory
		currentRootDirectory.set(directory.getRoot());
		
		// Save directory information
		Userdata.setLastDirectory(directory);
	}
	
	
	/**
	 * Goes to the given folder as a track list.
	 * @param directory The directory
	 */
	public static void goToTracks(Directory directory) {
		musicPlayer.newDirectory(directory, false);
		navigatorViewProperty.set(NavigatorView.TRACK);
		
		// Update root directory
		currentRootDirectory.set(directory.getRoot());
		
		// Save directory information
		Userdata.setLastDirectory(directory);
	}
	
	
	/**
	 * Goes to the given folder as a directory and updates the history.
	 * @param directory The directory
	 */
	public static void goForwardToDirectory(Directory directory) {
		if (musicPlayer.getCurDirectory().get() == directory)
			return;
		
		goToDirectory(directory);
		if (historyIndexProperty.get() == history.size() - 1) {
		}
		else {
			while (history.size() - 1 > historyIndexProperty.get()) {
				history.remove(history.size() - 1);
				historyTrackView.remove(historyTrackView.size() - 1);
			}
		}
		history.add(directory);
		historyTrackView.add(false);
		historyIndexProperty.set(history.size() - 1);
	}
	
	
	/**
	 * Goes to the given folder as a track list and updates the history.
	 * @param directory The directory
	 */
	public static void goForwardToTracks(Directory directory) {
		if (musicPlayer.getCurDirectory().get() == directory)
			return;
		
		goToTracks(directory);
		if (historyIndexProperty.get() == history.size() - 1) {
		}
		else {
			while (history.size() - 1 > historyIndexProperty.get()) {
				history.remove(history.size() - 1);
				historyTrackView.remove(historyTrackView.size() - 1);
			}
		}
		history.add(directory);
		historyTrackView.add(true);
		historyIndexProperty.set(history.size() - 1);
	}
	
	
	
	// --- Accessors and mutators ---
	
	
	/**
	 * @return The music player
	 */
	public static MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}
	
	
	/**
	 * @return An observable list of the root directories
	 */
	public static ObservableList<Directory> getRootDirectories() {
		return allRootDirectories;
	}
	
	
	/**
	 * @return The current root directory property
	 */
	public static ObjectProperty<Directory> currentRootDirectoryProperty() {
		return currentRootDirectory;
	}
	
	
	/**
	 * @return The root directory
	 */
	public static Directory getCurrentRootDirectory() {
		return currentRootDirectory.get();
	}
	
	
	/**
	 * @return A list of a history of the previously viewed directories
	 */
	public static ObservableList<Directory> getHistory() {
		return history;
	}
	
	
	/**
	 * @return The observable property of index of the directory history
	 */
	public static IntegerProperty historyIndexProperty() {
		return historyIndexProperty;
	}
	
	
	/**
	 * @return The index of the directory history
	 */
	public static int getHistoryIndex() {
		return historyIndexProperty.get();
	}
	
	
	/**
	 * @return The current UI size
	 */
	public static UISize getCurUISize() {
		return curUISize;
	}
	
	
	/**
	 * @return The current theme
	 */
	public static Theme getCurTheme() {
		return curTheme;
	}
	
	
	/**
	 * @return The current speed of the audio playback
	 */
	public static Rate getRateEnum() {
		return rateEnumProperty.get();
	}
	
	
	/**
	 * Sets the current speed of the audio playback
	 * @param rate The rate to set
	 */
	public static void setRateEnum(Rate rate) {
		rateEnumProperty.set(rate);
	}
	
	
	/**
	 * @return The rate enum property
	 */
	public static ObjectProperty<Rate> rateEnumProperty() {
		return rateEnumProperty;
	}
	
	
	/**
	 * @return The current left/right balance of the music player
	 */
	public static Balance getBalanceEnum() {
		return balanceEnumProperty.get();
	}
	
	
	/**
	 * Sets the current left/right balance of the music player
	 * @param balance The balance to set
	 */
	public static void setBalanceEnum(Balance balance) {
		balanceEnumProperty.set(balance);
	}
	
	
	/**
	 * @return The balance enum property
	 */
	public static ObjectProperty<Balance> balanceEnumProperty() {
		return balanceEnumProperty;
	}
	
	
	/**
	 * @return The current navigator view
	 */
	public static NavigatorView getNavigatorView() {
		return navigatorViewProperty.get();
	}
	
	
	/**
	 * @return The navigator view property
	 */
	public static OnSetProperty<NavigatorView> navigatorViewProperty() {
		return navigatorViewProperty;
	}
	
	
	/**
	 * @return Whether the music library is loading
	 */
	public static boolean isReloading() {
		return reloadingProperty.get();
	}
	
	
	/**
	 * @return The reloading property
	 */
	public static BooleanProperty reloadingProperty() {
		return reloadingProperty;
	}
	
	
	/**
	 * @return The current view of the UI
	 */
	public static View getCurrentView() {
		return currentViewProperty.get();
	}
	
	
	/**
	 * @return The current view property
	 */
	public static ObjectProperty<View> currentViewProperty() {
		return currentViewProperty;
	}


	/**
	 * @return the currently loading song property
	 */
	public static final StringProperty updateMessageProperty() {
		return updateMessageProperty;
	}
	

	/**
	 * @return the value of the currently loading song property
	 */
	public static final String getUpdateMessage() {
		return updateMessageProperty().get();
	}
	
	/**
	 * Sets the value of the currently loading song property
	 * @param currentlyLoadingSong The value to set
	 */
	public static final void setUpdateMessage(
			final String currentlyLoadingSong) {
		updateMessageProperty().set(currentlyLoadingSong);
	}


	/**
	 * @return The reload progress property
	 */
	public static final DoubleProperty reloadProgressProperty() {
		return reloadProgressProperty;
	}

	/**
	 * @return The progress of the reload
	 */
	public static final double getReloadProgressProperty() {
		return reloadProgressProperty.get();
	}

	/**
	 * Sets the progress of the reload
	 * @param val The progress (0.0 to 1.0)
	 */
	public static final void setReloadProgressProperty(double val) {
		reloadProgressProperty.set(val);
	}

}
