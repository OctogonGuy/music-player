package tech.octopusdragon.musicplayer.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.model.Album;
import tech.octopusdragon.musicplayer.model.AlbumCollection;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.model.RootDirectories;
import tech.octopusdragon.musicplayer.model.Song;
import tech.octopusdragon.musicplayer.util.Util;

import javafx.scene.image.Image;

/**
 * Data saved so that the application can load faster and remember user settings
 * @author Alex Gill
 *
 */
public class Userdata {
	
	// --- Constants ---
	// Directory of userdata file
	private static final String USERDATA_DIRECTORY = "userdata/music_player_userdata";
	// Directory of album collections
	private static final String ALBUM_COLLECTION_DIRECTORY = "userdata/Album Collections";
	// Album collection file extention
	private static final String ALBUM_COLLECTION_EXTENTION = ".alco";
	// The interval after which it is okay to save userdata to file again
	private static final int WAIT_INTERVAL = 500;
	
	// --- Serializable components filenames ---
	private static final String MUSIC_LIBRARY_FILENAME = "music_library.ser";
	private static final String PROPERTIES_FILENAME = "userdata.ser";
	private static final String LAST_DIRECTORY_FILENAME = "last_directory.ser";
	
	// --- Components of the userdata ---
	// The user's music library data
	private static RootDirectories musicLibrary;
	// Values of music player and application window
	private static Properties properties;
	// The last directory the user visited
	private static Directory lastDirectory;
	
	// --- On load components ---
	// Saved lists of loaded directory nodes it only has to be loaded once
	private static Map<Album, Image> coverArtCache;
	
	// --- Other variables ---
	// Whether to wait to save to file so that the file is not accessed so much
	private static boolean wait;
	
	
	static {
		musicLibrary = loadMusicLibrary();
		properties = loadProperties();
		lastDirectory = loadLastDirectory();
		coverArtCache = new HashMap<>();
		if (musicLibrary != null) {
			new Thread(() -> {
				for (Album album : musicLibrary.getAlbums()) {
					if (album.getCoverArtData() == null) continue;
					coverArtCache.put(album, new Image(new ByteArrayInputStream(album.getCoverArtData())));
				}
			}).start();
		}
	}
	

	/**
	 * Tries to load userdata from the userdata file. If successful, it returns
	 * a new object with the userdata loaded. If unsuccessful, it returns an
	 * empty userdata object
	 * @return The music library
	 */
	private static RootDirectories loadMusicLibrary() {
		RootDirectories object = null;
		try {
			FileInputStream fileIn = new FileInputStream(
					USERDATA_DIRECTORY + "/" + MUSIC_LIBRARY_FILENAME);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			object = (RootDirectories) objectIn.readObject();
			objectIn.close();
		} catch (IOException | ClassNotFoundException e) {
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
		}
		
		return object;
	}
	

	/**
	 * Tries to load userdata from the userdata file. If successful, it returns
	 * a new object with the userdata loaded. If unsuccessful, it returns an
	 * empty userdata object
	 * @return The properties
	 */
	private static Properties loadProperties() {
		Properties object;
		try {
			FileInputStream fileIn = new FileInputStream(
					USERDATA_DIRECTORY + "/" + PROPERTIES_FILENAME);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			object = (Properties) objectIn.readObject();
			objectIn.close();
		} catch (IOException | ClassNotFoundException e) {
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
			object = new Properties();
		}
		
		return object;
	}
	

	/**
	 * Tries to load userdata from the userdata file. If successful, it returns
	 * a new object with the userdata loaded. If unsuccessful, it returns an
	 * empty userdata object
	 * @return The last open directory
	 */
	private static Directory loadLastDirectory() {
		Directory object = null;
		try {
			FileInputStream fileIn = new FileInputStream(
					USERDATA_DIRECTORY + "/" + LAST_DIRECTORY_FILENAME);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			object = (Directory) objectIn.readObject();
			objectIn.close();
		} catch (IOException | ClassNotFoundException e) {
			if (!(e instanceof FileNotFoundException)) {
				e.printStackTrace();
			}
		}
		
		// Use music library's corresponding object so that the object reference
		// is the same
		if (musicLibrary != null) {
			Directory[] allDirectories = musicLibrary.getAllDirectories();
			for (Directory directory : allDirectories) {
				if (directory.equals(object)) {
					object = directory;
					break;
				}
			}
		}
		
		return object;
	}
	
	
	/**
	 * Saves the music library
	 * @param bypassWait Whether to run immediately regardless of whether there
	 * is a wait timer
	 */
	public static void saveMusicLibrary(boolean bypassWait) {
		if (wait == true && !bypassWait) return;
		
		new Thread() { public void run() {
			
			// Make sure the folder exists
			File folder = new File(USERDATA_DIRECTORY);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			// Write the userdata to the folder
			try {
				FileOutputStream fileOut = new FileOutputStream(
						USERDATA_DIRECTORY + "/" + MUSIC_LIBRARY_FILENAME);
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
				objectOut.writeObject(musicLibrary);
				objectOut.close();
			} catch (IOException e) {
				System.out.println("Error trying to save userdata");
				e.printStackTrace();
			}
		
		}}.start();
		
		// Start wait countdown
		wait = true;
		new Timer(true).schedule(new TimerTask() { @Override public void run() {
			wait = false;
		}}, WAIT_INTERVAL);
	}
	
	
	/**
	 * Saves the properties
	 * @param bypassWait Whether to run immediately regardless of whether there
	 * is a wait timer
	 */
	public static void saveProperties(boolean bypassWait) {
		if (wait == true && !bypassWait) return;
		
		new Thread() { public void run() {
			
			// Make sure the folder exists
			File folder = new File(USERDATA_DIRECTORY);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			// Write the userdata to the folder
			try {
				FileOutputStream fileOut = new FileOutputStream(
						USERDATA_DIRECTORY + "/" + PROPERTIES_FILENAME);
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
				objectOut.writeObject(properties);
				objectOut.close();
			} catch (IOException e) {
				System.out.println("Error trying to save userdata");
				e.printStackTrace();
			}
		
		}}.start();
		
		// Start wait countdown
		wait = true;
		new Timer(true).schedule(new TimerTask() { @Override public void run() {
			wait = false;
		}}, WAIT_INTERVAL);
	}
	
	
	/**
	 * Saves the last visited directory
	 * @param bypassWait Whether to run immediately regardless of whether there
	 * is a wait timer
	 */
	public static void saveLastDirectory(boolean bypassWait) {
		if (wait == true && !bypassWait) return;
		
		new Thread() { public void run() {
			
			// Make sure the folder exists
			File folder = new File(USERDATA_DIRECTORY);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			
			// Write the userdata to the folder
			try {
				FileOutputStream fileOut = new FileOutputStream(
						USERDATA_DIRECTORY + "/" + LAST_DIRECTORY_FILENAME);
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
				objectOut.writeObject(lastDirectory);
				objectOut.close();
			} catch (IOException e) {
				System.out.println("Error trying to save userdata");
				e.printStackTrace();
			}
		
		}}.start();
		
		// Start wait countdown
		wait = true;
		new Timer(true).schedule(new TimerTask() { @Override public void run() {
			wait = false;
		}}, WAIT_INTERVAL);
	}
	
	
	/**
	 * Loads and returns all album collections from the album collection directory
	 * @return The album collections
	 */
	public static List<AlbumCollection> loadAllAlbumCollections() {
		// Create a list of album collections
		List<AlbumCollection> albumCollectionList = new ArrayList<AlbumCollection>();
		
		// Get all files in the directory and add only album collection files to
		// the album collection list
		File albumCollectionDirectory = new File(ALBUM_COLLECTION_DIRECTORY);
		if (!albumCollectionDirectory.exists()) return albumCollectionList;
		File[] files = albumCollectionDirectory.listFiles();
		for (File file : files) {
			if (Util.getFileExtension(file).equals(ALBUM_COLLECTION_EXTENTION)) {
				AlbumCollection newAlbumCollection = loadAlbumCollection(file);
				albumCollectionList.add(newAlbumCollection);
			}
		}
		
		// Return the album collections
		return albumCollectionList;
	}
	
	
	/**
	 * Loads and returns an album collection from the album collection directory
	 * @param filename The filename of the album collection
	 * @return The album collection
	 */
	private static AlbumCollection loadAlbumCollection(File file) {
		// Create the album collection
		String albumCollectionName =
				file.getName().replace(Util.getFileExtension(file), "");
		AlbumCollection albumCollection = new AlbumCollection(albumCollectionName,
				musicLibrary.getAlbumCollectionDirectory());
		
		// Add the albums of each song in the file
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Problem reading album collection file");
			e.printStackTrace();
		}
		Scanner inputFile = new Scanner(is, "UTF-8");
		// Read every non-comment (#) line
		String line;
		while (inputFile.hasNext()) {
			line = inputFile.nextLine();
			if (line.startsWith("#")) continue;
			// Remove prefix
			if (line.startsWith("file:///")) {
				line = line.substring(8);
			}
			// Unescape HTML URL encoding
			try {
				line = URLDecoder.decode(line, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				System.out.println("ERROR URL decoding line: " + line);
				e.printStackTrace();
			}
			
			// Create the song so that the album can be retrieved.
			Song song = new Song(line);
			
			// Create an album and set the parent to the album collection if the
			// album does not already exist in the collection
			for (Directory directory : musicLibrary.
					getAlbumDirectory().getDirectories()) {
				Album album = (Album)directory;
				if (album.getSongs().contains(song)) {
					Album albumCopy = new Album(album);
					albumCopy.setParent(albumCollection);
					if (!albumCollection.getDirectories().contains(album))
						albumCollection.addDirectory(albumCopy);
				}
			}
		}
		// Close the input file
		inputFile.close();
		
		// Return the album collection
		return albumCollection;
	}
	
	
	/**
	 * Saves all album collections in the userdata to a .alco file in the album
	 * collection file directory
	 */
	public static void saveAllAlbumCollections() {
		// Make sure the album collection folder exists
		File folder = new File(ALBUM_COLLECTION_DIRECTORY);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		for (Directory albumCollection : musicLibrary.
				getAlbumCollectionDirectory().getDirectories()) {
			saveAlbumCollection((AlbumCollection)albumCollection);
		}
	}
	
	
	/**
	 * Writes an album collection file. This is done by looking at each album in
	 * an album collection and writing the path to the first song. Then saves the
	 * file as a .alco file.
	 * @param albumCollection The album collection to write to the file.
	 */
	public static void saveAlbumCollection(AlbumCollection albumCollection) {
		// Open file for writing
		OutputStream os = null;
		try {
			os = new FileOutputStream(ALBUM_COLLECTION_DIRECTORY + "/" +
					albumCollection.getName() + ALBUM_COLLECTION_EXTENTION);
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening album collection file");
			e.printStackTrace();
		}
		OutputStreamWriter osWriter = null;
		try {
			osWriter = new OutputStreamWriter(os, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("Problem opening album collection file for UTF-8");
			e.printStackTrace();
		}
		PrintWriter outputFile = new PrintWriter(osWriter);
		
		// Write the path of each first song of each album in the album collection
		for (Directory album : albumCollection.getDirectories()) {
			outputFile.println(album.getSongs().get(0).getPath());
		}
		// Close the output file
		outputFile.close();
	}
	
	
	/**
	 * Deletes an album collection from the album collection directory as well as
	 * its file in the album collection folder
	 * @param albumCollection The album collection to delete
	 */
	public static void deleteAlbumCollection(AlbumCollection albumCollection) {
		// Delete from the collection directory
		musicLibrary.getAlbumCollectionDirectory().getDirectories().
				remove(albumCollection);
		
		// Delete from file system
		File albumCollectionFile = new File(ALBUM_COLLECTION_DIRECTORY + "/" +
				albumCollection.getName() + ALBUM_COLLECTION_EXTENTION);
		albumCollectionFile.delete();
		
		// Save
		saveAllAlbumCollections();
	}
	
	
	/**
	 * Renames an album collection
	 * @param albumCollection The album collection
	 * @param name The new name
	 */
	public static void renameAlbumCollection(AlbumCollection albumCollection, String name) {
		String oldName = albumCollection.getName();
		String newName = name;
		
		// Rename file in file system
		File albumCollectionFile = new File(ALBUM_COLLECTION_DIRECTORY + "/" +
				albumCollection.getName() + ALBUM_COLLECTION_EXTENTION);
		boolean succeeded = albumCollectionFile.renameTo(
				new File(albumCollectionFile.getPath().replace(oldName, newName)));
		if (!succeeded) return;
		
		// Change the name in the application only if the file renaming succeeds
		albumCollection.setName(name);
		
		// Save
		saveAlbumCollection(albumCollection);
	}
	
	
	/**
	 * Reads and returns a property value
	 * @param key The property key
	 * @return The property value or null if not found
	 */
	public static String readProperty(String key) {
		return properties.getProperty(key);
	}
	
	
	/**
	 * Reads and returns a property value or a default value if not found
	 * @param key The property key
	 * @param defaultValue A property value to write if the file is not found or
	 * 	the property is not found
	 * @return The property value or a default value if not found
	 */
	public static String readProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	
	/**
	 * Writes a property
	 * @param key The property key
	 * @param value The property value
	 */
	public static void writeProperty(String key, String value) {
		properties.setProperty(key, value);
		
		saveProperties(false);
	}
	
	
	/**
	 * Reads and saves the user's music library
	 * @param The directory of the user's music library
	 */
	public static void scanMusicLibrary(String directoryPath) {
			musicLibrary = new RootDirectories();
			musicLibrary.pullData(directoryPath);

			MusicPlayerApplication.setUpdateMessage("Loading album cover art");
			for (Album album : musicLibrary.getAlbums()) {
				if (album.getCoverArtData() == null) continue;
				coverArtCache.put(album, new Image(new ByteArrayInputStream(album.getCoverArtData())));
			}
			MusicPlayerApplication.setUpdateMessage(null);
			
			saveMusicLibrary(false);
	}


	/**
	 * @return The root directories of the user's music library
	 */
	public static RootDirectories getRootDirectories() {
		return musicLibrary;
	}
	
	
	/**
	 * Sets the last directory the user visited
	 * @param dir the last directory the user visited
	 */
	public static void setLastDirectory(Directory dir) {
		lastDirectory = dir;
		
		saveLastDirectory(false);
	}


	/**
	 * @return the last directory the user visited
	 */
	public static Directory getLastDirectory() {
		return lastDirectory;
	}
	
	
	/**
	 * @param album The album
	 * @return Cached data of an album's album cover
	 */
	public static Image getCoverArt(Album album) {
		return coverArtCache.get(album);
	}
	
	
	/**
	 * @param album The album
	 * @return Whether an album has cached data for its album cover
	 */
	public static boolean containsCoverArt(Album album) {
		return coverArtCache.containsKey(album);
	}
}
