package tech.octopusdragon.musicplayer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.tools.Userdata;
import tech.octopusdragon.musicplayer.util.Util;

/**
 * A collection of all types of root directories
 * @author Alex Gill
 *
 */
public class RootDirectories implements Serializable {
	private static final long serialVersionUID = -8507744048197435913L;
	
	// The directory type that will be loaded when no data is present
	private static final DirectoryType DEFAULT_DIRECTORY_TYPE = DirectoryType.ALBUM;
	
	private List<Song> songs;				// List of all songs
	private List<Album> albums;				// List of all albums
	private SongDirectory songDirectory;	// Root song directory
	private Directory albumDirectory;		// Root album directory
	private Directory artistDirectory;		// Root artist directory
	private Directory genreDirectory;		// Root genre directory
	private Folder folderDirectory;			// Root folder directory
	private Directory playlistDirectory;	// Root playlist directory
	private AlbumCollectionDirectory albumCollectionDirectory;	// Root ALCO dir
	
	/**
	 * @return the songs
	 */
	public List<Song> getSongs() {
		return songs;
	}

	/**
	 * @return the albums
	 */
	public List<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * @return the song directory
	 */
	public Directory getSongDirectory() {
		return songDirectory;
	}

	/**
	 * @return the albumDirectory
	 */
	public Directory getAlbumDirectory() {
		return albumDirectory;
	}

	/**
	 * @return the artistDirectory
	 */
	public Directory getArtistDirectory() {
		return artistDirectory;
	}

	/**
	 * @return the genreDirectory
	 */
	public Directory getGenreDirectory() {
		return genreDirectory;
	}

	/**
	 * @return the folderDirectory
	 */
	public Folder getFolderDirectory() {
		return folderDirectory;
	}

	/**
	 * @return the playlistDirectory
	 */
	public Directory getPlaylistDirectory() {
		return playlistDirectory;
	}

	/**
	 * @return the album collection directory
	 */
	public Directory getAlbumCollectionDirectory() {
		return albumCollectionDirectory;
	}
	
	/**
	 * @param type The type of directory
	 * @return The root directory depending on the given type
	 */
	public Directory getDirectory(DirectoryType type) {
		Directory directory;
		switch(type) {
		case FOLDER:
			directory = getFolderDirectory();
			break;
		case ALBUM:
			directory = getAlbumDirectory();
			break;
		case ARTIST:
			directory = getArtistDirectory();
			break;
		case GENRE:
			directory = getGenreDirectory();
			break;
		case PLAYLIST:
			directory = getPlaylistDirectory();
			break;
		case ALBUM_COLLECTION:
			directory = getAlbumCollectionDirectory();
			break;
		default:
			directory = null;
			break;
		}
		return directory;
	}
	
	/**
	 * @return all root directories
	 */
	public Directory[] getAllDirectories() {
		return new Directory[] {
			songDirectory,
			albumDirectory,
			artistDirectory,
			genreDirectory,
			folderDirectory,
			playlistDirectory,
			albumCollectionDirectory
		};
	}
	
	public Directory defaultDirectory() {
		return getDirectory(DEFAULT_DIRECTORY_TYPE);
	}
	
	/**
	 * Assigns all of this object's variables with data pulled from the user's
	 * music directory.
	 * @param directoryPath The path of the directory to pull data from
	 */
	public void pullData(String directoryPath) {
		MusicPlayerApplication.setUpdateMessage("Loading folders");
		folderDirectory = getFolderDirectory(directoryPath);
		
		MusicPlayerApplication.setUpdateMessage("Loading songs");
		songs = folderDirectory.getSongsRecursive();
		
		MusicPlayerApplication.setUpdateMessage("Creating song directory");
		songDirectory = new SongDirectory("Songs");
		songDirectory.addSongs(songs);
		
		MusicPlayerApplication.setUpdateMessage("Loading albums");
		albumDirectory = getAlbumDirectory(songs);
		albums = new ArrayList<Album>();
		for (Directory directory : albumDirectory.getDirectories())
			albums.add((Album) directory);
		
		MusicPlayerApplication.setUpdateMessage("Loading artists");
		artistDirectory = getArtistDirectory(albums);
		
		MusicPlayerApplication.setUpdateMessage("Loading genres");
		genreDirectory = getGenreDirectory(albums);
		
		MusicPlayerApplication.setUpdateMessage("Loading playlists");
		playlistDirectory = getPlaylistDirectory(directoryPath);
		
		MusicPlayerApplication.setUpdateMessage("Loading album collections");
		albumCollectionDirectory = new AlbumCollectionDirectory("Album Collections");
		for (AlbumCollection albumCollection : Userdata.loadAllAlbumCollections()) {
			albumCollectionDirectory.addDirectory(albumCollection);
		}
	
		MusicPlayerApplication.setUpdateMessage(null);
	}
	
	
	
	// --- Methods to get data ---
	
	/**
	 * Returns a list of songs in a folder
	 * @param directory The folder
	 * @param recursive Whether to include songs in sub-folders recursively
	 * @return The list of songs
	 */
	private List<Song> getSongsInFolder(
			File directory, boolean recursive) {
		List<Song> songList = new ArrayList<Song>();
		
		// Load the song paths
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				
				// If the file is a directory and recursion is on, recursively
				// call this method again
				if (recursive && file.isDirectory()) {
					songList.addAll(
							getSongsInFolder(file, recursive));
				}
				
				// If a music file, add it
				else if (Arrays.asList(Util.MUSIC_FILE_EXTENTIONS).contains(Util.getFileExtension(file))) {
					MusicPlayerApplication.setUpdateMessage(file.getPath());
					Song newSong = new Song(file);
					songList.add(newSong);
					MusicPlayerApplication.updateUI(newSong);
				}
			}
		}
		
		return songList;
	}
	
	
	/**
	 * A folder directory
	 * @param rootFolder The folder
	 * @return The folder directory
	 */
	private Folder getFolderDirectory(File rootFolder) {
		Folder directory = new Folder(rootFolder.getPath(), "Folders");
		
		for (Folder folder : getFoldersInFolder(rootFolder, directory)) {
			directory.addDirectory(folder);
		}
		for (Song song : getSongsInFolder(rootFolder, false)) {
			directory.addSong(song);
		}
		
		MusicPlayerApplication.setUpdateMessage(null);
		
		return directory;
	}
	
	
	/**
	 * A folder directory
	 * @param rootFolderPath The path of the folder as a string
	 * @return The folder directory
	 */
	private Folder getFolderDirectory(String rootFolderPath) {
		return getFolderDirectory(new File(rootFolderPath));
	}
	
	
	/**
	 * A list of sub-folders in a folder
	 * @param directory The folder
	 * @param parent The directory to set as the sub-folders' parent
	 * @return The list of folders
	 */
	private List<Folder> getFoldersInFolder(
			File directory, Directory parent) {
		List<Folder> folderList = new ArrayList<Folder>();
		
		
		Folder curFolder;
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					curFolder = new Folder(file.getPath(), file.getName(), parent);
					
					for (Folder folder : getFoldersInFolder(file, curFolder)) {
						curFolder.addDirectory(folder);
					}
					for (Song song : getSongsInFolder(file, false)) {
						curFolder.addSong(song);
					}
					// Sort songs in folder
					curFolder.getSongs().sort(Comparator.naturalOrder());
					
					if (curFolder.hasDirectories() || curFolder.hasSongs()) {
						folderList.add(curFolder);
					}
				}
			}
		}
		
		
		return folderList;
	}

	
	/**
	 * A directory filled with all albums in the user's music library
	 * @param songs A list of songs from which to pull the albums
	 * @return The directory with the albums
	 */
	private Directory getAlbumDirectory(List<Song> songs) {
		Directory directory = new Directory("Albums");
		
		Map<String, Map<String, Album>> albumMap = new HashMap<String, Map<String, Album>>();
		
		for (Song song : songs) {
			
			if (!albumMap.containsKey(song.getAlbumArtist())) {
				albumMap.put(song.getAlbumArtist(), new HashMap<String, Album>());
			}
			if (!albumMap.get(song.getAlbumArtist()).containsKey(song.getAlbumStr())) {
				Album newAlbum = new Album(song.getAlbumStr(), song.getAlbumArtist(), song.getGenre(), song.getYear(), song.coverArtData(), directory);
				albumMap.get(song.getAlbumArtist()).put(song.getAlbumStr(), newAlbum);
			}
			
			albumMap.get(song.getAlbumArtist()).get(song.getAlbumStr()).addSong(song);
			song.setAlbum(albumMap.get(song.getAlbumArtist()).get(song.getAlbumStr()));
		}

		
		// Convert it to a list, sort it, and add the albums to the directory
		List<Album> albumList = new ArrayList<Album>();
		
		for (Map<String, Album> albumArtist : albumMap.values()) {
			for (Album album : albumArtist.values()) {
				albumList.add(album);
			}
		}
		
		albumList.sort(Comparator.naturalOrder());
		
		for (Album album : albumList) {
			// Sort songs in album
			album.getSongs().sort(Comparator.naturalOrder());
			
			directory.addDirectory(album);
		}
		
		return directory;
	}
	
	
	/**
	 * A directory filled with all artists in the user's music library
	 * @param albums A list of albums from which to pull the artists
	 * @return The directory with artists
	 */
	private Directory getArtistDirectory(List<Album> albums) {
		Directory directory = new Directory("Artists");
		
		// Map with album artist string as key and artist as value
		Map<String, Artist> artistMap = new HashMap<String, Artist>();
		
		for (Album album : albums) {
			
			if (!artistMap.containsKey(album.getAlbumArtist())) {
				Artist newArtist = new Artist(album.getAlbumArtist(), directory);
				artistMap.put(album.getAlbumArtist(), newArtist);
			}
			
			// Create a new copy and set the parent so that the album's parent
			// is this artist
			Album albumCopy = new Album(album);
			albumCopy.setParent(artistMap.get(album.getAlbumArtist()));
			artistMap.get(album.getAlbumArtist()).addDirectory(albumCopy);
		}
		
		
		// Convert it to a list, sort it, and add the artists to the directory
		List<Artist> artistList = new ArrayList<Artist>(artistMap.values());
		
		artistList.sort(Comparator.naturalOrder());
		
		for (Artist artist : artistList) {
			directory.addDirectory(artist);
		}
		
		return directory;
	}
	
	
	/**
	 * A directory filled with all genres in the user's music library
	 * @param albums A list of albums from which to pull the genres
	 * @return The directory with genres
	 */
	private Directory getGenreDirectory(List<Album> albums) {
		Directory directory = new Directory("Genres");
		
		// Map with genre string as key and genre as value
		Map<String, Genre> genreMap = new HashMap<String, Genre>();
		
		for (Album album : albums) {
			
			if (!genreMap.containsKey(album.getGenre())) {
				Genre newGenre = new Genre(album.getGenre(), directory);
				genreMap.put(album.getGenre(), newGenre);
			}
			
			// Create a new copy and set the parent so that the album's parent
			// is this genre
			Album albumCopy = new Album(album);
			albumCopy.setParent(genreMap.get(album.getGenre()));
			genreMap.get(album.getGenre()).addDirectory(albumCopy);
		}
		
		
		// Convert it to a list, sort it, and add the genres to the directory
		List<Genre> genreList = new ArrayList<Genre>(genreMap.values());
		
		genreList.sort(Comparator.naturalOrder());
		
		for (Genre genre : genreList) {
			directory.addDirectory(genre);
		}
		
		return directory;
	}
	
	
	/**
	 * Returns a directory with the playlists found in the given folder.
	 * @param rootFolder The root folder
	 * @return A directory with playlists
	 */
	private Directory getPlaylistDirectory(File rootFolder) {
		List<File> playlistFileList = getPlaylistsInFolder(rootFolder);
		
		Directory rootDirectory = new Directory("Playlists");
		
		// Get a list of songs for each line in each playlist file
		for (File playlistFile : playlistFileList) {
			InputStream is = null;
			try {
				is = new FileInputStream(playlistFile);
			} catch (FileNotFoundException e1) {
				System.out.println("Problem reading playlist file");
				e1.printStackTrace();
			}
			
			// Create a new playlist object
			String playlistName = playlistFile.getName().replace(Util.getFileExtension(playlistFile), "");
			MusicPlayerApplication.setUpdateMessage(playlistFile.getPath());
			Playlist newPlaylist = new Playlist(playlistName, rootDirectory);
			rootDirectory.addDirectory(newPlaylist);
			
			// Open the file
			Scanner inputFile = new Scanner(is, "UTF-8");
			
			// Read every non-comment (#) line
			String line;
			while (inputFile.hasNext()) {
				line = inputFile.nextLine();
				if (line.startsWith("#"))
					continue;
				
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
				
				// Create a new song with the line which is a song path and add
				// it to the playlist object
				Song newSong = new Song(line);
				newPlaylist.addSong(newSong);
			}
			
			// Close the file
			inputFile.close();
		}
		
		return rootDirectory;
	}
	
	
	/**
	 * Returns a directory with the playlists found in the given folder.
	 * @param rootFolderPath The path of the root folder as a string
	 * @return A directory with playlists
	 */
	private Directory getPlaylistDirectory(String rootFolderPath) {
		return getPlaylistDirectory(new File(rootFolderPath));
	}
		
	
	/**
	 * A list of playlists in a folder
	 * @param directory The folder
	 * @return The list of playlists
	 */
	private List<File> getPlaylistsInFolder(File directory) {
		List<File> playlistList = new ArrayList<File>();
		
		// Load the playlist paths
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				
				// If the file is a directory and recursion is on, recursively
				// call this method again
				if (file.isDirectory()) {
					playlistList.addAll(getPlaylistsInFolder(file));
				}
				
				// If a playlist file, add it
				else if (Arrays.asList(Util.PLAYLIST_FILE_EXTENTIONS).contains(Util.getFileExtension(file))) {
					playlistList.add(file);
				}
			}
		}
		
		return playlistList;
	}
}
