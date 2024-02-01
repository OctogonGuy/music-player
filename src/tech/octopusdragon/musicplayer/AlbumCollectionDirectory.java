package tech.octopusdragon.musicplayer;

/**
 * Represents a collection of album collections
 * @author Alex Gill
 *
 */
public class AlbumCollectionDirectory extends Directory {
	private static final long serialVersionUID = -8386296817845792753L;

	public AlbumCollectionDirectory(String name) {
		this(name, null);
	}

	public AlbumCollectionDirectory(String name, Directory parent) {
		super(name, parent);
		this.type = DirectoryType.ALBUM_COLLECTION_DIRECTORY;
	}

}
