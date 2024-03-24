package tech.octopusdragon.musicplayer.scenes;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.model.Song;
import tech.octopusdragon.musicplayer.util.Resource;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * View showing the currently playing song
 * @author Alex Gill
 *
 */
public class CurrentlyPlayingView extends StackPane {
	
	// Cover art width and height as proportion of view
	private static final double COVER_ART_SIZE = 0.6;
	
	@FXML private ImageView coverArt;	// Image view for current song cover art

	public CurrentlyPlayingView() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CurrentlyPlayingView.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		
		// Bind album cover image view to current song album cover
		coverArt.imageProperty().bind(Bindings.createObjectBinding(() -> {
			Song curSong = MusicPlayerApplication.getMusicPlayer().getCurSong().get();
			if (curSong == null) {
				return new Image(Resource.NOW_PLAYING_IMAGE.getResourceAsStream());
			}
			byte[] imageArray = MusicPlayerApplication.getMusicPlayer().getCurSong().get().coverArtData();
			if (imageArray != null) {
				return new Image(new ByteArrayInputStream(imageArray));
			}
			else {
				return new Image(Resource.NOW_PLAYING_IMAGE.getResourceAsStream());
			}
		}, MusicPlayerApplication.getMusicPlayer().getCurSong()));
		
		coverArt.fitHeightProperty().bind(this.heightProperty().multiply(COVER_ART_SIZE));
		coverArt.fitWidthProperty().bind(this.widthProperty().multiply(COVER_ART_SIZE));
		coverArt.visibleProperty().bind(heightProperty().greaterThan(0.0).and(widthProperty().greaterThan(0.0)));
		
		
		// Add effects to the cover art
		NumberBinding sizeBinding = Bindings.min(coverArt.fitHeightProperty(), coverArt.fitWidthProperty());
		DropShadow shadow = new DropShadow();
		shadow.setColor(Color.color(0, 0, 0, 0.6));
		shadow.radiusProperty().bind(sizeBinding.divide(8));
		shadow.setOffsetX(0.0);
		shadow.offsetYProperty().bind(sizeBinding.divide(21));
		shadow.setBlurType(BlurType.GAUSSIAN);
		shadow.setSpread(0.14);
		Reflection reflection = new Reflection();
		reflection.topOffsetProperty().bind(sizeBinding.negate().divide(4));
		reflection.setFraction(0.45);
		reflection.setTopOpacity(0.45);
		reflection.setBottomOpacity(0.0);
		reflection.setInput(shadow);
		coverArt.setEffect(reflection);
	}

}
