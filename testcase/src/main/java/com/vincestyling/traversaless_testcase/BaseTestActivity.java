package com.vincestyling.traversaless_testcase;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.vincestyling.traversaless_testcase.frags.BookshelfView;
import com.vincestyling.traversaless_testcase.frags.DetectorView;
import com.vincestyling.traversaless_testcase.frags.FinderView;
import com.vincestyling.traversaless_testcase.frags.SearchView;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseTestActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	private ViewGroup mRootView;

	protected List<FragmentCreator> mMenuList;

	protected boolean mIsInKeptStateMode = true;
	protected boolean mIsInMineMode = true;
	protected View mViewPagerObj;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateView();

		mMenuList = buildMenuList();

		mRootView = (ViewGroup) findViewById(R.id.mainContent);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getActionBar().setListNavigationCallbacks(
				ArrayAdapter.createFromResource(
						getActionBar().getThemedContext(),
						R.array.action_list,
						android.R.layout.simple_spinner_dropdown_item),
				this);
	}

	protected List<FragmentCreator> buildMenuList() {
		List<FragmentCreator> menuList = new LinkedList<FragmentCreator>();
		menuList.add(new FragmentCreator(BookshelfView.class, R.string.pager_item_bookshelf));
		menuList.add(new FragmentCreator(FinderView.class, R.string.pager_item_finder));
		menuList.add(new FragmentCreator(DetectorView.class, R.string.pager_item_detector));
		menuList.add(new FragmentCreator(SearchView.class, R.string.pager_item_search));
		return menuList;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		mRootView.removeAllViews();
		switch (itemPosition) {
			case 0:
				mIsInKeptStateMode = true;
				mIsInMineMode = true;
				displayMineViewPager();
				break;
			case 1:
				mIsInKeptStateMode = false;
				mIsInMineMode = true;
				displayMineViewPager();
				break;
			case 2:
				mIsInKeptStateMode = true;
				mIsInMineMode = false;
				displayOriginalViewPager();
				break;
			case 3:
				mIsInKeptStateMode = false;
				mIsInMineMode = false;
				displayOriginalViewPager();
				break;
		}
		return true;
	}

	protected void onCreateView() {}

	private void displayMineViewPager() {
		final com.vincestyling.traversaless.ViewPager viewPager = new com.vincestyling.traversaless.ViewPager(this, null);

		viewPager.setLayoutParams(new com.vincestyling.traversaless.ViewPager.LayoutParams(
				com.vincestyling.traversaless.ViewPager.LayoutParams.MATCH_PARENT, com.vincestyling.traversaless.ViewPager.LayoutParams.MATCH_PARENT));
		viewPager.setId(R.id.mainContentPager);
		mRootView.addView(viewPager);

//		viewPager.setPageMarginDrawable(new ColorDrawable(Color.RED));
//		viewPager.setPageMargin(10);

		mViewPagerObj = viewPager;
		setMineViewPagerAdapter(false);
	}

	protected void setMineViewPagerAdapter(boolean turnEmpty) {
		((com.vincestyling.traversaless.ViewPager) mViewPagerObj).setAdapter(turnEmpty ? null : buildMineAdapter());
	}

	private com.vincestyling.traversaless.PagerAdapter buildMineAdapter() {
		if (mIsInKeptStateMode) {
			return new com.vincestyling.traversaless.FragmentStatePagerAdapter(getSupportFragmentManager()) {
				@Override
				public int getCount() {
					return mMenuList.size();
				}

				@Override
				public Fragment getItem(int position) {
					return mMenuList.get(position).newInstance();
				}

				@Override
				public CharSequence getPageTitle(int position) {
					return getResources().getString(mMenuList.get(position).getTitleResId());
				}

				@Override
				public Object getItemIdentifier(int position) {
					return mMenuList.get(position);
				}

				// this method will invoke when dataset changed.
				@Override
				public int getItemPosition(Object object) {
					// this just available for my ViewPager, original ViewPager doesn't provide past position,
					// in this case, we haven't enough arguments to prove that the POSITION is UNCHANGED.
					// now in my ViewPager, you can just return the new position,
					// it could be estimate the POSITION is UNCHANGED or not.
					if (object != null) {
						for (int pos = 0; pos < mMenuList.size(); pos++) {
							Class oClas = object instanceof Class ? (Class) object : object.getClass();
							if (mMenuList.get(pos).getFragClass() == oClas) return pos;
						}
					}
					return com.vincestyling.traversaless.PagerAdapter.POSITION_NONE;
				}

				@Override
				public void startUpdate(ViewGroup container) {
					super.startUpdate(container);
					fragmentUpdating();
				}
			};
		}

		return new com.vincestyling.traversaless.FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mMenuList.size();
			}

			@Override
			public Fragment getItem(int position) {
				return mMenuList.get(position).newInstance();
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return getResources().getString(mMenuList.get(position).getTitleResId());
			}

			// this method will invoke when dataset changed.
			@Override
			public int getItemPosition(Object object) {
				// this just available for my ViewPager, original ViewPager doesn't provide past position,
				// in this case, we haven't enough arguments to prove that the POSITION is UNCHANGED.
				// now in my ViewPager, you can just return the new position,
				// it could be estimate the POSITION is UNCHANGED or not.
				if (object != null) {
					for (int pos = 0; pos < mMenuList.size(); pos++) {
						Class oClas = object instanceof Class ? (Class) object : object.getClass();
						if (mMenuList.get(pos).getFragClass() == oClas) return pos;
					}
				}
				return com.vincestyling.traversaless.PagerAdapter.POSITION_NONE;
			}

			@Override
			public void startUpdate(ViewGroup container) {
				super.startUpdate(container);
				fragmentUpdating();
			}
		};
	}

	private void displayOriginalViewPager() {
		final android.support.v4.view.ViewPager viewPager = new android.support.v4.view.ViewPager(this, null);

		viewPager.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT));
		viewPager.setId(R.id.mainContentPager);
		mRootView.addView(viewPager);

		mViewPagerObj = viewPager;
		setOriginalViewPagerAdapter(false);
	}

	protected void setOriginalViewPagerAdapter(boolean turnEmpty) {
		((android.support.v4.view.ViewPager) mViewPagerObj).setAdapter(turnEmpty ? null : buildOriginalPagerAdapter());
	}

	private android.support.v4.view.PagerAdapter buildOriginalPagerAdapter() {
		if (mIsInKeptStateMode) {
			return new android.support.v4.app.FragmentStatePagerAdapter(getSupportFragmentManager()) {
				@Override
				public int getCount() {
					return mMenuList.size();
				}

				@Override
				public Fragment getItem(int position) {
					return mMenuList.get(position).newInstance();
				}

				@Override
				public CharSequence getPageTitle(int position) {
					return getResources().getString(mMenuList.get(position).getTitleResId());
				}

				// this method will invoke when dataset changed.
				@Override
				public int getItemPosition(Object object) {
					// for original ViewPager, it's tough to do in getItemPosition().
					return super.getItemPosition(object);
				}

				@Override
				public void startUpdate(ViewGroup container) {
					super.startUpdate(container);
					fragmentUpdating();
				}
			};
		}

		return new android.support.v4.app.FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mMenuList.size();
			}

			@Override
			public Fragment getItem(int position) {
				return mMenuList.get(position).newInstance();
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return getResources().getString(mMenuList.get(position).getTitleResId());
			}

			// this method will invoke when dataset changed.
			@Override
			public int getItemPosition(Object object) {
				// for original ViewPager, it's tough to do in getItemPosition().
				return super.getItemPosition(object);
			}

			@Override
			public void startUpdate(ViewGroup container) {
				super.startUpdate(container);
				fragmentUpdating();
			}
		};
	}

	protected void fragmentUpdating() {
	}
}
