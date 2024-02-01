package tech.octopusdragon.musicplayer;

import tech.octopusdragon.musicplayer.application.tools.Userdata;

/**
 * A directory in which the user can select albums to add to the album collection
 * @author Alex Gill
 *
 */
public class ChooseAlbumsDirectory extends Directory {
	
	private static final long serialVersionUID = 6046205992283247877L;

	public ChooseAlbumsDirectory(String name, AlbumCollection parent) {
		super(name, parent);
		this.type = DirectoryType.CHOOSE_ALBUMS_DIRECTORY;
		
		// Add album directory
		Directory albumDirectory = Userdata.
				getRootDirectories().getAlbumDirectory();
		Directory newAlbumDirectory = new Directory("Albums", this);
		for (Directory album : albumDirectory.getDirectories()) {
			Album newAlbum = new Album((Album)album);
			newAlbum.setParent(newAlbumDirectory);
			newAlbumDirectory.getDirectories().add(newAlbum);
		}
		getDirectories().add(newAlbumDirectory);
		
		// Add artist directory
		Directory artistDirectory = Userdata.
				getRootDirectories().getArtistDirectory();
		Directory newArtistDirectory = new Directory("Artists", this);
		for (Directory artist : artistDirectory.getDirectories()) {
			Artist newArtist = new Artist((Artist)artist);
			newArtist.setParent(newArtistDirectory);
			newArtistDirectory.getDirectories().add(newArtist);
		}
		getDirectories().add(newArtistDirectory);
		
		// Add genre directory
		Directory genreDirectory = Userdata.
				getRootDirectories().getGenreDirectory();
		Directory newGenreDirectory = new Directory("Genres", this);
		for (Directory genre : genreDirectory.getDirectories()) {
			Genre newGenre = new Genre((Genre)genre);
			newGenre.setParent(newGenreDirectory);
			newGenreDirectory.getDirectories().add(newGenre);
		}
		getDirectories().add(newGenreDirectory);
	}

}
