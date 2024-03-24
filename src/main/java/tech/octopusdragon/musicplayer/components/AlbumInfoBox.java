package tech.octopusdragon.musicplayer.components;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.model.Album;
import tech.octopusdragon.musicplayer.model.Song;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * UI component with album info to appear in the track navigation view
 * @author Alex Gill
 *
 */
public class AlbumInfoBox extends GridPane {
	
	// --- Constants ---
	// Height of album info box
	private static final double DEFAULT_HEIGHT = 140.0;
	
	// --- Variables ---
	private Album album;	// The album this shows info for
	
	// --- UI components ---
	@FXML private StackPane imagePane;
	@FXML private ImageView albumImageView;
	@FXML private Label albumLabel;
	@FXML private Label albumArtistLabel;
	@FXML private Label genreLabel;
	@FXML private Label yearLabel;
	
	
	/**
	 * Constructs an album info box
	 */
	public AlbumInfoBox() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AlbumInfoBox.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set height of grid pane
		imagePane.setPrefHeight(DEFAULT_HEIGHT);
		
		// Set height of album image view
		albumImageView.fitHeightProperty().bind(imagePane.prefHeightProperty());
		
		// Change album and UI upon song list changing
		ObservableList<Song> songs = MusicPlayerApplication.getMusicPlayer().getDirSongs();
		songs.addListener((ListChangeListener<Song>) c -> {
			if (songs.isEmpty())
				return;
			
			album = songs.get(0).getAlbum();
			
			if (album == null) return;
			
			if (album.getCoverArtData() != null) {
				albumImageView.setImage(new Image(new ByteArrayInputStream(album.getCoverArtData())));
			}
			if (album.getCoverArtData() != null &&
					!this.getChildren().contains(albumImageView)) {
				this.getChildren().add(0, albumImageView);
			}
			else if (album.getCoverArtData() == null &&
					this.getChildren().contains(albumImageView)){
				this.getChildren().remove(albumImageView);
			}
			albumLabel.setText(album.getAlbum());
			albumArtistLabel.setText(album.getAlbumArtist());
			genreLabel.setText(album.getGenre());
			yearLabel.setText(String.valueOf(album.getYear()));
		});
	}
}
