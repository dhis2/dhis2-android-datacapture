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

package org.dhis2.mobile.ui.adapters.menuNavigation;

import java.util.ArrayList;

import org.dhis2.mobile.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationListAdapter extends BaseAdapter {
	ArrayList<NavigationMenuItem> menuItems;
	private LayoutInflater inflater;
	
	public NavigationListAdapter(Context context, ArrayList<NavigationMenuItem> menuItems) {
		this.menuItems = menuItems;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return menuItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view;
		ViewHolder holder;
		
		if (convertView == null) {
			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.navigation_drawer_item, null);
			TextView title = (TextView) root.findViewById(R.id.drawer_item_description);
			ImageView icon = (ImageView) root.findViewById(R.id.drawer_icon_item);
			
			holder = new ViewHolder(title, icon);
			root.setTag(holder);
			view = root;
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		holder.title.setText(menuItems.get(position).getTitleId());
		holder.icon.setImageResource(menuItems.get(position).getIconId());
		
		return view;
	}
	
	private class ViewHolder {
		public final TextView title;
		public final ImageView icon;
		
		ViewHolder(TextView title, ImageView icon) {
			this.title = title;
			this.icon = icon;
		}
	}

}
