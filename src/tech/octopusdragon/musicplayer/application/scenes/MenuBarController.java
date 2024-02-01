package tech.octopusdragon.musicplayer.application.scenes;

import tech.octopusdragon.musicplayer.MusicPlayer;
import tech.octopusdragon.musicplayer.application.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.application.tools.Balance;
import tech.octopusdragon.musicplayer.application.tools.Rate;
import tech.octopusdragon.musicplayer.application.tools.Theme;
import tech.octopusdragon.musicplayer.application.tools.UISize;
import tech.octopusdragon.musicplayer.application.tools.View;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

public class MenuBarController {
	// TODO - Visualizer view - Fourier Transform
	
	// --- Constants ---
	// Interval to fast forward/rewind by in seconds
	private static final double TIME_INTERVAL = 15.0;
	// Interval to increase/decrease volume by as a percent
	private static final double VOLUME_INTERVAL = 0.1;
	
	// --- Variables ---
	private MusicPlayer player;	// The music player
	
	@FXML private CheckMenuItem shuffleMenuItem;
	@FXML private CheckMenuItem repeatMenuItem;
	@FXML private CheckMenuItem repeatSingleMenuItem;
	@FXML private CheckMenuItem muteMenuItem;
	
	@FXML private RadioMenuItem rateQuarterMenuItem;
	@FXML private RadioMenuItem rateHalfMenuItem;
	@FXML private RadioMenuItem rateNormalMenuItem;
	@FXML private RadioMenuItem rateDoubleMenuItem;
	@FXML private RadioMenuItem rateQuadrupleMenuItem;
	
	@FXML private RadioMenuItem balanceFullLeftMenuItem;
	@FXML private RadioMenuItem balanceHalfLeftMenuItem;
	@FXML private RadioMenuItem balanceCenterMenuItem;
	@FXML private RadioMenuItem balanceHalfRightMenuItem;
	@FXML private RadioMenuItem balanceFullRightMenuItem;
	
	@FXML private RadioMenuItem explorerMenuItem;
	@FXML private RadioMenuItem currentlyPlayingMenuItem;

	@FXML private Menu uiSizeMenu;
	@FXML private Menu themeMenu;
	
	@FXML
	private void initialize() {
		player = MusicPlayerApplication.getMusicPlayer();
		
		// Bind some play menu items bidirectionally so that clicking it affects
		// the music player and using the music player checks the item
		shuffleMenuItem.selectedProperty().bindBidirectional(
				player.shuffleProperty());
		repeatMenuItem.selectedProperty().bindBidirectional(
				player.repeatProperty());
		repeatSingleMenuItem.selectedProperty().bindBidirectional(
				player.repeatSingleProperty());
		muteMenuItem.selectedProperty().bindBidirectional(
				player.muteProperty());
		
		// Set initial checked rate item
		switch(MusicPlayerApplication.getRateEnum()) {
		case QUARTER:
			rateQuarterMenuItem.setSelected(true);
			break;
		case HALF:
			rateHalfMenuItem.setSelected(true);
			break;
		case NORMAL:
			rateNormalMenuItem.setSelected(true);
			break;
		case DOUBLE:
			rateDoubleMenuItem.setSelected(true);
			break;
		case QUADRUPLE:
			rateQuadrupleMenuItem.setSelected(true);
			break;
		}
		
		// Set initial checked balance item
		switch(MusicPlayerApplication.getBalanceEnum()) {
		case FULL_LEFT:
			balanceFullLeftMenuItem.setSelected(true);
			break;
		case HALF_LEFT:
			balanceHalfLeftMenuItem.setSelected(true);
			break;
		case CENTER:
			balanceCenterMenuItem.setSelected(true);
			break;
		case HALF_RIGHT:
			balanceHalfRightMenuItem.setSelected(true);
			break;
		case FULL_RIGHT:
			balanceFullRightMenuItem.setSelected(true);
			break;
		}
		
		// Set initial selection for view items
		ToggleGroup viewToggleGroup = new ToggleGroup();
		explorerMenuItem.setToggleGroup(viewToggleGroup);
		currentlyPlayingMenuItem.setToggleGroup(viewToggleGroup);
		switch (MusicPlayerApplication.getCurrentView()) {
		case EXPLORER:
			explorerMenuItem.setSelected(true);
			break;
		case CURRENTLY_PLAYING:
			currentlyPlayingMenuItem.setSelected(true);
			break;
		}
		
		// Populate UI size menu
		ToggleGroup uiSizeGroup = new ToggleGroup();
		for (UISize uiSize : UISize.values()) {
			RadioMenuItem newUISizeMenuItem = new RadioMenuItem(uiSize.toString());
			newUISizeMenuItem.setToggleGroup(uiSizeGroup);
			if (MusicPlayerApplication.getCurUISize() == uiSize)
				newUISizeMenuItem.setSelected(true);
			newUISizeMenuItem.setOnAction(event -> { setUISize(event, uiSize); });
			uiSizeMenu.getItems().add(newUISizeMenuItem);
		}
		
		// Populate theme menu
		ToggleGroup themeGroup = new ToggleGroup();
		for (Theme theme : Theme.values()) {
			RadioMenuItem newThemeMenuItem = new RadioMenuItem(theme.toString());
			newThemeMenuItem.setToggleGroup(themeGroup);
			if (MusicPlayerApplication.getCurTheme() == theme)
				newThemeMenuItem.setSelected(true);
			newThemeMenuItem.setOnAction(event -> { setTheme(event, theme); });
			themeMenu.getItems().add(newThemeMenuItem);
		}
	}
	
	
	
	// --- File ---
	
	@FXML
	private void exit(ActionEvent event) {
		Platform.exit();
	}
	
	@FXML
	private void selectRootFolder(ActionEvent event) {
		MusicPlayerApplication.selectRootFolder();
	}
	
	
	
	// --- Play ---
	
	@FXML
	private void playPause(ActionEvent event) {
		player.togglePlay();
	}
	
	@FXML
	private void fastForward(ActionEvent event) {
		player.move(Duration.seconds(TIME_INTERVAL).toMillis());
	}
	
	@FXML
	private void rewind(ActionEvent event) {
		player.move(-Duration.seconds(TIME_INTERVAL).toMillis());
	}
	
	@FXML
	private void restart(ActionEvent event) {
		player.restartMedia();
	}
	
	@FXML
	private void stop(ActionEvent event) {
		player.stopMedia();
	}
	
	@FXML
	private void previous(ActionEvent event) {
		player.previous();
	}
	
	@FXML
	private void next(ActionEvent event) {
		player.next();
	}
	
	@FXML
	private void volumeUp(ActionEvent event) {
		player.changeVolume(
				Math.min(player.getVolume() + VOLUME_INTERVAL, 1.0));
	}
	
	@FXML
	private void volumeDown(ActionEvent event) {
		player.changeVolume(
				Math.max(player.getVolume() - VOLUME_INTERVAL, 0.0));
	}
	
	@FXML
	private void rateQuarter(ActionEvent event) {
		MusicPlayerApplication.setRateEnum(Rate.QUARTER);
	}
	
	@FXML
	private void rateHalf(ActionEvent event) {
		MusicPlayerApplication.setRateEnum(Rate.HALF);
	}
	
	@FXML
	private void rateNormal(ActionEvent event) {
		MusicPlayerApplication.setRateEnum(Rate.NORMAL);
	}
	
	@FXML
	private void rateDouble(ActionEvent event) {
		MusicPlayerApplication.setRateEnum(Rate.DOUBLE);
	}
	
	@FXML
	private void rateQuadruple(ActionEvent event) {
		MusicPlayerApplication.setRateEnum(Rate.QUADRUPLE);
	}
	
	@FXML
	private void balance100Left(ActionEvent event) {
		MusicPlayerApplication.setBalanceEnum(Balance.FULL_LEFT);
	}
	
	@FXML
	private void balance50Left(ActionEvent event) {
		MusicPlayerApplication.setBalanceEnum(Balance.HALF_LEFT);
	}
	
	@FXML
	private void balanceCenter(ActionEvent event) {
		MusicPlayerApplication.setBalanceEnum(Balance.CENTER);
	}
	
	@FXML
	private void balance50Right(ActionEvent event) {
		MusicPlayerApplication.setBalanceEnum(Balance.HALF_RIGHT);
	}
	
	@FXML
	private void balance100Right(ActionEvent event) {
		MusicPlayerApplication.setBalanceEnum(Balance.FULL_RIGHT);
	}
	
	@FXML
	private void switchToExplorerView(ActionEvent event) {
		MusicPlayerApplication.switchView(View.EXPLORER);
	}
	
	@FXML
	private void switchToCurrentlyPlayingView(ActionEvent event) {
		MusicPlayerApplication.switchView(View.CURRENTLY_PLAYING);
	}
	
	private void setTheme(ActionEvent event, Theme theme) {
		MusicPlayerApplication.setTheme(theme);
	}
	
	private void setUISize(ActionEvent event, UISize uiSize) {
		MusicPlayerApplication.setUISize(uiSize);
	}
}
