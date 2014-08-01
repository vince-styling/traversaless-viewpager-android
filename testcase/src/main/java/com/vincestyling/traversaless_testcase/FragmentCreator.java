package com.vincestyling.traversaless_testcase;

import android.support.v4.app.Fragment;

public class FragmentCreator {
	private Class fragClass;
	private int titleResId;

	public FragmentCreator(Class fragClass, int titleResId) {
		this.fragClass = fragClass;
		this.titleResId = titleResId;
	}

	public Fragment newInstance() {
		try {
			return (Fragment) fragClass.newInstance();
		} catch (Exception e) {}
		return null;
	}

	public Class getFragClass() {
		return fragClass;
	}

	public int getTitleResId() {
		return titleResId;
	}
}
