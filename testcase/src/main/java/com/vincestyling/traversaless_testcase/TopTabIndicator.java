/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Jake Wharton
 * http://viewpagerindicator.com/
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
package com.vincestyling.traversaless_testcase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * A PageIndicator is responsible to show an visual indicator
 * on the total views number and the current visible view.
 */
public class TopTabIndicator extends View {
	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	private boolean mIsInMineMode;
	private View mViewPager;

	private int mScrollingToPage;
	private float mPageOffset;

	private static final int INVALID_POINTER = -1;

	private int mTouchSlop;
	private float mLastMotionX = -1;
	private int mActivePointerId = INVALID_POINTER;
	private boolean mIsDragging;

	protected int mTextColor;
	protected float mTextSize;
	protected int mUnderlineColor;
	protected int mUnderlineHeight;

	public TopTabIndicator(Context context) {
		this(context, null);
	}

	public TopTabIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;

		mTextColor = getResources().getColor(R.color.frag_tab_item_text);
		mTextSize = getResources().getDimension(R.dimen.frag_tab_item_text);
		mUnderlineColor = getResources().getColor(R.color.frag_tab_item_underline);
		mUnderlineHeight = getResources().getDimensionPixelSize(R.dimen.frag_tab_item_underline);

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mViewPager == null) return;

		final int count = getCount();
		if (count == 0) return;

		Rect areaRect = new Rect();
		areaRect.left = getPaddingLeft();
		areaRect.right = getWidth() - getPaddingRight();
		areaRect.top = getPaddingTop();
		areaRect.bottom = getHeight() - getPaddingBottom();

		int btnWidth = areaRect.width() / count;

		Rect tabRect = new Rect(areaRect);
		tabRect.top = tabRect.height() - mUnderlineHeight;
		tabRect.left += (mScrollingToPage + mPageOffset) * btnWidth;
		tabRect.right = tabRect.left + btnWidth;
		mPaint.setColor(mUnderlineColor);
		canvas.drawRect(tabRect, mPaint);

		mPaint.setColor(mTextColor);
		mPaint.setTextSize(mTextSize);

		for (int pos = 0; pos < count; pos++) {
			tabRect.set(areaRect);
			tabRect.left += pos * btnWidth;
			tabRect.right = tabRect.left + btnWidth;

			String pageTitle = getPageTitle(pos);

			RectF bounds = new RectF(tabRect);
			bounds.right = mPaint.measureText(pageTitle, 0, pageTitle.length());
			bounds.bottom = mPaint.descent() - mPaint.ascent();

			bounds.left += (tabRect.width() - bounds.right) / 2.0f;
			bounds.top += (tabRect.height() - bounds.bottom) / 2.0f;

			canvas.drawText(pageTitle, bounds.left, bounds.top - mPaint.ascent(), mPaint);
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (super.onTouchEvent(ev)) return true;
		if (mViewPager == null) return false;

		final int count = getCount();
		if (count == 0) return false;

		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mLastMotionX = ev.getX();
				break;

			case MotionEvent.ACTION_MOVE: {
				final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
				final float x = MotionEventCompat.getX(ev, activePointerIndex);
				final float deltaX = x - mLastMotionX;

				if (!mIsDragging) {
					if (Math.abs(deltaX) > mTouchSlop) {
						mIsDragging = true;
					}
				}

				if (mIsDragging) {
					mLastMotionX = x;
					if (isFakeDragging() || beginFakeDrag()) {
						fakeDragBy(deltaX);
					}
				}

				break;
			}

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (!mIsDragging) {
					Rect areaRect = new Rect();
					areaRect.left = getPaddingLeft();
					areaRect.right = getWidth() - getPaddingRight();
					areaRect.top = getPaddingTop();
					areaRect.bottom = getHeight() - getPaddingBottom();

					int btnWidth = areaRect.width() / count;

					for (int pos = 0; pos < count; pos++) {
						RectF tabRect = new RectF(areaRect);
						tabRect.left += pos * btnWidth;
						tabRect.right = tabRect.left + btnWidth;

						if (tabRect.contains(ev.getX(), ev.getY())) {
							setCurrentItem(pos);
							return true;
						}
					}
				}

				mIsDragging = false;
				mActivePointerId = INVALID_POINTER;
				if (isFakeDragging()) endFakeDrag();
				break;

			case MotionEventCompat.ACTION_POINTER_DOWN: {
				final int index = MotionEventCompat.getActionIndex(ev);
				mLastMotionX = MotionEventCompat.getX(ev, index);
				mActivePointerId = MotionEventCompat.getPointerId(ev, index);
				break;
			}

			case MotionEventCompat.ACTION_POINTER_UP:
				final int pointerIndex = MotionEventCompat.getActionIndex(ev);
				final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
				if (pointerId == mActivePointerId) {
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
				}
				mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
				break;
		}

		return true;
	}

	public void setViewPager(View view, boolean isInMineMode) {
		if (mViewPager == view) return;

		mViewPager = view;
		mIsInMineMode = isInMineMode;
		setOnPageChangeListener();

		invalidate();
	}

	public void setViewPager(View view, boolean isInMineMode, int initialPosition) {
		setViewPager(view, isInMineMode);
		setCurrentItem(initialPosition);
	}

	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		setActuallyCurrentItem(item);
		invalidate();
	}

	public void notifyDataSetChanged() {
		invalidate();
	}

	public void onPageScrolled(int position, float positionOffset) {
		mPageOffset = positionOffset;
		mScrollingToPage = position;
		invalidate();
	}

	private int getCount() {
		if (mIsInMineMode) {
			return ((com.vincestyling.traversaless.ViewPager) mViewPager).getAdapter().getCount();
		} else {
			return ((ViewPager) mViewPager).getAdapter().getCount();
		}
	}

	private String getPageTitle(int position) {
		if (mIsInMineMode) {
			return (String) ((com.vincestyling.traversaless.ViewPager) mViewPager).getAdapter().getPageTitle(position);
		} else {
			return (String) ((ViewPager) mViewPager).getAdapter().getPageTitle(position);
		}
	}

	private void setOnPageChangeListener() {
		if (mIsInMineMode) {
			((com.vincestyling.traversaless.ViewPager) mViewPager)
					.setOnPageChangeListener(new com.vincestyling.traversaless.ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
							TopTabIndicator.this.onPageScrolled(position, positionOffset);
						}
					});
		} else {
			((ViewPager) mViewPager).setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					TopTabIndicator.this.onPageScrolled(position, positionOffset);
				}
			});
		}
	}

	private void setActuallyCurrentItem(int item) {
		if (mIsInMineMode) {
			((com.vincestyling.traversaless.ViewPager) mViewPager).setCurrentItem(item);
		} else {
			((ViewPager) mViewPager).setCurrentItem(item);
		}
	}

	private boolean isFakeDragging() {
		if (mIsInMineMode) {
			return ((com.vincestyling.traversaless.ViewPager) mViewPager).isFakeDragging();
		} else {
			return ((ViewPager) mViewPager).isFakeDragging();
		}
	}

	private boolean beginFakeDrag() {
		if (mIsInMineMode) {
			return ((com.vincestyling.traversaless.ViewPager) mViewPager).beginFakeDrag();
		} else {
			return ((ViewPager) mViewPager).beginFakeDrag();
		}
	}

	private void fakeDragBy(float xOffset) {
		if (mIsInMineMode) {
			((com.vincestyling.traversaless.ViewPager) mViewPager).fakeDragBy(xOffset);
		} else {
			((ViewPager) mViewPager).fakeDragBy(xOffset);
		}
	}

	private void endFakeDrag() {
		if (mIsInMineMode) {
			((com.vincestyling.traversaless.ViewPager) mViewPager).endFakeDrag();
		} else {
			((ViewPager) mViewPager).endFakeDrag();
		}
	}

}
