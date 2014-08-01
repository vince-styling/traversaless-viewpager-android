package com.vincestyling.traversaless_testcase;

import android.view.View;
import android.widget.ToggleButton;
import com.vincestyling.traversaless.ViewPager;

public class GeneralTestActivity extends BaseTestActivity {
	private ToggleButton btnSwitchSmoothScroll;

	@Override
	protected void onCreateView() {
		setContentView(R.layout.general);

		btnSwitchSmoothScroll = (ToggleButton) findViewById(R.id.btnSwitchSmoothScroll);

		findViewById(R.id.btnBookshelf).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentItem(0);
			}
		});
		findViewById(R.id.btnFinder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentItem(1);
			}
		});
		findViewById(R.id.btnDetector).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentItem(2);
			}
		});
		findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setCurrentItem(3);
			}
		});
	}

	private void setCurrentItem(int item) {
		if (mIsInMineMode) {
			((ViewPager) mViewPagerObj).setCurrentItem(item, btnSwitchSmoothScroll.isChecked());
		} else {
			((android.support.v4.view.ViewPager) mViewPagerObj).setCurrentItem(item, btnSwitchSmoothScroll.isChecked());
		}
	}
}
