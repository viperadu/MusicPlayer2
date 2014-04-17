package com.example.musicplayer2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class MusicPlayerWidget extends AppWidgetProvider {
	public static final String ACTION_CLICK = "ACTION_CLICK";
	private MusicPlayer mp;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		ComponentName thisWidget = new ComponentName(context, MusicPlayerWidget.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		mp = ((MusicPlayer) context.getApplicationContext());
		
		for(int widgetId : allWidgetIds) {
			
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			String details = mp.get(mp.getCurrent()).getAuthor() + " - " + mp.get(mp.getCurrent()).getTitle();
			remoteViews.setTextViewText(R.id.trackDetails, details);
			
			//Load songs for playlist view
			Intent svcIntent = new Intent(context, PlayListService.class);
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
			remoteViews.setRemoteAdapter(R.id.playList, svcIntent);
			
			Intent intent = new Intent(context, MusicPlayerWidget.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
			
			Intent n = new Intent(context, MusicPlayerWidget.class);
			n.setAction("next");
			PendingIntent pn = PendingIntent.getBroadcast(context, 0, n, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_right, pn);
			
			Intent p = new Intent(context, MusicPlayerWidget.class);
			p.setAction("prev");
			PendingIntent pp = PendingIntent.getBroadcast(context, 0, p, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_left, pp);
			
			Intent play = new Intent(context, MusicPlayerWidget.class);
			play.setAction("play");
			PendingIntent pendingPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_play, pendingPlay);
			
			Intent pause = new Intent(context, MusicPlayerWidget.class);
			pause.setAction("pause");
			PendingIntent pendingPause = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_pause, pendingPause);
			
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		mp = ((MusicPlayer) context.getApplicationContext());
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		if(intent.getAction().equals("prev")) {
			if(mp.getCurrent() == 0) {
				mp.setCurrent(mp.size() - 1);
			} else {
				mp.setCurrent(mp.getCurrent() - 1);
			}			
		} else if(intent.getAction().equals("next")) {
			if(mp.getCurrent() == mp.size() - 1) {
				mp.setCurrent(0);
			} else {
				mp.setCurrent(mp.getCurrent() + 1);
			}
		} else if(intent.getAction().equals("play")) {
			if (mp.isPaused()) {
				mp.start();
				mp.setPaused(false);
			} else {
				loadSong();
				mp.start();
				mp.setPaused(false);
			}
		} else if(intent.getAction().equals("pause")) {
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
		if(intent.getAction().equals("prev") || intent.getAction().equals("next")) {
			if(mp.isPlaying()) {
				loadSong();
				mp.start();
			}
			String details = mp.get(mp.getCurrent()).getAuthor() + " - " + mp.get(mp.getCurrent()).getTitle();
			remoteViews.setTextViewText(R.id.trackDetails, details);
		}
		//trigger self-update
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), MusicPlayerWidget.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private void loadSong() {
		mp.reset();
		mp.setDataSource(mp.get(mp.getCurrent()).getPath());
		mp.prepare();
	}	
}
