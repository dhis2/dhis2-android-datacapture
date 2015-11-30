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

import org.dhis2.mobile.utils.ViewUtils;

import org.dhis2.mobile.io.models.Coordinates;
import org.dhis2.mobile.io.models.Field;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CoordinatesPicker {
	private static final int MIN_LATITUDE_VALUE = -90;
	private static final int MAX_LATITUDE_VALUE = 90;
	private static final int MIN_LONGITUDE_VALUE = -180;
	private static final int MAX_LONGITUDE_VALUE = 180;
	private static final String MINUS = "-";
	
	public interface OnProgressListener {
		public void onProgressChanged(Coordinates coords);
	}
	
	private abstract static class AbstractTextWatcher implements TextWatcher {
		public abstract void afterTextChanged(Editable editable);
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
	}
	
	private View coordsPickerContainer;
	private TextView coordsPickerLabel;
	
	private EditText latitude;
	private EditText longitude;
	private OnProgressListener callback;
	
	private Coordinates coords;
	
	public CoordinatesPicker(View root, OnProgressListener callback) {
		this.callback = callback;
		
		coordsPickerLabel = (TextView) root.findViewById(R.id.coordinates_picker_label);
		coordsPickerContainer = root.findViewById(R.id.coordinates_picker_container);
		
		TextWatcher latitudeWatcher = new AbstractTextWatcher() {
			
			@Override
			public void afterTextChanged(Editable editable) {
				handleLatitude(editable.toString());
			}
		};
		
		TextWatcher longitudeWatcher = new AbstractTextWatcher() {

			@Override
			public void afterTextChanged(Editable editable) {
				handleLongitude(editable.toString());
			}
		};
		
		latitude = (EditText) root.findViewById(R.id.latitude);
		latitude.addTextChangedListener(latitudeWatcher);
		
		longitude = (EditText) root.findViewById(R.id.longitude);
		longitude.addTextChangedListener(longitudeWatcher);
		
		coords = new Coordinates();
		hide();
	}
	
	public void show() {
		ViewUtils.enableViews(coordsPickerContainer, coordsPickerLabel);
		resetValues();
	}
	
	public void hide() {
		ViewUtils.hideAndDisableViews(coordsPickerContainer, coordsPickerLabel);
		resetValues();
	}
	
	public void resetValues() {
		coords = new Coordinates();
		latitude.setText(Field.EMPTY_FIELD);
		longitude.setText(Field.EMPTY_FIELD);
	}
	
	public boolean isShown() {
		return coordsPickerContainer.isShown() && coordsPickerContainer.isShown();
	}
	
	private static double roundCoordinate(double coord) {
		return Math.round(coord * 100.0) / 100.0;
	} 
	
	private void handleLatitude(String lat) {
		if (lat == null || lat.equals(Field.EMPTY_FIELD) || lat.equals(MINUS)) {
			coords.setLatitude(Field.EMPTY_FIELD);
		} else {
			double value = Double.parseDouble(lat);
			if (value < MIN_LATITUDE_VALUE || value > MAX_LATITUDE_VALUE) {
				coords.setLatitude(Field.EMPTY_FIELD);
				latitude.setError("Latitude is between -90 and 90");
			} else {
				String coord = Double.toString(roundCoordinate(value));
				coords.setLatitude(coord);
				latitude.setError(null);
			}
		}
		callback.onProgressChanged(coords);
	}
	
	private void handleLongitude(String lon) {
		if (lon == null || lon.equals(Field.EMPTY_FIELD) || lon.equals(MINUS)) {
			coords.setLongitude(Field.EMPTY_FIELD);
		} else {
			double value = Double.parseDouble(lon);
			if (value < MIN_LONGITUDE_VALUE || value > MAX_LONGITUDE_VALUE) {
				coords.setLongitude(Field.EMPTY_FIELD);
				longitude.setError("Longitude is between -180 and 180");
			} else {
				String coord = Double.toString(roundCoordinate(value));
				coords.setLongitude(coord);
				longitude.setError(null);
			}
		}
		callback.onProgressChanged(coords);
	}
	
	public void restoreSelection(Coordinates coords) {
		if (coords != null) {
			latitude.setText(coords.getLatitude());
			longitude.setText(coords.getLongitude());
			
			handleLatitude(coords.getLatitude());
			handleLongitude(coords.getLongitude());
		}
	}
	
	public Coordinates getSavedSelection() {
		String lat = latitude.getText().toString();
		String lon = longitude.getText().toString();
	
		if (lat.equals(Field.EMPTY_FIELD) && lon.equals(Field.EMPTY_FIELD)) {
			return null;
		} else {
			return new Coordinates(lat, lon);
		}
	}
}