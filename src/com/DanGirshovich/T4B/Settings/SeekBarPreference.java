package com.DanGirshovich.T4B.Settings;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class SeekBarPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String ns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mValueText;
	private Context mContext;
	private int mDefault, mMax, mValue = 0;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mDefault = attrs.getAttributeIntValue(ns, "defaultValue", 50);
		mMax = attrs.getAttributeIntValue(ns, "max", 100);
	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		mValueText = new TextView(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.addView(mValueText, params);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));

		if (shouldPersist())
			mValue = getPersistedInt(mDefault);

		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
		setLabel(mValue);
		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			mValue = (Integer) defaultValue;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		setLabel(value);
		if (shouldPersist()) persistInt(value);
		callChangeListener(new Integer(value));
	}
	
	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public int getProgress() {
		return mValue;
	}
	
	public void setLabel(int val){
		mValueText.setText(String.valueOf(val));
	}
}