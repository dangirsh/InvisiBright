package com.DanGirshovich.T4B.Settings;

import com.DanGirshovich.T4B.Const;

import android.content.Context;
import android.util.AttributeSet;

public class SensitivityPreference extends SeekBarPreference{
	
	public SensitivityPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public void setLabel(int val){
		val += Const.SENSITIVITY_PREF_OFFSET;
		super.setLabel(val);
	}

}
