package com.vincestyling.traversaless_testcase;

import android.os.Handler;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DataSetChangedTestActivity extends BaseTestActivity implements Runnable, View.OnClickListener {
	private final static StringBuilder FRAGMENT_LOGS = new StringBuilder();
	private static int LOG_NUMBER;

	private TextView mTxvFragmentLogs;

	@Override
	protected void onCreateView() {
		setContentView(R.layout.dataset_changed);

		mTxvFragmentLogs = (TextView) findViewById(R.id.txvFragmentLogs);
		mTxvFragmentLogs.setMovementMethod(new ScrollingMovementMethod());
		FRAGMENT_LOGS.setLength(0);
		LOG_NUMBER = 10;

		findViewById(R.id.btnNothingChange).setOnClickListener(this);
		findViewById(R.id.btnShuffle).setOnClickListener(this);
		findViewById(R.id.btnClear).setOnClickListener(this);
		findViewById(R.id.btnDeleteItem).setOnClickListener(this);
		findViewById(R.id.btnAddItem).setOnClickListener(this);
		findViewById(R.id.btnRenewAdapter).setOnClickListener(this);
		findViewById(R.id.btnEmptyAdapter).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnNothingChange) {}

		else if (v.getId() == R.id.btnShuffle) {
			shuffleMenuList(mMenuList);
			shuffleMenuList(mMenuList);
		}

		else if (v.getId() == R.id.btnClear) {
			mMenuList.clear();
		}

		else if (v.getId() == R.id.btnDeleteItem) {
			if (mMenuList.size() == 0) return;
			int randIndex = new Random(System.nanoTime()).nextInt(mMenuList.size());
			mMenuList.remove(randIndex);
		}

		else if (v.getId() == R.id.btnAddItem) {
			if (mMenuList.size() == 0) return;
			List<FragmentCreator> tmpMenuList = buildMenuList();
			shuffleMenuList(tmpMenuList);
			shuffleMenuList(tmpMenuList);
			mMenuList.add(tmpMenuList.get(0));
		}

		else if (v.getId() == R.id.btnRenewAdapter) {
			if (mIsInMineMode) {
				// random delete first item to mock dataset change.
				if (new Random(System.nanoTime()).nextInt(mMenuList.size()) % 2 == 0) {
					mMenuList.remove(0);
				}
				setMineViewPagerAdapter(false);
			} else {
				// random delete first item to mock dataset change.
				if (new Random(System.nanoTime()).nextInt(mMenuList.size()) % 2 == 0) {
					mMenuList.remove(0);
				}
				setOriginalViewPagerAdapter(false);
			}
			return;
		}

		else if (v.getId() == R.id.btnEmptyAdapter) {
			if (mIsInMineMode) {
				setMineViewPagerAdapter(true);
			} else {
				setOriginalViewPagerAdapter(true);
			}
			return;
		}

		if (mIsInMineMode) {
			com.vincestyling.traversaless.ViewPager viewPager = (com.vincestyling.traversaless.ViewPager) mViewPagerObj;
			viewPager.getAdapter().notifyDataSetChanged();
		} else {
			android.support.v4.view.ViewPager viewPager =
					(android.support.v4.view.ViewPager) mViewPagerObj;
			viewPager.getAdapter().notifyDataSetChanged();
		}
	}

	private void shuffleMenuList(List<FragmentCreator> menuList) {
		long seed = System.nanoTime();
		Collections.shuffle(menuList, new Random(seed));
		Collections.shuffle(menuList, new Random(seed));
	}

	@Override
	protected void fragmentUpdating() {
		new Handler().postDelayed(this, 300);
	}

	@Override
	public void run() {
		mTxvFragmentLogs.setText(FRAGMENT_LOGS);
		final Layout layout = mTxvFragmentLogs.getLayout();
		if (layout != null) {
			int scrollDelta = layout.getLineBottom(mTxvFragmentLogs.getLineCount() - 1)
					- mTxvFragmentLogs.getScrollY() - mTxvFragmentLogs.getHeight();
			if (scrollDelta > 0)
				mTxvFragmentLogs.scrollBy(0, scrollDelta);
		}
	}

	public static void appendFragmentLog(CharSequence log) {
		if (FRAGMENT_LOGS.length() > 0)
			FRAGMENT_LOGS.append(System.getProperty("line.separator"));
		FRAGMENT_LOGS.append(++LOG_NUMBER).append('.').append(log);
	}
}