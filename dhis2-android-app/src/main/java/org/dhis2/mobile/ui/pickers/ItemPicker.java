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

import java.util.ArrayList;

import org.dhis2.mobile.R;

import org.dhis2.mobile.ui.adapters.dataEntry.DateAdapter;
import android.app.Dialog;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class ItemPicker extends BaseItemPicker {
	protected Parcelable selection;
	
	public ItemPicker(Context context, View root, int invokerId, 
			int dialogTitleId, int invokerTitleId) {
		super(context, root, invokerId, invokerTitleId, dialogTitleId);
	}

	@Override
	protected void initDialog(Context context, int dialogTitleId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // view which represents contents of dialog
        list = (ListView) inflater.inflate(R.layout.dialog_listview, null);
        
        dialog = new Dialog(context);
        dialog.setContentView(list);
		dialog.setTitle(dialogTitleId);
        
        labels = new ArrayList<>();
        adapter = new DateAdapter(inflater);
        list.setAdapter(adapter);
        
        disable();
	}
	
	@Override
	public void enable() {
		super.enable();
		resetSelection();
	}
	
	@Override
	public void disable() {
		super.disable();
		resetSelection();
	}
	
	public void saveSelection(Parcelable selection) {
		this.selection = selection;
	}
	
	public Parcelable getSavedSelection() {
		return selection;
	}
	
    private void resetSelection() {
    	selection = null;
    }
	
    public void updateContent(ArrayList<String> items) {
        labels.clear();
        labels.addAll(items);
        adapter.notifyDataSetChanged();
    }
}
