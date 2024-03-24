package tech.octopusdragon.musicplayer.model;

import java.io.FileNotFoundException;

/**
 * Represents a folder.
 * @author Alex Gill
 *
 */
public class Folder extends Directory {
	
	private static final long serialVersionUID = 5713845695048686120L;
	
	// The path of the folder
	private String path;
	
	
	/**
	 * Instantiates a new folder
	 * @param name The name that will show up in navigator and history
	 * @throws FileNotFoundException If the folder image is not found
	 */
	public Folder(String path, String name) {
		super(name);
		this.type = DirectoryType.FOLDER;
		this.path = path;
	}
	
	
	/**
	 * Instantiates a new folder
	 * @param path The path of the folder
	 * @param name The name that will show up in navigator and history
	 * @param parent The parent directory
	 * @throws FileNotFoundException If the folder image is not found
	 */
	public Folder(String path, String name, Directory parent) {
		super(name, parent);
		this.type = DirectoryType.FOLDER;
		this.path = path;
	}
	
	
	/**
	 * @return the path of the folder
	 */
	public String getPath() {
		return path;
	}
}
