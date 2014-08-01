package com.vincestyling.traversaless_testcase;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ScrollingSpeedTestActivity extends BaseTestActivity implements View.OnClickListener, Animator.AnimatorListener {
	private View btnTriggerNext;
	private View btnTriggerPrev;
	private ToggleButton togSpeed;

	@Override
	protected void onCreateView() {
		setContentView(R.layout.scrolling_speed);

		togSpeed = (ToggleButton) findViewById(R.id.togSpeed);

		btnTriggerNext = findViewById(R.id.btnTriggerNext);
		btnTriggerNext.setOnClickListener(this);

		btnTriggerPrev = findViewById(R.id.btnTriggerPrev);
		btnTriggerPrev.setOnClickListener(this);
	}

	private boolean mIsInAnimation;

	@Override
	public void onClick(View v) {
		if (mIsInAnimation) return;
		ObjectAnimator anim;

		if (v == btnTriggerPrev) {
			if (!hasPrevPage()) {
				Toast.makeText(this, "no more previous page", Toast.LENGTH_SHORT).show();
				return;
			}
			anim = ObjectAnimator.ofFloat(this, "motionX", 0, mViewPagerObj.getWidth());
		}
		else if (v == btnTriggerNext) {
			if (!hasNextPage()) {
				Toast.makeText(this, "no more next page", Toast.LENGTH_SHORT).show();
				return;
			}
			anim = ObjectAnimator.ofFloat(this, "motionX", 0, -mViewPagerObj.getWidth());
		}
		else return;

		anim.setInterpolator(new LinearInterpolator());
		anim.addListener(this);
		anim.setDuration(togSpeed.isChecked() ? 300 : 800);
		anim.start();
	}

	public void setMotionX(float motionX) {
		if (!mIsInAnimation) return;
		mLastMotionX = motionX;
		final long time = SystemClock.uptimeMillis();
		simulate(MotionEvent.ACTION_MOVE, mMotionBeginTime, time);
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		mIsInAnimation = false;
		final long time = SystemClock.uptimeMillis();
		simulate(MotionEvent.ACTION_UP, mMotionBeginTime, time);
	}

	protected long mMotionBeginTime;
	protected float mLastMotionX;

	@Override
	public void onAnimationStart(Animator animation) {
		mLastMotionX = 0;
		mIsInAnimation = true;
		final long time = SystemClock.uptimeMillis();
		simulate(MotionEvent.ACTION_DOWN, time, time);
		mMotionBeginTime = time;
	}

	// method from http://stackoverflow.com/a/11599282/1294681
	private void simulate(int action, long startTime, long endTime) {
		// specify the property for the two touch points
		MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[1];
		MotionEvent.PointerProperties pp1 = new MotionEvent.PointerProperties();
		pp1.id = 0;
		pp1.toolType = MotionEvent.TOOL_TYPE_FINGER;

		properties[0] = pp1;

		// specify the coordinations of the two touch points
		// NOTE: you MUST set the pressure and size value, or it doesn't work
		MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[1];
		MotionEvent.PointerCoords pc1 = new MotionEvent.PointerCoords();
		pc1.x = mLastMotionX;
		pc1.pressure = 1;
		pc1.size = 1;
		pointerCoords[0] = pc1;

		final MotionEvent ev = MotionEvent.obtain(
				startTime, endTime, action, 1, properties,
				pointerCoords, 0,  0, 1, 1, 0, 0, 0, 0 );

		mViewPagerObj.dispatchTouchEvent(ev);
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

	private boolean hasPrevPage() {
		return getCurrentItem() > 0;
	}

	private boolean hasNextPage() {
		return getCurrentItem() + 1 < getCount();
	}

	private int getCount() {
		if (mIsInMineMode) {
			return ((com.vincestyling.traversaless.ViewPager) mViewPagerObj).getAdapter().getCount();
		} else {
			return ((android.support.v4.view.ViewPager) mViewPagerObj).getAdapter().getCount();
		}
	}

	private int getCurrentItem() {
		if (mIsInMineMode) {
			return ((com.vincestyling.traversaless.ViewPager) mViewPagerObj).getCurrentItem();
		} else {
			return ((android.support.v4.view.ViewPager) mViewPagerObj).getCurrentItem();
		}
	}
}
