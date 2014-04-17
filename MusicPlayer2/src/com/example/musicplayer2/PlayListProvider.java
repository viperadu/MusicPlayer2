package com.example.musicplayer2;

import java.util.ArrayList;

import android.app.LauncherActivity.ListItem;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class PlayListProvider implements RemoteViewsFactory {

	private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
	private Context context = null;
	private int appWidgetId;
	private MusicPlayer mp;
	
	public PlayListProvider(Context context, Intent intent) {
		this.context = context;
		mp = ((MusicPlayer) context.getApplicationContext());
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		populateListItem();
	}
	
	private void populateListItem() {
		for(int i=0; i<mp.size(); i++) {
			ListItem listItem = new ListItem();
			listItem.label = mp.get(mp.getCurrent()).getAuthor() + 
					" - " + mp.get(mp.getCurrent()).getTitle();
			listItemList.add(listItem);
		}
	}

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteViews getViewAt(int arg0) {
		final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_item);
		ListItem listItem = (ListItem) listItemList.get(arg0);
		remoteView.setTextViewText(R.id.heading, listItem.label);
//		remoteView.setTextViewText(R.id.content, listItem.content);
		return remoteView;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDataSetChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}

}
