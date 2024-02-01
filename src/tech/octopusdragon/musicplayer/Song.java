package tech.octopusdragon.musicplayer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.mp4.Mp4Tag;

import tech.octopusdragon.musicplayer.util.Util;

/**
 * Represents a song with attributes such as file and path, as well as methods
 * to retrieve metadata such as track, title, artist, and album.
 * @author Alex
 *
 */
public class Song implements Comparable<Song>, Serializable {
	
	private static final long serialVersionUID = 1831441055680211029L;
	
	// Fields
	private String path;		// The path of the file as a string
	private String filename;	// Filename of the file as a string
	private int disc;
	private int track;
	private String title;
	private String artist;
	private String albumArtist;
	private String albumStr;
	private String genre;
	private int year;
	private String duration;
	
	private Album album;
	
	/**
	 * Constructor
	 * @param file The File object for the song.
	 */
	public Song(File file) {
		
		// Initialize path and filename
		path = file.getPath();
		filename = file.getName();
		
		// Initialize audio file tags
		initializeTags(file);
	}
	
	
	/**
	 * Initializes the audio tags of the song
	 */
	private void initializeTags(File file) {
		
		// Initialize audio file and tag
		AudioFile audioFile = audioFile(file);
		Tag tag = tag(audioFile);
		//AudioFileFormat format = AudioSystem.getAudioFileFormat(file);
		
		// Initialize disc
		initializeDisc(tag);
		
		// Initialize track
		initializeTrack(tag);
		
		// Initialize title
		initializeTitle(tag);

		// Initialize artist
		initializeArtist(tag);

		// Initialize album artist
		initializeAlbumArtist(tag);

		// Initialize album
		initializeAlbum(tag);

		// Initialize genre
		initializeGenre(tag);

		// Initialize year
		initializeYear(tag);

		// Initialize duration
		initializeDuration(audioFile);
	}
	
	
	/**
	 * Constructor
	 * @param filename The filename of the song as a string
	 */
	public Song(String filename) {
		this(new File(filename));
	}
	
	
	/**
	 * Returns the URI that represents the song's abstract path.
	 * @return The URI that represents the song's abstract path.
	 */
	public String getURI() {
		return new File(path).toURI().toString();
	}
	
	
	/**
	 * Returns the song's abstract path.
	 * @return The song's abstract path.
	 */
	public String getPath() {
		return path;
	}
	

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}


	/**
	 * Returns the song's extension.
	 * @return The song's extension.
	 */
	public String getExtension() {
		return Util.getFileExtension(path);
	}
	
	
	/**
	 * Initializes the disc number of the song.
	 */
	public void initializeDisc(Tag tag) {
		String disc = "";
		
		disc = tag.getFirst(FieldKey.DISC_NO);
		
		// Separate track number from total tracks if applicable.
		//disc = disc.split("/")[0];
		
		// If empty, it is disc 1.
		if (disc.equals(""))
			disc = "1";
		
		this.disc =  Integer.parseInt(disc);
	}
	
	
	/**
	 * Initializes the song's track number.
	 */
	public void initializeTrack(Tag tag) {
		String track = "";
		
		track = tag.getFirst(FieldKey.TRACK);
		
		// Separate track number from total tracks if applicable.
		//track = track.split("/")[0];
		
		// If empty, set to 0
		if (track.equals(""))
			track = "0";
		
		this.track = Integer.parseInt(track);
	}
	
	
	/**
	 * Initializes the song's title.
	 */
	public void initializeTitle(Tag tag) {
		String title = "";
		
		title = tag.getFirst(FieldKey.TITLE);
		
		// If empty set to unknown title
		if (title.equals(""))
			title = "Unknown Title";
		
		this.title = title;
	}
	
	
	/**
	 * Initializes the song's artist.
	 */
	public void initializeArtist(Tag tag) {
		String artist = "";
		
		artist = tag.getFirst(FieldKey.ARTIST);
		
		// If empty set to unknown artist
		if (artist.equals(""))
			artist = "Unknown Artist";
		
		this.artist = artist;
	}
	
	
	/**
	 * Initializes the song's album artist.
	 */
	public void initializeAlbumArtist(Tag tag) {
		String albumArtist = "";
		
		albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
		
		// If empty set to unknown album artist
		if (albumArtist.equals(""))
			albumArtist = "Unknown Album Artist";
		
		this.albumArtist = albumArtist;
	}
	
	
	/**
	 * Initializes the song's album.
	 */
	public void initializeAlbum(Tag tag) {
		String album = "";
		
		album = tag.getFirst(FieldKey.ALBUM);
		
		// If empty set to unknown album
		if (album.equals(""))
			album = "Unknown Album";
		
		this.albumStr = album;
	}
	
	
	/**
	 * Initializes the song's genre.
	 */
	public void initializeGenre(Tag tag) {
		String genre = "";
		
		genre = tag.getFirst(FieldKey.GENRE);
		
		//genre = Util.parseGenre(genre);
		
		// If empty set to unknown genre
		if (genre.equals(""))
			genre = "Unknown Genre";
		
		this.genre = genre;
	}
	
	
	/**
	 * Initializes the song's year.
	 */
	public void initializeYear(Tag tag) {
		String year = "";
		
		year = tag.getFirst(FieldKey.YEAR);
		
		year = year.split("-")[0];
		
		// If empty set to unknown genre
		if (year.equals(""))
			year = "0";
		
		this.year = Integer.parseInt(year);
	}
	
	
	/**
	 * Initializes the song's duration in the form of a string.
	 */
	public void initializeDuration(AudioFile audioFile) {
		int minutes, seconds;
		
		// Get minutes and seconds;
		seconds = audioFile.getAudioHeader().getTrackLength();
		minutes = seconds / 60;
		seconds = seconds % 60;
		
		this.duration = String.format("%01d:%02d", minutes, seconds);
	}


	/**
	 * @return the disc
	 */
	public int getDisc() {
		return disc;
	}


	/**
	 * @return the track
	 */
	public int getTrack() {
		return track;
	}


	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}


	/**
	 * @return the albumArtist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}


	/**
	 * @return the album as a string
	 */
	public String getAlbumStr() {
		return albumStr;
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
	 * @return the coverArt
	 */
	public byte[] coverArtData() {
		Tag tag = tag();
		Artwork coverArt = null;
		
		// .mp3 extension
		if (getExtension().equals(".mp3"))
			coverArt = tag.getFirstArtwork();
		
		// .m4a extension
		else if (getExtension().equals(".m4a"))
			coverArt = ((Mp4Tag)tag).getFirstArtwork();
		
		return coverArt != null ? coverArt.getBinaryData() : null;
	}


	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}


	/**
	 * @return the album
	 */
	public Album getAlbum() {
		return album;
	}


	/**
	 * @param album the album to set
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}
	
	
	@Override
	public int compareTo(Song other) {
		int number;
		
		// First compare albums
		if (getAlbumStr().compareToIgnoreCase(
				other.getAlbumStr()) < 0)
			number = -1;
		else if (getAlbumStr().compareToIgnoreCase(
				other.getAlbumStr()) > 0)
			number = 1;
		
		// Then compare album artists
		else if (getAlbumArtist().compareToIgnoreCase(
				other.getAlbumArtist()) < 0)
			number = -1;
		else if (getAlbumArtist().compareToIgnoreCase(
				other.getAlbumArtist()) > 0)
			number = 1;
		
		// Then compare discs
		else if (getDisc() < other.getDisc())
			number = -1;
		else if (getDisc() > other.getDisc())
			number = 1;
		
		// Then compare tracks
		else if (getTrack() < other.getTrack())
			number = -1;
		else if (getTrack() > other.getTrack())
			number = 1;
		
		else
			number = 0;
		
		return number;
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (!Object.class.getClass().equals(Song.class.getClass()) ||
				this == null || other == null)
			return false;
		
		Song otherSong = (Song) other;
		if (path.equals(otherSong.path))
			return true;
		else
			return false;
	}
	
	
	/**
	 * @return An audio file of this song
	 */
	private AudioFile audioFile() {
		return audioFile(new File(getPath()));
	}
	

	/**
	 * @param file An existing File object to use for AudioFile constructor
	 * @return An audio file of this song
	 */
	private AudioFile audioFile(File file) {
		AudioFile audioFile = null;
		try {
			audioFile = AudioFileIO.read(file);
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException e) {
			System.out.println("ERROR: Cannot read audio file...");
			e.printStackTrace();
		}
		return audioFile;
	}
	
	
	/**
	 * @return A tag of this song
	 */
	private Tag tag() {
		return tag(audioFile());
	}
	
	
	/**
	 * @param audioFile An existing AudioFile object to use for Tag constructor
	 * @return A tag of this song
	 */
	private Tag tag(AudioFile audioFile) {
		Tag tag = null;
		
		if (getExtension().equals(".mp3"))		// .mp3
			tag = ((MP3File)audioFile).getTagAndConvertOrCreateAndSetDefault();
		
		else if (getExtension().equals(".m4a"))	// .m4a
			tag = (Mp4Tag) audioFile.getTagAndConvertOrCreateAndSetDefault();
		
		return tag;
	}
}
