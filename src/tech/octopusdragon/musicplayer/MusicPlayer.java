package tech.octopusdragon.musicplayer;

import java.util.Comparator;

import tech.octopusdragon.musicplayer.util.Util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {
	
	// --- Variables ---
	private ObjectProperty<Directory> curDirectory;	// Current directory
	private ObservableList<Song> dirSongs;	// Songs in last directory in order
	private FilteredList<Song> dirSongsFiltered;	// Songs in directory filtered
	private SortedList<Song> dirSongsFilteredSorted;	// Filtered & sorted
	private ObservableList<Song> selectedSongs;	// Songs selected in track list
	private ObservableList<Song> songs;		// Current list of songs
	private ObjectProperty<Song> curSong;	// The currently playing song
	private Media media;				// Current media
	
	// --- Properties are used here instead of primitives to allow certain
	// things to happen  automatically when changes to these variables take
	// place. ---
	private ObjectProperty<MediaPlayer> playerProperty;	// Current media player
	private IntegerProperty index;		// Song index
	private BooleanProperty loaded;		// Whether media is loaded
	private BooleanProperty playing;		// Whether the media is playing
	private DoubleProperty volume;		// Current volume
	private BooleanProperty mute;		// Whether the media is muted
	private BooleanProperty shuffle;		// Whether in shuffle mode
	private BooleanProperty repeat;		// Whether in repeat mode
	private BooleanProperty repeatSingle;// Whether in repeat single mode
	private DoubleProperty rate;			// Rate of playback
	private DoubleProperty balance;		// Left/right balance/pan
	
	public MusicPlayer() {
		
		// Initialize some variables
		playerProperty = new SimpleObjectProperty<MediaPlayer>();
		curDirectory = new SimpleObjectProperty<Directory>();
		curSong = new SimpleObjectProperty<Song>();
		dirSongs = FXCollections.observableArrayList();
		dirSongsFiltered = new FilteredList<Song>(dirSongs);
		dirSongsFilteredSorted = new SortedList<Song>(dirSongsFiltered);
		selectedSongs = FXCollections.observableArrayList();
		songs = FXCollections.observableArrayList();
		
		index = new SimpleIntegerProperty(-1);
		loaded = new SimpleBooleanProperty(false);
		playing = new SimpleBooleanProperty(false);
		
		mute = new SimpleBooleanProperty(false);
		
		shuffle = new SimpleBooleanProperty(false);
		
		repeat = new SimpleBooleanProperty(false);
		repeatSingle = new SimpleBooleanProperty(false);
		repeat.addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				repeatSingle.set(false);
			}
		});
		repeatSingle.addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				repeat.set(false);
			}
		});
		
		volume = new SimpleDoubleProperty(0.8);
		
		rate = new SimpleDoubleProperty(1.0);
		balance = new SimpleDoubleProperty(0.0);
	}
	
	
	/**
	 * Pauses the current song if playing; plays the current song otherwise
	 */
	public void togglePlay() {
		if (playing.get()) {
			pauseMedia();
		}
		else {
			playMedia();
		}
	}
	
	
	/**
	 * Plays the current song.
	 */
	public void playMedia() {
		
		// If no directory has been loaded, do nothing
		if (curDirectory.get() == null)
			return;
		
		// If no media is loaded, load new media
		if (!loaded.get()) {
			newMediaReload();
		}
		
		// Play media
		playerProperty.get().play();
		
		// Update relevant variables
		playing.set(true);
	}
	
	
	
	/**
	 * Pauses the current song.
	 */
	public void pauseMedia() {
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// Pause media
		playerProperty.get().pause();
		
		// Update relevant variables
		playing.set(false);
	}
	
	
	/**
	 * Stops the current song.
	 */
	public void stopMedia() {
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// Stop media
		playerProperty.get().stop();
		
		// Set the current song to null
		curSong.set(null);
		
		// Update relevant variables
		if (playing.get())
			playing.set(false);
		loaded.set(false);
		index.set(-1);
	}
	
	
	/**
	 * Moves to the beginning of the current song.
	 */
	public void restartMedia() {
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// Restart
		playerProperty.get().seek(Duration.seconds(0.0));
	}
	
	
	/**
	 * Reloads the songs and starts a new song at 0.
	 */
	public void newMediaReload() {
		
		// Create a new song list from the current directory
		newSongList();
		
		// If shuffle is on, shuffle the list
		if (shuffle.get())
			shuffleList();
		
		newMedia(0);
	}
	
	
	/**
	 * Reloads the songs and starts a new song at the index of the given song
	 * @param song The song to play
	 */
	public void newMediaReload(Song song) {
		
		// Create a new song list from the current directory
		newSongList();

		newMedia(songs.indexOf(song));
		
		// If shuffle is on, shuffle the list
		if (shuffle.get())
			shufflePlaying();
	}
	
	
	/**
	 * Reloads the songs and starts a new song at the given index.
	 * @param index The index of the song to play
	 */
	public void newMediaReload(int index) {
		
		// Create a new song list from the current directory
		newSongList();
		
		newMedia(index);
		
		// If shuffle is on, shuffle the list
		if (shuffle.get())
			shufflePlaying();
	}
	
	
	/**
	 * Starts a new song
	 */
	public void newMedia(int newIndex) {

		// Do nothing if at an invalid index
		if (newIndex == -1)
			return;
		
		// Stop current media if playing
		else if (playing.get())
			playerProperty.get().stop();
		
		// Initialize the new media
		curSong.set(songs.get(newIndex));
		media = new Media(curSong.getValue().getURI());
		playerProperty.set(new MediaPlayer(media));
		playerProperty.get().setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				// If on repeat single, replay the track
				if (repeatSingle.get()) {
					restartMedia();
				}
				
				// Else if not on repeat and at the end, stop the media.
				else if (!repeat.get() && index.get() == songs.size() - 1) {
					stopMedia();
				}
				
				// Otherwise, the next method should take care of logic.
				else {
					next();
				}
			}
		});
		playerProperty.get().muteProperty().bind(mute);
		playerProperty.get().volumeProperty().bind(volume);
		playerProperty.get().rateProperty().bind(rate);
		playerProperty.get().balanceProperty().bind(balance);
		
		// Play media if it was playing before
		if (playing.get())
			playerProperty.get().play();
		
		// Set relevant variables
		if (!loaded.get())
			loaded.set(true);
		index.set(newIndex);
		
	}
	
	
	/**
	 * Starts the previous song
	 */
	public void previous() {
		int prevIndex;
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// If not at the beginning, just decrement the song index
		else if (index.get() > 0) {
			prevIndex = index.get() - 1;
		}
		
		// If at the beginning, go around to the end.
		else {
			// If on shuffle, create a new shuffle list.
			if (shuffle.get())
				shuffleList();
			prevIndex = songs.size() - 1;
		}
		
		// Initialize the new media
		newMedia(prevIndex);
	}
	
	
	/**
	 * Starts the next song
	 */
	public void next() {
		int nextIndex;
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// If not at the end, just increment the song index
		else if (index.get() < songs.size() - 1) {
			nextIndex = index.get() + 1;
		}
		
		// If at the end, go back to the beginning.
		else {
			// If on shuffle, create a new shuffle list.
			if (shuffle.get())
				shuffleList();
			nextIndex = 0;
		}
		
		// Initialize the new media
		newMedia(nextIndex);
	}
	
	
	/**
	 * Move to a new position in the audio.
	 * @param position The position duration in milliseconds
	 */
	public void seek(double position) {
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// Seek
		playerProperty.get().seek(Duration.millis(position));
	}
	
	
	/**
	 * Increase/decrease the current position in the audio
	 * @param amount The amount to move in milliseconds
	 */
	public void move(double amount) {
		
		// Do nothing if no media is loaded.
		if (!loaded.get())
			return;
		
		// Seek
		playerProperty.get().seek(Duration.millis(
				playerProperty.get().getCurrentTime().toMillis() + amount));
	}
	
	
	public void toggleShuffle() {
		
		// Toggle shuffle
		shuffle.set(!shuffle.get());
		
		if (playing.get()) {
			// If set to shuffle, shuffle
			if (shuffle.get()) {
				shufflePlaying();
			}
			
			// If not set to shuffle, reorder the list
			else {
				orderList();
			}
		}
	}
	
	
	/**
	 * Rotates the repeat option. This is a three-way toggle toggling between
	 * nothing, repeat, and repeat single.
	 */
	public void rotateRepeat() {
		
		// Toggle (repeat -> repeatSingle -> nothing ->)
		if (repeat.get()) {
			repeatSingle.set(true);
		}
		else if (repeatSingle.get()) {
			repeatSingle.set(false);
		}
		else {
			repeat.set(true);
		}
	}
	
	/**
	 * Toggles just repeat. Turns off repeat single if it is on
	 */
	public void toggleRepeat() {
		repeat.set(!repeat.get());
	}

	
	/**
	 * Toggles just repeat single. Turns off repeat if it is on
	 */
	public void toggleRepeatSingle() {
		repeatSingle.set(!repeatSingle.get());
	}
	
	
	/**
	 * Toggles the mute option.
	 */
	public void toggleMute() {
		mute.set(!mute.get());
	}
	
	
	/**
	 * Changes the volume of the media player.
	 * @param value The value to set the volume at from 0.0 to 1.0.
	 */
	public void changeVolume(double value) {
		volume.set(value);
	}
	
	
	/**
	 * Changes the playback rate of the media player.
	 * @param value The value to set the rate at
	 */
	public void changeRate(double value) {
		rate.set(value);
	}
	
	
	/**
	 * Changes the left/right balance/pan of the media player
	 * @param value The balance to set it at
	 */
	public void changeBalance(double value) {
		balance.set(value);
	}
	
	
	/**
	 * Lists the songs in a new directory.
	 * @param path The new directory.
	 * @param recursive Whether to add songs in subdirectories
	 */
	public void newDirectory(Directory directory, boolean recursive) {
		
		// Set the directory
		curDirectory.set(directory);
		dirSongs.clear();
		if (recursive)
			dirSongs.addAll(curDirectory.get().getSongsRecursive());
		else
			dirSongs.addAll(curDirectory.get().getSongs());
	}
	
	
	/**
	 * Lists songs in the given directories
	 * @param directories The directories
	 * @param recursive Whether to add songs in subdirectories
	 */
	public void newDirectories(boolean recursive, Directory... directories) {
		
		// Set the songs directories
		dirSongs.clear();
		for (Directory directory : directories) {
			if (recursive)
				dirSongs.addAll(directory.getSongsRecursive());
			else
				dirSongs.addAll(directory.getSongs());
		}
	}
	
	
	/**
	 * Fills the song list with the songs in the current directory in ascending
	 * order
	 */
	private void newSongList() {
		songs.clear();
		if (selectedSongs.size() > 1) {
			songs.addAll(selectedSongs);
		}
		else {
			songs.addAll(dirSongsFilteredSorted);
		}
	}
	
	
	/**
	 * Shuffles the songs in the song list in a random order and moves the
	 * currently playing song to the front of the list if need be.
	 */
	public void shufflePlaying() {
		
		shuffleList();
		
		// If there is a song playing, move it to the beginning of the
		// list. Then set the current index is 0
		if (loaded.get()) {
			songs.remove(curSong.getValue());
			songs.add(0, curSong.getValue());
			index.set(0);
		}
	}
	
	
	/**
	 * Shuffles the songs in the song list in a random order.
	 */
	public void shuffleList() {
		
		// Shuffle the songs to a random order
		Util.shuffleList(songs);
	}
	
	
	/**
	 * Orders the songs in the song list in ascending order.
	 */
	public void orderList() {
		
		// Sort the songs based on the directory's order (may or may not be in
		// ascending track order)
		songs.sort(Comparator.comparing(item -> dirSongsFilteredSorted.indexOf(item)));
		
		// If playing, the current index is the index of the current song in the
		// list
		if (playing.get())
			index.set(songs.indexOf(curSong.getValue()));
	}
	
	
	
	// --- Accessors and mutators ---

	/**
	 * @return The player property
	 */
	public ObjectProperty<MediaPlayer> playerProperty() {
		return playerProperty;
	}
	

	/**
	 * @return The underlying media player
	 */
	public MediaPlayer getPlayer() {
		return playerProperty.get();
	}


	/**
	 * @return the media
	 */
	public Media getMedia() {
		return media;
	}
	

	/**
	 * @return the current directory
	 */
	public ObjectProperty<Directory> getCurDirectory() {
		return curDirectory;
	}


	/**
	 * @return the currently playing song
	 */
	public ObjectProperty<Song> getCurSong() {
		return curSong;
	}
	
	
	/**
	 * @return An observable list of songs in the current directory
	 */
	public ObservableList<Song> getDirSongs() {
		return dirSongs;
	}
	
	
	/**
	 * @return A filterable list of songs in the current directory
	 */
	public FilteredList<Song> getFilteredDirSongs() {
		return dirSongsFiltered;
	}
	
	
	/**
	 * @return A sortable list of songs in the current directory
	 */
	public SortedList<Song> getSortedDirSongs() {
		return dirSongsFilteredSorted;
	}
	
	
	/**
	 * @return Songs selected in track list
	 */
	public ObservableList<Song> getSelectedSongs() {
		return selectedSongs;
	}
	
	
	/**
	 * Sets the songs selected in track list
	 * @return selectedSongs Songs selected in track list
	 */
	public void setSelectedSongs(ObservableList<Song> selectedSongs) {
		this.selectedSongs = selectedSongs;
	}

	
	/**
	 * @return The loaded property
	 */
	public BooleanProperty loadedProperty() {
		return this.loaded;
	}
	

	/**
	 * @return Whether media is loaded
	 */
	public boolean isLoaded() {
		return this.loadedProperty().get();
	}

	
	/**
	 * @return The playing property
	 */
	public BooleanProperty playingProperty() {
		return this.playing;
	}
	

	/**
	 * @return Whether a track is playing
	 */
	public boolean isPlaying() {
		return this.playingProperty().get();
	}
	
	
	/**
	 * @return The volume property
	 */
	public DoubleProperty volumeProperty() {
		return this.volume;
	}
	

	/**
	 * @return The volume
	 */
	public double getVolume() {
		return this.volumeProperty().get();
	}
	

	/**
	 * Sets the volume
	 * @param volume The volume to set (0.0 - 1.0)
	 */
	public void setVolume(double volume) {
		this.volumeProperty().set(volume);
	}

	
	/**
	 * @return The mute property
	 */
	public BooleanProperty muteProperty() {
		return this.mute;
	}
	

	/**
	 * @return Whether the player is muted
	 */
	public boolean isMuted() {
		return this.muteProperty().get();
	}
	

	/**
	 * Mutes or unmutes the player
	 * @param mute Whether the player is to be muted
	 */
	public void setMute(boolean mute) {
		this.muteProperty().set(mute);
	}

	
	/**
	 * @return The shuffle property
	 */
	public BooleanProperty shuffleProperty() {
		return this.shuffle;
	}
	

	/**
	 * @return Whether the player is on shuffle
	 */
	public boolean isOnShuffle() {
		return this.shuffleProperty().get();
	}
	

	/**
	 * Sets whether to shuffle the player
	 * @param shuffle Whether to shuffle the player
	 */
	public void setShuffle(boolean shuffle) {
		this.shuffleProperty().set(shuffle);
	}

	
	/**
	 * @return The repeat property
	 */
	public BooleanProperty repeatProperty() {
		return this.repeat;
	}
	

	/**
	 * @return Whether the player is on repeat
	 */
	public boolean isOnRepeat() {
		return this.repeatProperty().get();
	}
	

	/**
	 * Sets whether the player is on repeat
	 * @param repeat Whether the player is on repeat
	 */
	public void setRepeat(boolean repeat) {
		this.repeatProperty().set(repeat);
	}

	
	/**
	 * @return The repeat single property
	 */
	public BooleanProperty repeatSingleProperty() {
		return this.repeatSingle;
	}
	

	/**
	 * @return Whether the player is on repeat single
	 */
	public boolean isOnRepeatSingle() {
		return this.repeatSingleProperty().get();
	}
	

	/**
	 * Sets whether the player is on repeat single
	 * @param repeat Whether the player is on repeat single
	 */
	public void setRepeatSingle(boolean repeatSingle) {
		this.repeatSingleProperty().set(repeatSingle);
	}

	
	/**
	 * @return The rate property
	 */
	public DoubleProperty rateProperty() {
		return this.rate;
	}
	

	/**
	 * @return The rate of playback
	 */
	public double getRate() {
		return this.rateProperty().get();
	}
	

	/**
	 * Sets the rate of playback
	 * @param rate The rate of playback
	 */
	public void setRate(double rate) {
		this.rateProperty().set(rate);
	}

	
	/**
	 * @return The balance property
	 */
	public DoubleProperty balanceProperty() {
		return this.balance;
	}
	

	/**
	 * @return The left/right balance
	 */
	public double getBalance() {
		return this.balanceProperty().get();
	}
	

	/**
	 * Sets the left/right balance
	 * @param rate The left/right balance (-1.0 - 1.0)
	 */
	public void setBalance(double balance) {
		this.balanceProperty().set(balance);
	}
}