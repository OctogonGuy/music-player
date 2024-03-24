package tech.octopusdragon.musicplayer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a directory that has other directories or audio files. Can be a
 * folder, an artist, an album, etc.
 * @author Alex Gill
 *
 */
public class Directory implements Serializable, Comparable<Directory> {
	
	private static final long serialVersionUID = 8392913034804114971L;
	
	protected String name;	// The name that will show in navigator and history
	protected DirectoryType type;			// The type of directory
	protected Directory parent;				// The parent directory
	protected List<Directory> directories;	// List of sub-directories
	protected List<Song> songs;				// List of songs
	
	
	/**
	 * Instantiates a new directory
	 * @param name The name that will show up in navigator and history
	 */
	public Directory(String name) {
		this(name, null);
	}
	
	
	/**
	 * Instantiates a new directory
	 * @param name The name that will show up in navigator and history
	 * @param parent The parent directory
	 */
	public Directory(String name, Directory parent) {
		this.name = name;
		this.parent = parent;
		this.type = DirectoryType.DIRECTORY;
		directories = new ArrayList<Directory>();
		songs = new ArrayList<Song>();
	}
	
	
	/**
	 * @return the name that will show up in navigator and history
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @return the type of directory (same as class but as an enum)
	 */
	public DirectoryType getType() {
		return type;
	}
	
	
	/**
	 * @return the parent directory, or null if there is none
	 */
	public Directory getParent() {
		return parent;
	}
	
	
	/**
	 * @return the highest level directory, or itself if this is the highest
	 */
	public Directory getRoot() {
		Directory curDir = this;
		while (curDir.getParent() != null) {
			curDir = curDir.getParent();
		}
		return curDir;
	}
	
	
	/**
	 * Sets this directory's parent directory
	 * @param parent the parent directory
	 */
	public void setParent(Directory parent) {
		this.parent = parent;
	}
	
	
	/**
	 * @return A list of directories in this directory
	 */
	public List<Directory> getDirectories() {
		return directories;
	}


	/**
	 * @return the songs in this directory
	 */
	public List<Song> getSongs() {
		return songs;
	}


	/**
	 * @return the song paths in this directory and all its subdirectories
	 */
	public List<Song> getSongsRecursive() {
		List<Song> deepSongs = new ArrayList<Song>();
		
		for (Directory directory : directories) {
			deepSongs.addAll(directory.getSongsRecursive());
		}
		
		deepSongs.addAll(songs);
		
		return deepSongs;
	}
	
	
	/**
	 * @return whether this directory has any subdirectories
	 */
	public boolean hasDirectories() {
		return !directories.isEmpty();
	}
	
	
	/**
	 * @return whether this directory has any songs
	 */
	public boolean hasSongs() {
		return !songs.isEmpty();
	}
	
	
	/**
	 * Adds a directory to this directory's list of of sub-directories
	 * @param directory The directory to add
	 */
	public void addDirectory(Directory directory) {
		directories.add(directory);
	}
	
	
	/**
	 * Adds a song to this directory's list of songs
	 * @param song The song to add
	 */
	public void addSong(Song song) {
		songs.add(song);
	}
	
	
	/**
	 * Adds multiple songs to this directory's list of songs
	 * @param song The song to add
	 */
	public void addSongs(Collection<Song> songCollection) {
		songs.addAll(songCollection);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Directory))
			return false;
		Directory otherDirectory = (Directory)other;
		return this.name.equals(otherDirectory.name) && this.type == otherDirectory.type;
	}
	
	
	@Override
	public String toString() {
		return this.getName();
	}


	@Override
	public int compareTo(Directory other) {
		return this.getName().compareToIgnoreCase(other.getName());
	}
}
