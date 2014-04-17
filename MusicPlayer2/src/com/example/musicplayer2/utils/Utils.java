package com.example.musicplayer2.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;

import com.example.musicplayer2.Song;

public class Utils {
	
	static String mediaPath = "/storage/emulated/0/Music/";
	
	public static void loadSongs(ArrayList<Song> songList) {
		if(songList == null) {
			songList = new ArrayList<Song>();
		}
		File songs = new File(mediaPath);
		if(songs.listFiles().length > 0) {
			for(File song : songs.listFiles()) {
				Song melody = new Song();
				melody.setPath(song.getAbsolutePath());
				try {
					melody.setAuthor(song.getName().substring(0, song.getName().indexOf(" - ")));
				} catch(Exception e) {
					melody.setAuthor("Unknown");
				}
				try {
					int songNameStart = song.getName().indexOf(" - ") + 3;
					int songNameEnd = song.getName().lastIndexOf(".");
					melody.setTitle(song.getName().substring(songNameStart, songNameEnd));
				} catch(Exception e) {
					melody.setTitle("Unknown");
				}
				Log.i("MusicPlayer", melody.getAuthor() + " - " + melody.getTitle());
				songList.add(melody);
			}
		}
	}
	
	public static void loadSongs(ArrayList<Song> songList, String path) {
		if(songList == null) {
			songList = new ArrayList<Song>();
		}
		File songs = new File(path);
		if(songs.listFiles().length > 0) {
			for(File song : songs.listFiles()) {
				Song melody = new Song();
				melody.setPath(song.getAbsolutePath());
				try {
					melody.setAuthor(song.getName().substring(0, song.getName().indexOf(" - ")));
				} catch(Exception e) {
					melody.setAuthor("Unknown");
				}
				try {
					int songNameStart = song.getName().indexOf(" - ") + 3;
					int songNameEnd = song.getName().lastIndexOf(".");
					melody.setTitle(song.getName().substring(songNameStart, songNameEnd));
				} catch(Exception e) {
					melody.setTitle("Unknown");
				}
				Log.i("MusicPlayer", melody.getAuthor() + " - " + melody.getTitle());
				songList.add(melody);
			}
		}
	}
	
	public static String getDuration(int miliseconds) {
		int seconds = miliseconds / 1000;
		if (seconds > 3599) {
			// TODO: fix this
			int hours = seconds / 3600;
			int minutes = seconds % 3600 / 60;
			int secs = seconds % 3600 % 60;
			return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, secs);
		} else {
			return String.format(Locale.ENGLISH, "%d:%02d", seconds / 60, seconds % 60);
		}
	}
}
