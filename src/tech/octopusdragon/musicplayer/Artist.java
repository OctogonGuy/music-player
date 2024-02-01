package tech.octopusdragon.musicplayer;

import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Represents an artist. Has albums.
 * @author Alex Gill
 *
 */
public class Artist extends Directory {
	
	private static final long serialVersionUID = 2347920597315347220L;
	
	private String albumArtist;		// Album artist
	
	
	
	/**
	 * Instantiates a new folder
	 * @param albumArtist The album artist
	 * @param parent The parent directory
	 * @throws FileNotFoundException If the folder image is not found
	 */
	public Artist(String albumArtist, Directory parent) {
		super(albumArtist, parent);
		this.type = DirectoryType.ARTIST;
		this.albumArtist = albumArtist;
	}
	
	
	/**
	 * Copy constructor
	 * @param other The object to copy from
	 */
	public Artist(Artist other) {
		this(	other.albumArtist,
				other.parent
			);
		this.directories = new ArrayList<Directory>(other.directories);
		this.songs = new ArrayList<Song>(other.songs);
	}


	/**
	 * @return the album artist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}
}
