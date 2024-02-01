package tech.octopusdragon.musicplayer;

/**
 * A directory which stores playlists not of songs but of albums
 * @author Alex Gill
 *
 */
public class AlbumCollection extends Directory {
	private static final long serialVersionUID = 1819060191528291479L;

	public AlbumCollection(String name) {
		this(name, null);
	}

	public AlbumCollection(String name, Directory parent) {
		super(name, parent);
		this.type = DirectoryType.ALBUM_COLLECTION;
	}
	
	/**
	 * Sets the name of the album collection
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
