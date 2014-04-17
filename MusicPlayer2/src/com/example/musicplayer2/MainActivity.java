package com.example.musicplayer2;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicplayer2.utils.TrackAnalyser;
import com.example.musicplayer2.utils.Utils;

public class MainActivity extends Activity implements OnSeekBarChangeListener,
		Observer {

	public static final String TAG = "MusicPlayer";

	private MusicPlayer mp;

	private Button left;
	private Button play;
	private Button pause;
	private Button right;

	private TextView songTitle;
	private TextView currentDuration;
	private TextView totalDuration;

	private SeekBar songProgress;

	private ImageView image;

	private Handler mHandler;

	private float x1, x2, y1, y2;

	private TrackAnalyser trackAnalyser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mp = ((MusicPlayer) getApplicationContext());

		mHandler = new Handler();
		;

		// TODO: only the first time. after this, load the list from the memory
		mp.loadSongs();

		left = (Button) findViewById(R.id.left);
		leftClickListener();

		play = (Button) findViewById(R.id.play);
		playClickListener();

		pause = (Button) findViewById(R.id.pause);
		pauseClickListener();

		right = (Button) findViewById(R.id.right);
		rightClickListener();

		songTitle = (TextView) findViewById(R.id.title);
		songTitle.setSelected(true);

		currentDuration = (TextView) findViewById(R.id.currentDuration);
		currentDuration.setText("0:00");

		totalDuration = (TextView) findViewById(R.id.totalDuration);

		songProgress = (SeekBar) findViewById(R.id.progressBar);
		songProgress.setMax(500);
		songProgress.setOnSeekBarChangeListener(this);

		image = (ImageView) findViewById(R.id.imageView1);
		x1 = 0.0f;
		x2 = 0.0f;
		y1 = 0.0f;
		y2 = 0.0f;

		if (mp.size() > 0) {
			songTitle.setText(mp.get(mp.getCurrent()).getAuthor() + " - "
					+ mp.get(mp.getCurrent()).getTitle());
			loadTotalDuration(mp.getCurrent());
		} else {
			songTitle.setText("No songs found in default directory");
			totalDuration.setText("0:00");
		}

		trackAnalyser = new TrackAnalyser();
		trackAnalyser.addObserver(this);
	}

	private void leftClickListener() {
		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				leftClickListenerAction();
			}
		});
	}

	private void leftClickListenerAction() {
		if (mp.getCurrent() == 0) {
			mp.setCurrent(mp.size() - 1);
		} else {
			mp.setCurrent(mp.getCurrent() - 1);
		}
		if (mp.isPlaying()) {
			play(mp.getCurrent());
		} else {
			songTitle.setText(mp.get(mp.getCurrent()).getAuthor() + " - "
					+ mp.get(mp.getCurrent()).getTitle());
			loadTotalDuration(mp.getCurrent());
		}
	}

	private void playClickListener() {
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp.isPaused()) {
					mp.start();
					mp.setPaused(false);
				} else {
					play(mp.getCurrent());
					mp.setPaused(false);
				}
			}
		});
	}

	private void pauseClickListener() {
		pause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp.isPlaying()) {
					mp.pause();
					mp.setPaused(true);
				} else {
					if (mp.getCurrentPosition() > 0) {
						mp.start();
						mp.setPaused(false);
					}
				}
			}
		});
	}

	private void rightClickListener() {
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rightClickListenerAction();
			}
		});
	}

	private void rightClickListenerAction() {
		if (mp.getCurrent() == mp.size() - 1) {
			mp.setCurrent(0);
		} else {
			mp.setCurrent(mp.getCurrent() + 1);
		}
		if (mp.isPlaying()) {
			play(mp.getCurrent());
		} else {
			songTitle.setText(mp.get(mp.getCurrent()).getAuthor() + " - "
					+ mp.get(mp.getCurrent()).getTitle());
			loadTotalDuration(mp.getCurrent());
		}
	}

	private void play(int index) {
		songTitle.setText(mp.get(index).getAuthor() + " - "
				+ mp.get(index).getTitle());
		loadSong(index);
		mp.start();
		totalDuration.setText(Utils.getDuration(mp.getDuration()));
		songProgress.setProgress(0);
		updateSongProgress();
	}

	private void loadSong(int index) {
		mp.reset();
		mp.setDataSource(mp.get(index).getPath());
		mp.prepare();
	}

	private void updateSongProgress() {
		Log.i(TAG, "update in progress");
		mHandler.postDelayed(mUpdateTimeTask, 25);
		triggerWidgetUpdate();
	}

	private void triggerWidgetUpdate() {
		Intent intent = new Intent(this, MusicPlayerWidget.class);
		intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int[] ids = AppWidgetManager.getInstance(getApplication())
				.getAppWidgetIds(
						new ComponentName(getApplication(),
								MusicPlayerWidget.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(intent);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long currentPosition = mp.getCurrentPosition();
			int progress = (int) (currentPosition * 500 / mp.getDuration());
			currentDuration.setText(Utils.getDuration(mp.getCurrentPosition()));
			songProgress.setProgress(progress);
			mHandler.postDelayed(this, 25);
		}
	};

	private void loadTotalDuration(int current) {
		Log.i(TAG, "Loading current song into music player");
		loadSong(current);
		Log.i(TAG, "Presumably everything loaded successfully");
		totalDuration.setText(Utils.getDuration(mp.getDuration()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.add(1, Menu.FIRST, Menu.FIRST, "Analyse track");
		menu.add(1, Menu.FIRST + 1, Menu.FIRST + 1, "Shuffle");
		menu.add(1, Menu.FIRST + 2, Menu.FIRST + 2, "Random");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			trackAnalyser.getTrackDetails(mp.get(mp.getCurrent()));
			break;
		case 2:
			mp.setOnShuffle(!mp.isOnShuffle());
			break;
		case 3:
			mp.setOnRepeat(!mp.isOnRepeat());
			break;
		}
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int newPosition = seekBar.getProgress();
		int totalMilSec = mp.getDuration();
		int milSec = (newPosition * totalMilSec / 500);
		Log.i(TAG, milSec / 1000 + "");
		mp.seekTo(milSec);
		updateSongProgress();
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(mUpdateTimeTask);
		triggerWidgetUpdate();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateSongProgress();
		songTitle.setText(mp.get(mp.getCurrent()).getAuthor() + " - "
				+ mp.get(mp.getCurrent()).getTitle());
		totalDuration.setText(Utils.getDuration(mp.getDuration()));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mp.release();
		finish();
	}

	@Override
	public void onStop() {
		super.onStop();
		mp.saveChanges();
	}

	@Override
	public void update(Observable observable, Object data) {
		Log.i(TAG, data.toString());
		Toast.makeText(getApplicationContext(), data.toString(),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder ask = new AlertDialog.Builder(this);
		ask.setTitle("What do you want to do?");
		ask.setMessage("Do you want your music player to keep playing or exit the application?");
		ask.setPositiveButton("Keep Playing",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO: keep playing
						Log.i(TAG, "Keep playing");
						showHomescreen();
					}

				});
		ask.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO: exit
				Log.i(TAG, "Exit");
				mHandler.removeCallbacks(mUpdateTimeTask);
				onDestroy();
			}
		});
		ask.setCancelable(true);
		ask.create().show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent touch) {
		switch (touch.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			x1 = touch.getX();
			y1 = touch.getY();
			break;
		}
		case MotionEvent.ACTION_UP: {
			x2 = touch.getX();
			y2 = touch.getY();
			if (x1 < x2) {
				leftClickListenerAction();
				Log.i(TAG, "Left to right swipe");
			} else if (x1 > x2) {
				rightClickListenerAction();
				Log.i(TAG, "Right to left swipe");
			}
			break;
		}
		}

		return false;
	}

	private void showHomescreen() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
