package tech.octopusdragon.musicplayer.components;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tech.octopusdragon.musicplayer.model.Song;

public class TrackColumn<T> extends TableColumn<Song, T> {

	public TrackColumn() {
		super();
		createSongCellFactory();
	}
	
	public TrackColumn(String text) {
		super(text);
		createSongCellFactory();
	}
	
	/**
	 * Makes this song column have song cells as its cells
	 */
	private void createSongCellFactory() {
		super.setCellFactory(
				new Callback<TableColumn<Song, T>, TableCell<Song, T>>() {
			@Override
			public TableCell<Song, T> call(TableColumn<Song, T> p) {
				return new TrackCell<T>();
			}
		});
	}
}
