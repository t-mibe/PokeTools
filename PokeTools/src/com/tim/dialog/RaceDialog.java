package com.tim.dialog;

import com.tim.data.Race;

import android.content.Context;
import android.view.View;

public class RaceDialog extends ViewDialog {

	public RaceDialog(Context context) {
		super(context, new Race(context));
	}

	@Override
	public View getDialogViewByRecord(String record) {
		return null;
	}
}
