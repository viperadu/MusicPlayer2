package com.example.musicplayer2.utils;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

import android.os.AsyncTask;
import android.util.Log;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Track;
import com.example.musicplayer2.MainActivity;
import com.example.musicplayer2.Song;

public class TrackAnalyser extends Observable {
	private final String ECHO_NEST_API_KEY = "BNMWPUJJLIPJQURHZ";
	private EchoNestAPI en = null;
	
	public TrackAnalyser() {
		if(en == null) {
			en = new EchoNestAPI(ECHO_NEST_API_KEY);
		}
	}
	
	public void getTrackDetails(Song song) {
		Log.i(MainActivity.TAG, "Attempting to retrieve track details.");
		new getTrackInfo().execute(song);
	}
	
	public class getTrackInfo extends AsyncTask<Song, Void, String> {
		@Override
		protected String doInBackground(Song... arg0) {
			long startTime = System.nanoTime();
			String trackDetails = "";
			File song = new File(arg0[0].getPath());
			Log.i(MainActivity.TAG, song.exists() ? "true" : "false");
			try {
				
				Track track = en.uploadTrack(song);
				track.waitForAnalysis(60000);
                if (track.getStatus() == Track.AnalysisStatus.COMPLETE) {
                	trackDetails = track.getArtistName() + " - " + track.getTitle();
                	Log.i(MainActivity.TAG, "Analysis URL: " + track.getAnalysisURL());
                	Log.i(MainActivity.TAG, "Audio MD5: " + track.getAudioMD5());
                	Log.i(MainActivity.TAG, "Audio URL: " + track.getAudioUrl());
                	Log.i(MainActivity.TAG, "Danceability: " + track.getDanceability());
                	Log.i(MainActivity.TAG, "Duration: " + track.getDuration());
                	Log.i(MainActivity.TAG, "Energy: " + track.getEnergy());
                	Log.i(MainActivity.TAG, "Foreign ID: " + track.getForeignID());
                	Log.i(MainActivity.TAG, "ID: " + track.getID());
                	Log.i(MainActivity.TAG, "Key: " + track.getKey());
                	Log.i(MainActivity.TAG, "Liveness: " + track.getLiveness());
                	Log.i(MainActivity.TAG, "Loudness: " + track.getLoudness());
                	Log.i(MainActivity.TAG, "Mode: " + track.getMode());
                	Log.i(MainActivity.TAG, "Original ID: " + track.getOriginalID());
                	Log.i(MainActivity.TAG, "Preview URL: " + track.getPreviewUrl());
                	Log.i(MainActivity.TAG, "Release name: " + track.getReleaseName());
                	Log.i(MainActivity.TAG, "Song ID: " + track.getSongID());
                	Log.i(MainActivity.TAG, "Speechiness: " + track.getSpeechiness());
                	Log.i(MainActivity.TAG, "Status: " + track.getStatus());
                	Log.i(MainActivity.TAG, "Tempo: " + track.getTempo());
                	Log.i(MainActivity.TAG, "Time signature: " + track.getTimeSignature());
                } else {
//                    System.err.println("Trouble analysing track " + track.getStatus());
                	Log.e(MainActivity.TAG, "Trouble analysing track " + track.getStatus());
                }
			} catch (EchoNestException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.e(MainActivity.TAG, e.getMessage());
			}
			long endTime = System.nanoTime();

			Log.i(MainActivity.TAG, "Time passed to retrieve data: " + ((endTime - startTime)/1000000000.0) + "s.");
			return trackDetails;
		}
		
		protected void onPostExecute(String trackDetails) {
			setChanged();
			notifyObservers(trackDetails);
		}
		
	}
}
