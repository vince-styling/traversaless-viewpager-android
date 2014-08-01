package com.vincestyling.traversaless_testcase;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ListView lsvContent = (ListView) findViewById(R.id.lsvContent);

		lsvContent.setDivider(new ColorDrawable(Color.parseColor("#efefef")));
		lsvContent.setDividerHeight(1);

		final MenuItem[] menus = {
				new MenuItem(R.string.menu_general, GeneralTestActivity.class),
				new MenuItem(R.string.menu_dataset_changed, DataSetChangedTestActivity.class),
				new MenuItem(R.string.menu_indicator, IndicatorTestActivity.class),
				new MenuItem(R.string.menu_scrolling_speed, ScrollingSpeedTestActivity.class),
		};

		lsvContent.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return menus.length;
			}

			@Override
			public MenuItem getItem(int position) {
				return menus[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
				}

				MenuItem item = getItem(position);
				((TextView) convertView).setText(item.textResId);
				return convertView;
			}
		});

		lsvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(MainActivity.this, menus[position].activityClass));
			}
		});
	}

	private class MenuItem {
		int textResId;
		Class activityClass;
		private MenuItem(int textResId, Class activityClass) {
			this.textResId = textResId;
			this.activityClass = activityClass;
		}
	}
}
