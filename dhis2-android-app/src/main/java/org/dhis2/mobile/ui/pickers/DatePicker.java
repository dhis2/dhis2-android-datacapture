/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.ui.pickers;

import org.dhis2.mobile.R;
import org.dhis2.mobile.ui.pickers.DatePickerDialog.OnDateSetListener;
import org.joda.time.LocalDate;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DatePicker {
	private final DatePickerDialog dialog;
	private final TextView invoker;
	private String savedSelection;

	public DatePicker(Context context, View root, int resId) {
		OnClickListener invokerListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				dialog.show();
			}
		};
		
		invoker = (TextView) root.findViewById(resId);
		invoker.setText(R.string.choose_report_date);
		invoker.setOnClickListener(invokerListener);

		LocalDate currentDate = new LocalDate();
		dialog = new DatePickerDialog(context, context.getString(R.string.choose_report_date), currentDate.getYear(),
				currentDate.getMonthOfYear() - 1, currentDate.getDayOfMonth());
		
		disable();
	}

	public void setText(String title) {
		if (title == null) {
			invoker.setText(R.string.choose_report_date);
		} else {
			invoker.setText(title);		
		}
	}

	public void setDialogTitle(String title) {
		if (title == null) {
			dialog.setTitle(R.string.choose_report_date);
		} else {
			dialog.setTitle(title);
		}
	}

	public void setOnDateSetListener(OnDateSetListener callback) {
		dialog.setOnDateSetListener(callback);
	}

	public void saveSelection(String savedSelection) {
		this.savedSelection = savedSelection;
	}

	public String getSavedSelection() {
		return savedSelection;
	}
	
	public void enable() {
		invoker.setEnabled(true);
		resetState();
	}
	
	public void disable() {
		invoker.setEnabled(false);
		resetState();
	}
	
	private void resetState() {
		savedSelection = null;
		invoker.setText(R.string.choose_report_date);
		dialog.setTitle(R.string.choose_report_date);
	}
	
	public void dismiss() {
		dialog.dismiss();
	}
	
	public boolean isShowing() {
		return dialog.isShowing();
	}
}
