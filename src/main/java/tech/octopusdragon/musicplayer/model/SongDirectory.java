package tech.octopusdragon.musicplayer.model;

/**
 * Represents a directory with all songs and no subdirectories
 * @author Alex Gill
 *
 */
public class SongDirectory extends Directory {
	private static final long serialVersionUID = -2630100206382766281L;

	public SongDirectory(String name) {
		this(name, null);
	}

	public SongDirectory(String name, Directory parent) {
		super(name, parent);
		this.type = DirectoryType.SONG;
	}

}
