package tech.octopusdragon.musicplayer;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Represents a genre. Has albums.
 * @author Alex Gill
 *
 */
public class Genre extends Directory {
	
	private static final long serialVersionUID = 4248246835914042962L;
	

	private String genre;			// Genre
	
	
	/**
	 * Instantiates a new folder
	 * @param genre The genre
	 * @param parent The parent directory
	 * @throws FileNotFoundException If the folder image is not found
	 */
	public Genre(String genre, Directory parent) {
		super(genre, parent);
		this.type = DirectoryType.GENRE;
		this.genre = genre;
	}
	
	
	/**
	 * Copy constructor
	 * @param other The object to copy from
	 */
	public Genre(Genre other) {
		this(	other.genre,
				other.parent
			);
		this.directories = new ArrayList<Directory>(other.directories);
		this.songs = new ArrayList<Song>(other.songs);
	}


	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
}
