package com.vincestyling.traversaless_testcase;

public class IndicatorTestActivity extends BaseTestActivity {
	private TopTabIndicator pageIndicator;

	@Override
	protected void onCreateView() {
		setContentView(R.layout.with_indicator);
		pageIndicator = (TopTabIndicator) findViewById(R.id.pageIndicator);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		boolean result = super.onNavigationItemSelected(itemPosition, itemId);
		pageIndicator.setViewPager(mViewPagerObj, mIsInMineMode);
		return result;
	}
}
