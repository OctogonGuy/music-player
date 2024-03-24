package tech.octopusdragon.musicplayer.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;

public class Util {
	
	// File types
	public static final String[] MUSIC_FILE_EXTENTIONS = {
			".mp3", ".m4a" // ".aiff", ".wav", ".flac", ".ogg"
	};
	
	// Playlist types
	public static final String[] PLAYLIST_FILE_EXTENTIONS = {
			".m3u", ".m3u8"
	};
	
	// Array of genres whose index corresponds to genre codes in tags
	public static final String[] GENRE_ARRAY = {
	"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge",
    "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B",
    "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska",
    "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient",
    "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical",
    "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise",
    "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative",
    "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
    "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream",
    "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap",
    "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave",
    "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal",
    "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll",
    "Hard Rock",
    // These were made up by the authors of Winamp but backported into
    // the ID3 spec.
    "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion",
    "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde",
    "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock",
    "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour",
    "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony",
    "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club",
    "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul",
    "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House",
    "Dance Hall",
    // These were also invented by the Winamp folks but ignored by the
    // ID3 authors.
    "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie",
    "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap",
    "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian",
    "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "J-Pop",
    "Synthpop" };

	
	/**
	 * @return the folder image as a buffered input stream
	 * @param imagePath The image path
	 */
	public static byte[] getImage(String imagePath) {
		InputStream is = Util.class.getClassLoader().getResourceAsStream(
				imagePath);
		byte[] byteArray = null;
		try {
			byteArray = IOUtils.toByteArray(is);
		} catch (IOException e) {
			System.out.println("Error: problem reading folder image");
			e.printStackTrace();
			System.exit(1);
		}
		return byteArray;
	}
	
	
	public static String parseGenre(String genreString) {
		
		// Get the number
		String newGenreString;
		try {
			int genreNum = Integer.parseInt(
					genreString.replace("(", "").replace(")", ""));
			newGenreString = GENRE_ARRAY[genreNum];
		} catch (NumberFormatException e) {
			return genreString;
		}
		
		return newGenreString;
	}
	
	
	/**
	 * Returns the file extension of the given file.	
	 * @param file The file.
	 * @return The file extension starting with the dot.
	 */
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}
	
	
	/**
	 * Returns the file extension of the given file path.	
	 * @param file The file.
	 * @return The file extension starting with the dot.
	 */
	public static String getFileExtension(String path) {
		int lastIndexOf = path.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return "";	// return blank string if no extension
		}
		return path.substring(lastIndexOf);
	}
	
	
	/**
	 * Shuffles the contents of a list using the Fisher-Yates shuffle algorithm.
	 * @param list The list.
	 */
	public static <T extends Object> void shuffleList(List<T> list) {
		// Create Random object to generate random numbers.
		Random random = new Random();
		
		// To shuffle an array a of n elements (indices 0..n-1)
		int n = list.size();	// Number of elements
		int i;					// Index of element to switch
		int j;					// Index of random element to switch with
		T temp;
		for (i = n - 1; i >= 1; i--) {
			j = random.nextInt(i + 1);
			temp = list.get(j);
			list.set(j, list.get(i));
			list.set(i, temp);
		}
	}
	
}
