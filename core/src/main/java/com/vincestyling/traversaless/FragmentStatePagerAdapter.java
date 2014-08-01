/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vincestyling.traversaless;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;

public abstract class FragmentStatePagerAdapter extends PagerAdapter {
	private static final String TAG = "FragmentStatePagerAdapter";
	private static final boolean DEBUG = false;

	private final FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction = null;

	private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
	private Fragment mCurrentPrimaryItem = null;

	public FragmentStatePagerAdapter(FragmentManager fm) {
		mFragmentManager = fm;
	}

	/**
	 * Return the Fragment associated with a specified position.
	 */
	public abstract Fragment getItem(int position);

	@Override
	public void startUpdate(View container) {
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// If we already have this item instantiated, there is nothing
		// to do.  This can happen when we are restoring the entire pager
		// from its saved state, where the fragment manager has already
		// taken care of restoring the fragments we previously had instantiated.
		if (mFragments.size() > position) {
			Fragment f = mFragments.get(position);
			if (f != null) {
				return f;
			}
		}

		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}

		Fragment fragment = getItem(position);
		if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
		if (mSavedState.size() > position) {
			Fragment.SavedState fss = mSavedState.get(position);
			if (fss != null) {
				fragment.setInitialSavedState(fss);
			}
		}
		while (mFragments.size() <= position) {
			mFragments.add(null);
		}
		fragment.setMenuVisibility(false);
		mFragments.set(position, fragment);
		mCurTransaction.add(container.getId(), fragment);

		return fragment;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		Fragment fragment = (Fragment)object;

		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object
				+ " v=" + ((Fragment)object).getView());
		while (mSavedState.size() <= position) {
			mSavedState.add(null);
		}
		mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
		mFragments.set(position, null);

		mCurTransaction.remove(fragment);
	}

	@Override
	public void setPrimaryItem(View container, int position, Object object) {
		Fragment fragment = (Fragment)object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(View container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment)object).getView() == view;
	}

	@Override
	public Parcelable saveState() {
		Bundle state = null;
		if (mSavedState.size() > 0) {
			state = new Bundle();
			Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
			mSavedState.toArray(fss);
			state.putParcelableArray("states", fss);
		}
		for (int i=0; i<mFragments.size(); i++) {
			Fragment f = mFragments.get(i);
			if (f != null) {
				if (state == null) {
					state = new Bundle();
				}
				String key = "f" + i;
				mFragmentManager.putFragment(state, key, f);
			}
		}
		return state;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		if (state != null) {
			Bundle bundle = (Bundle)state;
			bundle.setClassLoader(loader);
			Parcelable[] fss = bundle.getParcelableArray("states");
			mSavedState.clear();
			mFragments.clear();
			if (fss != null) {
				for (int i=0; i<fss.length; i++) {
					mSavedState.add((Fragment.SavedState)fss[i]);
				}
			}
			Iterable<String> keys = bundle.keySet();
			for (String key: keys) {
				if (key.startsWith("f")) {
					int index = Integer.parseInt(key.substring(1));
					Fragment f = mFragmentManager.getFragment(bundle, key);
					if (f != null) {
						while (mFragments.size() <= index) {
							mFragments.add(null);
						}
						f.setMenuVisibility(false);
						mFragments.set(index, f);
					} else {
						Log.w(TAG, "Bad fragment at key " + key);
					}
				}
			}
		}
	}
}
