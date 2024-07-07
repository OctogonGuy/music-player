package tech.octopusdragon.musicplayer.scenes;

import javafx.scene.control.ProgressBar;
import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.components.ManualToggleButton;
import tech.octopusdragon.musicplayer.model.MusicPlayer;
import tech.octopusdragon.musicplayer.tools.TimeDenominatorMode;
import tech.octopusdragon.musicplayer.tools.Userdata;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class ControlBoxController {
	
	// --- Constants ---
	// Time in millis before which previous will return to the beginning
	public final int TIME_BEFORE_PREVIOUS = 2000;
	
	// --- Variables ---
	private MusicPlayer player;	// The music player
	private TimeDenominatorMode timeDenominatorMode;	// Time denominator mode
	
	@FXML private GridPane controlBox;
	@FXML private HBox controlBoxTop;
	@FXML private VBox controlBoxLeft;
	@FXML private HBox controlBoxCenter;
	@FXML private HBox controlBoxRight;
	@FXML private Label curTimeLabel;
	@FXML private Slider progressBar;
	@FXML private Label denominatorLabel;
	@FXML private Label titleLabel;
	@FXML private Label artistLabel;
	@FXML private Label albumLabel;
	@FXML private ManualToggleButton shuffleButton;
	@FXML private ManualToggleButton repeatButton;
	@FXML private Button stopButton;
	@FXML private Button previousButton;
	@FXML private Button playButton;
	@FXML private Button nextButton;
	@FXML private Button muteButton;
	@FXML private Slider volumeSlider;
	@FXML private Label loadingSongLabel;
	@FXML private Button reloadButton;
	@FXML private Button folderButton;
	@FXML private ProgressBar loadingProgressBar;
	
	@FXML
	private void initialize() {
		player = MusicPlayerApplication.getMusicPlayer();
		
		timeDenominatorMode = TimeDenominatorMode.valueOf(Userdata.
				readProperty("time-denominator-mode", TimeDenominatorMode.DURATION.name()));
		
		// Set bound properties
		volumeSlider.valueProperty().bindBidirectional(player.volumeProperty());
		
		stopButton.disableProperty().bind(player.loadedProperty().not());
		previousButton.disableProperty().bind(player.loadedProperty().not());
		nextButton.disableProperty().bind(player.loadedProperty().not());
		
		shuffleButton.selectedProperty().bind(
				player.shuffleProperty());
		repeatButton.selectedProperty().bind(
				player.repeatProperty().or(player.repeatSingleProperty()));
		
		reloadButton.disableProperty().bind(
				MusicPlayerApplication.reloadingProperty());
		
		playButton.textProperty().bind(Bindings.createStringBinding(() -> {
			if (player.isPlaying())
				return "⏸";
			else
				return "⏵";
		}, player.playingProperty()));
		
		muteButton.textProperty().bind(Bindings.createStringBinding(() -> {
			if (player.isMuted())
				return "🔇";
			else
				return "🔊";
		}, player.muteProperty()));
		
		repeatButton.textProperty().bind(Bindings.createStringBinding(() -> {
			if (player.isOnRepeat())
				return "🔁";
			else if (player.isOnRepeatSingle())
				return "🔂";
			else
				return "🔁";
		}, player.repeatProperty(), player.repeatSingleProperty()));

		loadingProgressBar.visibleProperty().bind(MusicPlayerApplication.reloadingProperty());
		loadingProgressBar.progressProperty().bind(MusicPlayerApplication.reloadProgressProperty());
		
		
		// Set the display to also update whenever the song changes
		player.playerProperty().addListener((observable, oldValue, newValue) -> {
			
			// If no media is loadedProperty(), reset the display.
			if (newValue == null) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						titleLabel.setText(null);
						artistLabel.setText(null);
						albumLabel.setText(null);
						progressBar.setValue(0.0);
						curTimeLabel.setVisible(false);
						curTimeLabel.setText("00:00");
						denominatorLabel.setVisible(false);
						denominatorLabel.setText("00:00");
					}
				});
			} else {
				
				// Update the new media's information
				newValue.setOnReady(new Runnable() {
					@Override
					public void run() {
						displayInfo();
						updateTime();
						updateProgress();
					}
				});
				newValue.currentTimeProperty().addListener(
						(timeObservable, timeOldValue, timeNewValue) -> {
					if (newValue.getStatus() == Status.PAUSED) return;
					updateTime();
					updateProgress();
				});
			}
		});
		
		// If no media is loadedProperty(), reset the display.
		player.loadedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != true) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						titleLabel.setText(null);
						artistLabel.setText(null);
						albumLabel.setText(null);
						progressBar.setValue(0.0);
						curTimeLabel.setVisible(false);
						curTimeLabel.setText("00:00");
						denominatorLabel.setVisible(false);
						denominatorLabel.setText("00:00");
					}
				});
			}
		});
		
		
		// Show the currently loading song when that changes
		MusicPlayerApplication.updateMessageProperty().addListener(
				(obs, oldVal, newVal) -> {
			Platform.runLater(() -> {
				loadingSongLabel.setText(
						MusicPlayerApplication.updateMessageProperty().getValue());
			});
		});
		
		// Set the time labels as invisible to start with
		curTimeLabel.setVisible(false);
		denominatorLabel.setVisible(false);
	}
	
	@FXML
	private void seekPressed(MouseEvent event) {
		if (!player.isLoaded()) return;
		
		if (player.isPlaying()) {
			player.getPlayer().pause();
		}
		
		player.seek(((Slider)event.getSource()).getValue() *
				player.getMedia().getDuration().toMillis());
		
		updateTime();
	}
	
	@FXML
	private void seekDragged(MouseEvent event) {
		if (!player.isLoaded()) return;
		
		player.seek(((Slider)event.getSource()).getValue() *
				player.getMedia().getDuration().toMillis());
		
		updateTime();
	}
	
	@FXML
	private void seekReleased(MouseEvent event) {
		if (!player.isLoaded()) return;
		
		if (((Slider)event.getSource()).getValue() == 1) {
			player.getPlayer().seek(player.getMedia().getDuration().add(Duration.millis(1).negate()));
		}
		
		if (player.isPlaying()) {
			player.getPlayer().play();
		}
	}
	
	@FXML
	private void toggleShuffle(ActionEvent event) {
		player.toggleShuffle();
	}
	
	@FXML
	private void rotateRepeat(ActionEvent event) {
		player.rotateRepeat();
	}
	
	@FXML
	private void stop(ActionEvent event) {
		player.stopMedia();
	}
	
	@FXML
	private void previous(ActionEvent event) {
		if (player.getPlayer().getCurrentTime().toMillis() < TIME_BEFORE_PREVIOUS) {
			player.previous();
		}
		else {
			player.restartMedia();
		}
	}
	
	@FXML
	private void playPause(ActionEvent event) {
		player.togglePlay();
	}
	
	@FXML
	private void next(ActionEvent event) {
		player.next();
	}
	
	@FXML
	private void toggleMute(ActionEvent event) {
		player.toggleMute();
	}
	
	@FXML
	private void reload(ActionEvent event) {
		MusicPlayerApplication.reload();
	}
	
	@FXML
	private void selectRootFolder(ActionEvent event) {
		MusicPlayerApplication.selectRootFolder();
	}
	
	@FXML
	private void toggleTimeDenominatorMode() {
		if (timeDenominatorMode == TimeDenominatorMode.DURATION) {
			timeDenominatorMode = TimeDenominatorMode.TIME_LEFT;
			updateTime();
		}
		else {
			timeDenominatorMode = TimeDenominatorMode.DURATION;
			displayInfo();
		}
		Userdata.writeProperty(
				"time-denominator-mode", timeDenominatorMode.name());
	}
	
	/**
	 * Displays the metadata of the song (i.e.: title, artist, album)
	 */
	private void displayInfo() {
		
		// Display the title
		titleLabel.setText(player.getCurSong().getValue().getTitle());
		
		// Display the artist
		artistLabel.setText(player.getCurSong().getValue().getArtist());
		
		// Display the album
		albumLabel.setText(player.getCurSong().getValue().getAlbumStr());
		
		// If denominator label is in duration mode, display the duration
		if (timeDenominatorMode == TimeDenominatorMode.DURATION) {
			double durationMins =
					player.getMedia().getDuration().toMinutes();
			double durationSecs =
					player.getMedia().getDuration().toSeconds() % 60;
			denominatorLabel.setText(String.format("%02d:%02d",
											(int)durationMins,
											(int)durationSecs));
		}
		
		// Display the current time
		updateTime();
	}
	
	/**
	 * Updates the time label to show the current time in the song
	 */
	private void updateTime() {

		// Update the current time
		double curTimeMins =
				player.getPlayer().getCurrentTime().toMinutes();
		double curTimeSecs =
				player.getPlayer().getCurrentTime().toSeconds() % 60;
		
		curTimeLabel.setText(String.format("%02d:%02d",
										(int)curTimeMins,
										(int)curTimeSecs));
		
		// If denominator label is in time left mode, update the time left
		if (timeDenominatorMode == TimeDenominatorMode.TIME_LEFT) {
			double timeLeftMins =
					player.getMedia().getDuration().toMinutes() -
					player.getPlayer().getCurrentTime().toMinutes();
			double timeLeftSecs =
					(player.getMedia().getDuration().toSeconds() -
					player.getPlayer().getCurrentTime().toSeconds()) % 60;
			
			denominatorLabel.setText(String.format("-%02d:%02d",
											(int)(timeLeftMins),
											(int)(timeLeftSecs)));
		}
		
		// Show the time
		curTimeLabel.setVisible(true);
		denominatorLabel.setVisible(true);
	}
	
	/**
	 * Updates the progress bar to match the current progress of the song.
	 */
	private void updateProgress() {
		
		// Get the current time
		double curTimeMillis =
				player.getPlayer().getCurrentTime().toMillis();
		
		// Get the duration
		double durationMillis =
				player.getMedia().getDuration().toMillis();
		
		// Set the progress of the progress bar
		progressBar.setValue(curTimeMillis / durationMillis);
	}
	
}
