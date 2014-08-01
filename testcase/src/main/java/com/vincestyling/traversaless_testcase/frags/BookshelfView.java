package com.vincestyling.traversaless_testcase.frags;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.vincestyling.traversaless_testcase.DataSetChangedTestActivity;
import com.vincestyling.traversaless_testcase.R;

public class BookshelfView extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment, null);
	}

	private TextSwitcher txsContent;
	private TextView txvText;
	private Button btnNext;

	protected String[] mTexts = {
			"Main HeadLine", "Game Of Thrones", "New In Technology", "New Articles",
			"Business News", "A Clash Of Kings", "The Whole Time", "A Storm Of Swords"
	};
	private int currentIndex;

	public static final String SAVED_INDEX = "saved_index";

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			currentIndex = savedInstanceState.getInt(SAVED_INDEX, 0);
			DataSetChangedTestActivity.appendFragmentLog(
					String.format("%s[%d] Created and restore index : %d.", ((Object) this).getClass().getSimpleName(), hashCode(), currentIndex));
		} else {
			DataSetChangedTestActivity.appendFragmentLog(
					String.format("%s[%d] Created.", ((Object) this).getClass().getSimpleName(), hashCode()));
		}

		txsContent = (TextSwitcher) view.findViewById(R.id.txsContent);
		txvText = (TextView) view.findViewById(R.id.txvText);
		btnNext = (Button) view.findViewById(R.id.btnNext);

		final float textSize = getResources().getDimension(R.dimen.frag_text_size);

		txsContent.setFactory(new ViewSwitcher.ViewFactory() {
			public View makeView() {
				TextView myText = new TextView(getActivity());
				myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
				myText.setTextColor(getTextColor());
				myText.setTextSize(textSize);
				return myText;
			}
		});

		txsContent.setInAnimation(null);
		txsContent.setOutAnimation(null);

		btnNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (++currentIndex == mTexts.length) currentIndex = 0;
				txsContent.setText(mTexts[currentIndex]);
			}
		});

		txsContent.setText(mTexts[currentIndex]);

		view.setBackgroundColor(getBackgroundColor());

		txvText.setText(toString());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(SAVED_INDEX, currentIndex);
	}

	@Override
	public void onDestroy() {
		DataSetChangedTestActivity.appendFragmentLog(
				String.format("%s[%d] Destroyed.", ((Object) this).getClass().getSimpleName(), hashCode()));
		super.onDestroy();
	}

	protected int getBackgroundColor() {
		return 0xff696969;
	}

	protected int getTextColor() {
		return Color.GREEN;
	}
}
