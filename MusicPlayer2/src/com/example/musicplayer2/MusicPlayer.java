package com.example.musicplayer2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Application;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.example.musicplayer2.utils.Utils;

public class MusicPlayer extends Application {
	private MediaPlayer mp;
	private ArrayList<Song> songList;
	private int current;

	private boolean onRepeat;
	private boolean onShuffle;
	private boolean paused;
	private String lastPath;

	private SharedPreferences preferences;

	@Override
	public void onCreate() {
		if (mp == null) {
			mp = new MediaPlayer();
		}
		if (songList == null) {
			songList = new ArrayList<Song>();
		}
		preferences = getSharedPreferences(getPackageName(), 0);
		current = preferences.getInt("current", 0);
		onRepeat = preferences.getBoolean("onRepeat", false);
		onShuffle = preferences.getBoolean("onShuffle", false);
		lastPath = preferences.getString("lastPath",
				"/storage/emulated/0/Music/");
		paused = false;

		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (onRepeat) {
					play();
				} else if (onShuffle) {
					current = new Random().nextInt(songList.size());
				} else {
					if (current < songList.size() - 1) {
						current++;
					} else {
						current = 0;
					}
					play();
				}
			}

		});

	}
	
	public void play() {
		mp.reset();
		setDataSource(songList.get(current).getPath());
		mp.start();
		paused = false;
	}

	public void start() {
		mp.start();
	}

	public void pause() {
		mp.pause();
	}

	public boolean isPlaying() {
		return mp.isPlaying();
	}

	public void setDataSource(String path) {
		try {
			mp.setDataSource(path);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void prepare() {
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getCurrentPosition() {
		return mp.getCurrentPosition();
	}

	public int getDuration() {
		return mp.getDuration();
	}

	public void seekTo(int position) {
		mp.seekTo(position);
	}

	public void reset() {
		mp.reset();
	}

	public void release() {
		mp.release();
	}

	public void loadSongs() {
		Utils.loadSongs(songList);
	}

	public void loadSongs(String path) {
		Utils.loadSongs(songList, path);
	}

	public int size() {
		return songList.size();
	}

	public Song get(int index) {
		if (index > songList.size() - 1) {
			loadSongs(lastPath);
		}
		return songList.get(index);
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public int getCurrent() {
		return current;
	}

	public boolean isOnRepeat() {
		return onRepeat;
	}

	public void setOnRepeat(boolean onRepeat) {
		this.onRepeat = onRepeat;
	}

	public boolean isOnShuffle() {
		return onShuffle;
	}

	public void setOnShuffle(boolean onShuffle) {
		this.onShuffle = onShuffle;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void saveChanges() {
		SharedPreferences.Editor edit = preferences.edit();
		edit.putInt("current", current);
		edit.putBoolean("onRepeat", onRepeat);
		edit.putBoolean("onShuffle", onShuffle);
		edit.putString(
				"lastPath",
				songList.get(current).getPath().substring(
				0,songList.get(current).getPath().lastIndexOf("/")));
		edit.commit();
	}
}
