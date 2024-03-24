package tech.octopusdragon.musicplayer.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import tech.octopusdragon.musicplayer.MusicPlayerApplication;
import tech.octopusdragon.musicplayer.model.Directory;
import tech.octopusdragon.musicplayer.model.Song;
import tech.octopusdragon.musicplayer.tools.Userdata;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;


/**
 * A component that navigates through tracks in an album.
 * @author Alex Gill
 *
 */
public class TrackList extends TableView<Song> {
	
	// --- Variables ---
	private Directory directory;		// Current directory
	SortedList<Song> songs;	// Dir songs filtered sorted
	
	// --- UI components ---
	private List<TrackColumn<?>> tableColumns;	// Table columns
	
	// --- Table columns ---
	private TrackColumn<Integer> discColumn;
	private TrackColumn<Integer> trackColumn;
	private TrackColumn<String> titleColumn;
	private TrackColumn<String> albumColumn;
	private TrackColumn<String> artistColumn;
	private TrackColumn<String> albumArtistColumn;
	private TrackColumn<String> genreColumn;
	private TrackColumn<Integer> yearColumn;
	private TrackColumn<String> durationColumn;
	private TrackColumn<String> filePathColumn;
	private TrackColumn<String> filenameColumn;
	
	
	/**
	 * Instantiates a new track navigator starting at the default directory.
	 * @param app The music player application
	 */
	public TrackList() {
		super();
		this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.setId("track-list");
		this.setTableMenuButtonVisible(true);
		
		// Set variables
		directory = null;
		songs = MusicPlayerApplication.getMusicPlayer().getSortedDirSongs();
		
		// Make this have song rows as its rows
		this.setRowFactory(new Callback<TableView<Song>, TableRow<Song>>() {
			@Override
			public TableRow<Song> call(TableView<Song> p) {
				return new TrackRow();
			}
		});
		
		// Instantiate table columns
		tableColumns = new ArrayList<TrackColumn<?>>();
		
		discColumn = new TrackColumn<Integer>("Disc #");
		discColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, Integer>,
				ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<Song, Integer> p) {
				return new ReadOnlyObjectWrapper<Integer>(
						Integer.valueOf(p.getValue().getDisc()));
			}
		});
		tableColumns.add(discColumn);
		trackColumn = new TrackColumn<Integer>("Track #");
		trackColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, Integer>,
				ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<Song, Integer> p) {
				return new ReadOnlyObjectWrapper<Integer>(
						Integer.valueOf(p.getValue().getTrack()));
			}
		});
		tableColumns.add(trackColumn);
		titleColumn = new TrackColumn<String>("Title");
		titleColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getTitle());
			}
		});
		tableColumns.add(titleColumn);
		albumColumn = new TrackColumn<String>("Album");
		albumColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getAlbumStr());
			}
		});
		tableColumns.add(albumColumn);
		artistColumn = new TrackColumn<String>("Artist");
		artistColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getArtist());
			}
		});
		tableColumns.add(artistColumn);
		albumArtistColumn = new TrackColumn<String>("Album Artist");
		albumArtistColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getAlbumArtist());
			}
		});
		tableColumns.add(albumArtistColumn);
		genreColumn = new TrackColumn<String>("Genre");
		genreColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getGenre());
			}
		});
		tableColumns.add(genreColumn);
		yearColumn = new TrackColumn<Integer>("Year");
		yearColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, Integer>,
				ObservableValue<Integer>>() {
			@Override
			public ObservableValue<Integer> call(
					CellDataFeatures<Song, Integer> p) {
				return new ReadOnlyObjectWrapper<Integer>(
						Integer.valueOf(p.getValue().getYear()));
			}
		});
		tableColumns.add(yearColumn);
		durationColumn = new TrackColumn<String>("Duration");
		durationColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getDuration());
			}
		});
		tableColumns.add(durationColumn);
		filenameColumn = new TrackColumn<String>("Filename");
		filenameColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getFilename());
			}
		});
		tableColumns.add(filenameColumn);
		filePathColumn = new TrackColumn<String>("File Path");
		filePathColumn.setCellValueFactory(new Callback<
				CellDataFeatures<Song, String>,
				ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(
					CellDataFeatures<Song, String> p) {
				return new ReadOnlyObjectWrapper<String>(
						p.getValue().getPath());
			}
		});
		tableColumns.add(filePathColumn);
		
		this.getColumns().addAll(tableColumns);
		
		// Set items
		this.setItems(songs);
		songs.comparatorProperty().bind(this.comparatorProperty());
    	
		// Clear selection upon clicking in the directory view but not on a
    	// node
		this.setOnMouseClicked(event -> {
			this.getSelectionModel().clearSelection();
		});
		
		// When items are selected, update the music player's selected items
		MusicPlayerApplication.getMusicPlayer().setSelectedSongs(getSelectionModel().getSelectedItems());
		
		
		// Add listeners for important value changes and save to userdata
		Platform.runLater(() -> {
			for (TrackColumn<?> songColumn : tableColumns) {
				songColumn.widthProperty().addListener((obs, oldVal, newVal) -> {
					Userdata.writeProperty(
							propertyKeyString(directory, songColumn, "Width"),
							String.valueOf(songColumn.getWidth()));
				});
				
				songColumn.visibleProperty().addListener((obs, oldVal, newVal) -> {
					Userdata.writeProperty(
							propertyKeyString(directory, songColumn, "Visible"),
							String.valueOf(newVal));
				});
				
				this.getColumns().addListener((ListChangeListener<? super TableColumn<Song, ?>>) c -> {
					Userdata.writeProperty(
							propertyKeyString(directory, songColumn, "Index"),
							String.valueOf(this.getColumns().indexOf(songColumn)));
				});
			};
		});
	}
	
	
	/**
	 * Sets the track list's items to all the audio files in the given
	 * directory. If the type of directory is different, save the previous
	 * directory's type's userdata and load the new one's
	 * @param directory The directory
	 */
	public void newDirectory(Directory directory) {
		Directory oldDirectory = this.directory;
		Directory newDirectory = directory;
		if (oldDirectory == null) {
			this.directory = directory;
			readView();
		}
		else if (!oldDirectory.getClass().equals(newDirectory.getClass())) {
			this.directory = directory;
			readView();
		}
	}
	
	
	/**
	 * Sets the columns and column widths to the value in the userdata depending
	 * on the type of directory (playlist or album)
	 */
	private void readView() {
		TrackColumn<?>[] reorderedColumns = new TrackColumn<?>[tableColumns.size()];
		
		for (TrackColumn<?> songColumn : tableColumns) {
			String widthValue = Userdata.readProperty(
					propertyKeyString(directory, songColumn, "Width"));
			if (widthValue != null)
				songColumn.setPrefWidth(Double.parseDouble(widthValue));
			
			String visibleValue = Userdata.readProperty(
					propertyKeyString(directory, songColumn, "Visible"));
			if (visibleValue != null)
				songColumn.setVisible(Boolean.parseBoolean(visibleValue));
			else
				setDefaultVisibility();
			
			String indexValue = Userdata.readProperty(
					propertyKeyString(directory, songColumn, "Index"));
			if (indexValue != null)
				reorderedColumns[Integer.parseInt(indexValue)] = songColumn;
		};
		
		// Reorder the actual columns
		List<TrackColumn<?>> reorderedColumnsList = Arrays.asList(reorderedColumns);
		this.getColumns().sort(Comparator.comparing(item -> reorderedColumnsList.indexOf(item)));
	}
	
	
	/**
	 * Sets default visibility values
	 */
	private void setDefaultVisibility() {
		for (TrackColumn<?> songColumn : tableColumns) {
			songColumn.setVisible(false);
		}
		switch (directory.getType()) {
		case ALBUM:
			trackColumn.setVisible(true);
			titleColumn.setVisible(true);
			artistColumn.setVisible(true);
			durationColumn.setVisible(true);
			break;
		default: 
			titleColumn.setVisible(true);
			artistColumn.setVisible(true);
			albumColumn.setVisible(true);
			durationColumn.setVisible(true);
			break;
		}
	}
	
	
	/**
	 * Reutrns the property key of a property with the given information
	 * @param directory The directory
	 * @param column The column
	 * @param attribute The attribute
	 * @return The property key
	 */
	private static String propertyKeyString(Directory directory, TrackColumn<?> column, String attribute) {
		// Get type of directory
		String directoryTypeStr = directory.getType().toString();
		
		// Get name of column.
		String columnStr = column.getText();
		// Remove special character (#) and trim it
		columnStr = columnStr.replaceAll("#", "");
		columnStr = columnStr.trim();
		// Replace spaces with dashes
		columnStr = columnStr.replaceAll(" ", "-");
		
		// Combine all and make lowercase
		String propertyKeyStr = String.format("%s-%s-%s",
				directoryTypeStr,
				columnStr,
				attribute);
		propertyKeyStr = propertyKeyStr.toLowerCase();
		
		// Return the final product
		return propertyKeyStr;
	}

}
