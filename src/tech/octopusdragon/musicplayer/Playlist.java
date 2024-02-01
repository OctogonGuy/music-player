package tech.octopusdragon.musicplayer;

import java.io.FileNotFoundException;

/**
 * Represents a playlist with songs.
 * @author Alex Gill
 *
 */
public class Playlist extends Directory {
	
	private static final long serialVersionUID = 6602665239188512305L;
	
	
	/**
	 * Instantiates a new playlist
	 * @param name The name that will show up in navigator and history
	 * @param parent The parent directory
	 * @throws FileNotFoundException If the folder image is not found
	 */
	public Playlist(String name, Directory parent) {
		super(name, parent);
		this.type = DirectoryType.PLAYLIST;
	}

}
