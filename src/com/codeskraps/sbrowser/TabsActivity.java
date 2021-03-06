package com.codeskraps.sbrowser;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TabsActivity extends Activity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "sBrowser";
	
	private SBrowserData sBrowserData = null;
	private DataBaseData dataBaseData = null;
	private ListTabAdapter lstTabAdapter = null;
	private ListView lstTab = null;
	private TextView txtIcon = null;
	private ImageView imgIcon = null;

	private Cursor cursor = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.lst_tab);

		sBrowserData = ((SBrowserApplication) getApplication()).getsBrowserData();
		dataBaseData = ((SBrowserApplication) getApplication()).getDataBaseData();

		lstTabAdapter = new ListTabAdapter(this);
		
		Log.d(TAG, "I got here");
		
		lstTab = (ListView) findViewById(R.id.lstTab);
		txtIcon = (TextView) findViewById(R.id.txtTabIcon);
		imgIcon = (ImageView) findViewById(R.id.imgTabIcon);
		
		lstTab.setOnItemClickListener(this);
		txtIcon.setOnClickListener(this);
		imgIcon.setOnClickListener(this);
		
		lstTab.setAdapter(lstTabAdapter);
		
		if ((sBrowserData.getBookmarkItem().getName().equals("updateView"))
				|| sBrowserData.getBookmarkItem().getName().equals("Set title"))
			Log.d(TAG, "do Nothing");
		else dataBaseData.insert(DataBaseData.DB_TABLE_TABS, sBrowserData.getBookmarkItem());

		cursor = dataBaseData.query(DataBaseData.DB_TABLE_TABS);
		startManagingCursor(cursor);
		
		final int idColumnIndex = cursor.getColumnIndex(DataBaseData.C_ID);
		final int userColumnIndex = cursor.getColumnIndex(DataBaseData.C_BOOK_NAME);
		final int textColumnIndex = cursor.getColumnIndex(DataBaseData.C_BOOK_URL);
		final int imageColumnIndex = cursor.getColumnIndex(DataBaseData.C_BOOK_IMAGE);

		Log.d(TAG, ("Got cursor with records: " + cursor.getCount()));

		BookmarkItem newB = new BookmarkItem("New Tab", "");
		lstTabAdapter.addItem(newB);
		
		int id;
		String name, url;
		byte[] image;
		while (cursor.moveToNext()) {
			id = cursor.getInt(idColumnIndex);
			name = cursor.getString(userColumnIndex);
			url = cursor.getString(textColumnIndex);
			image = cursor.getBlob(imageColumnIndex);
			BookmarkItem b = new BookmarkItem(name, url);
			b.setId(id);
			if (image != null) b.setFavIcon(image);
			lstTabAdapter.addItem(b);
			Log.d(TAG, String.format("\n%s: %s: %s", id, name, url));
			Log.d(TAG, "Image: " + image);
		}

	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		
		this.onClick(null);
	}

	public void onClick(View v) {
		Log.d(TAG, "count: " + lstTabAdapter.getCount());
		if (lstTabAdapter.getCount() <= 1) {
			sBrowserData.setSaveState(sBrowserData.getetxtHome());
			sBrowserData.setSelected(true);
			sBrowserData.setTabbed(false);
		}
		finish();
	}

	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Log.d(TAG, "itemClick: " + v.getId());
		if (position == 0) {
			
			sBrowserData.setSaveState(sBrowserData.getetxtHome());
		
		} else {
			
			BookmarkItem b = (BookmarkItem) lstTabAdapter.getItem(position);
			dataBaseData.delete(DataBaseData.DB_TABLE_TABS, b.getId());
			sBrowserData.setSaveState(b.getUrl());
		}
		
		sBrowserData.setTabbed(false);
		sBrowserData.setSelected(true);
		finish();
	}
	
	public void updateView() {
		Log.d(TAG, "updateView");
		(sBrowserData.getBookmarkItem()).setName("updateView");
		this.onCreate(null);
	}
}
