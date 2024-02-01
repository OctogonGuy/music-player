package tech.octopusdragon.musicplayer.application.tools;

import java.net.URL;

import tech.octopusdragon.musicplayer.util.Resource;

/**
 * Represents a customizable theme of the application UI
 * @author Alex Gill
 *
 */
public enum Theme {
	
	LIGHT(Resource.LIGHT_STYLESHEET),
	DARK(Resource.DARK_STYLESHEET),
	BLUE(Resource.BLUE_STYLESHEET),
	RED(Resource.RED_STYLESHEET),
	GREEN(Resource.GREEN_STYLESHEET),
	ORANGE(Resource.ORANGE_STYLESHEET),
	PURPLE(Resource.PURPLE_STYLESHEET),
	YELLOW(Resource.YELLOW_STYLESHEET),
	SPOOKY(Resource.SPOOKY_STYLESHEET),
	MERRY(Resource.MERRY_STYLESHEET),
	SAKURA(Resource.SAKURA_STYLESHEET),
	FUJI(Resource.FUJI_STYLESHEET),
	OCTOGON(Resource.OCTOGON_STYLESHEET);
	
	
	// --- Instance variables ---
	private Resource resource;	// The stylesheet resource
	
	
	/**
	 * Constructs a theme
	 * @param resource The stylesheet resource
	 */
	private Theme(Resource resource) {
		this.resource = resource;
	}
	
	
	/**
	 * @return The URL of the theme stylesheet
	 */
	public URL getResource() {
		return resource.getResource();
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.name().toLowerCase());
		// Replace underscores with spaces
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '_') {
				sb.replace(i, i + 1, " ");
			}
		}
		// Capitalize
		sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
		for (int i = 0; i < sb.length(); i++) {
			if (i >= 1 && sb.charAt(i - 1) == ' ') {
				sb.replace(i, i + 1, sb.substring(i, i + 1).toUpperCase());
			}
		}
		return sb.toString();
	}
}
