# Music Player

A media player for your music library.

## Overview

This music player has all you can expect: Audio playback, a user-friendly interface, navigation of artists, albums, genres, playlists, and more.

To set this music player apart, a novel concept called _**album collections**_ are included. As the name implies, album collections are essentially playlists of albums. You create a list of albums that you want to play through or shuffle and play random songs from those albums.

## Features

The following features are included:

- **Standard controls** for media playback such as pause/play, stop, restart, skip forward, skip backward, scrubbing, volume, balance, mute, playback rate, shuffle, replay, replay single, etc.
- **Navigation**: Go through songs, albums, artists, genres, folders, playlists, and album collections
- **Track lists**: See each track listing, and for each: Track number, title, album, artist, genre, year, duration, filename, etc.
  - **Sort** by any of the above properties.
- **View**: Switch between the navigation view and album art view.
- **Search** through your library for songs.
- **UI customization**: Change the theme of which there are several, and change the size of text and the overall UI.
- **Album collections**: Create album collections to listen to your favorite albums.

## Setup & Usage

### Prerequisites

You must have the following installed on your computer to run the application:

- Java JDK (version 21+)

### Installation

Perform the following steps to be able to run the application:

1. Clone the repository.
2. Navigate to the repository.
3. Add execute permissions to `mvnw`.
   ```
   chmod +x mvnw
   ```

### Running

In the directory of the repository, run the following command:

```
./mvnw javafx:run
```

## Demo

Here is a demonstration of the music player in action:

[![Music Player Demo](https://img.youtube.com/vi/4-CZNdzjmns/0.jpg)](https://www.youtube.com/watch?v=4-CZNdzjmns)

## Notes

- Search only encompasses song names.
- While playlists can be read, they currently cannot be created within the application.
- Currently supported audio formats: .mp3, .m4a
- Currently supported playlist formats: .m3u, .m3u8
