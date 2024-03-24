package tech.octopusdragon.musicplayer.tools;

import java.net.URL;

import tech.octopusdragon.musicplayer.util.Resource;

/**
 * Represents a possible size of text and graphics in the program
 * @author Alex Gill
 *
 */
public enum UISize {
	
	SMALL(Resource.SMALL_STYLESHEET),
	MEDIUM_SMALL(Resource.MEDIUM_SMALL_STYLESHEET),
	MEDIUM(Resource.MEDIUM_STYLESHEET),
	MEDIUM_LARGE(Resource.MEDIUM_LARGE_STYLESHEET),
	LARGE(Resource.LARGE_STYLESHEET);
	
	
	// --- Instance variables ---
	private Resource resource;	// The stylesheet resource
	
	
	/**
	 * Constructs a ui size
	 * @param resource The stylesheet resource
	 */
	private UISize(Resource resource) {
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
