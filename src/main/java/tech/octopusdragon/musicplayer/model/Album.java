package tech.octopusdragon.musicplayer.model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents an album
 * @author Alex Gill
 *
 */
public class Album extends Directory {
	
	private static final long serialVersionUID = -4217132869104276041L;
	
	private String album;			// Album
	private String albumArtist;		// Album artist
	private String genre;			// Genre
	private int year;				// Year
	private byte[] coverArtData;	// Cover art byte array
	
	
	/**
	 * Instantiates a new album
	 * @param album The album title
	 * @param albumArtist The album artist
	 * @param genre The genre
	 * @param year The year
	 * @param coverArtData A byte array containing the data for cover art
	 * @param parent The parent directory
	 */
	public Album(String album, String albumArtist, String genre, int year,
			byte[] coverArtData, Directory parent) {
		super(album, parent);
		this.type = DirectoryType.ALBUM;
		this.album = album;
		this.albumArtist = albumArtist;
		this.genre = genre;
		this.year = year;
		this.coverArtData = coverArtData;
	}
	
	
	/**
	 * Copy constructor
	 * @param other The object to copy from
	 */
	public Album(Album other) {
		this(	other.album,
				other.albumArtist,
				other.genre,
				other.year,
				other.coverArtData,
				other.parent
			);
		this.directories = new ArrayList<Directory>(other.directories);
		this.songs = new ArrayList<Song>(other.songs);
	}


	/**
	 * @return the album title
	 */
	public String getAlbum() {
		return album;
	}


	/**
	 * @return the album artist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}


	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}


	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}


	/**
	 * @return the cover art as a byte array
	 */
	public byte[] getCoverArtData() {
		return coverArtData;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(album, albumArtist, genre, year);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		return Objects.equals(album, other.album) && Objects.equals(albumArtist, other.albumArtist)
				&& Objects.equals(genre, other.genre) && year == other.year;
	}
}
