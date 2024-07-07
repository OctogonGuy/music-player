package tech.octopusdragon.musicplayer.util;

import java.io.InputStream;
import java.net.URL;

/**
 * Contains resource information
 * @author Alex Gill
 *
 */
public enum Resource {
	ICON("/images/icon.png"),
	DIRECTORY_IMAGE("/images/directory.png"),
	FOLDER_IMAGE("/images/folder.png"),
	ALBUM_IMAGE("/images/album.png"),
	ARTIST_IMAGE("/images/artist.png"),
	GENRE_IMAGE("/images/genre.png"),
	PLAYLIST_IMAGE("/images/playlist.png"),
	ALBUM_COLLECTION_IMAGE("/images/album_collection.png"),
	PLUS_IMAGE("/images/plus.png"),
	NOW_PLAYING_IMAGE("/images/now_playing.png"),
	
	MAIN_STYLESHEET("/stylesheets/main.css"),
	LIGHT_STYLESHEET("/stylesheets/themes/light.css"),
	DARK_STYLESHEET("/stylesheets/themes/dark.css"),
	BLUE_STYLESHEET("/stylesheets/themes/blue.css"),
	RED_STYLESHEET("/stylesheets/themes/red.css"),
	GREEN_STYLESHEET("/stylesheets/themes/green.css"),
	ORANGE_STYLESHEET("/stylesheets/themes/orange.css"),
	PURPLE_STYLESHEET("/stylesheets/themes/purple.css"),
	YELLOW_STYLESHEET("/stylesheets/themes/yellow.css"),
	SPOOKY_STYLESHEET("/stylesheets/themes/spooky.css"),
	MERRY_STYLESHEET("/stylesheets/themes/merry.css"),
	SAKURA_STYLESHEET("/stylesheets/themes/sakura.css"),
	FUJI_STYLESHEET("/stylesheets/themes/fuji.css"),
	OCTOGON_STYLESHEET("/stylesheets/themes/octogon.css"),
	
	SMALL_STYLESHEET("/stylesheets/ui_sizes/small.css"),
	MEDIUM_SMALL_STYLESHEET("/stylesheets/ui_sizes/medium_small.css"),
	MEDIUM_STYLESHEET("/stylesheets/ui_sizes/medium.css"),
	MEDIUM_LARGE_STYLESHEET("/stylesheets/ui_sizes/medium_large.css"),
	LARGE_STYLESHEET("/stylesheets/ui_sizes/large.css"),

	JUA("/fonts/Jua-Regular.ttf"),
	KOSUGI_MARU("/fonts/KosugiMaru-Regular.ttf"),
	VARELA_ROUND("/fonts/VarelaRound-Regular.ttf");
	
	// --- Instance variables ---
	private String path;	// The path to the resource file
	
	/**
	 * Initializes resource information
	 * @param path The path to the resource file
	 */
	private Resource(String path) {
		this.path = path;
	}
	
	/**
	 * @return The path of the resource
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * @return The resource as a URL
	 */
	public URL getResource() {
		return Resource.class.getResource(path);
	}
	
	/**
	 * @return The resource as an input stream
	 */
	public InputStream getResourceAsStream() {
		return Resource.class.getResourceAsStream(path);
	}
}
