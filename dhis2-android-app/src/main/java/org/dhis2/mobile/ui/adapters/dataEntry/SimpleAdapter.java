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

package org.dhis2.mobile.ui.adapters.dataEntry;

import java.util.ArrayList;

import org.dhis2.mobile.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SimpleAdapter extends BaseAdapter {
	private ArrayList<String> items;
	private LayoutInflater inflater;

	public SimpleAdapter(LayoutInflater inflater, ArrayList<String> items) {
		this.items = items;
		this.inflater = inflater;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextViewHolder holder;
		View view;

		if (convertView == null) {
			ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.dialog_listview_item, null);
			TextView textView = (TextView) rootView.findViewById(R.id.text_item);

			holder = new TextViewHolder(textView);
			rootView.setTag(holder);
			view = rootView;
		} else {
			view = convertView;
			holder = (TextViewHolder) view.getTag();
		}

		holder.textView.setText(items.get(position));
		return view;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int pos) {
		return pos;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	private class TextViewHolder {
		final TextView textView;

		public TextViewHolder(TextView textView) {
			this.textView = textView;
		}
	}
}
