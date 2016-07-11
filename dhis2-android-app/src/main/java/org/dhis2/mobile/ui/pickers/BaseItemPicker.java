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

import org.dhis2.mobile.ui.adapters.dataEntry.DateAdapter;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseItemPicker {
    private final TextView invoker;
    private final int invokerTitleId;
    
    protected Dialog dialog;
    protected ListView list;
    
    protected ArrayList<String> labels;
    protected DateAdapter adapter;
    
    public BaseItemPicker(Context context, View root, int invokerId, int invokerTitleId, int dialogTitleId) {
    	this.invokerTitleId = invokerTitleId;
   
        invoker = (TextView) root.findViewById(invokerId);
        invoker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				show();
			}
		});   
        
        initDialog(context, dialogTitleId);
    }
    
    protected abstract void initDialog(Context context, int dialogTitleId);
    
    public void show() {
    	dialog.show();
    }
    
    public void dismiss() {
        dialog.dismiss();
    }
    
    public boolean isShowing() {
    	return dialog.isShowing();
    }
    
    public boolean isInvokerEnabled() {
    	return invoker.isEnabled();
    }
    
    public void disable() {
    	labels.clear();
    	adapter.notifyDataSetChanged();   
        invoker.setEnabled(false);
        invoker.setText(invokerTitleId);
    }
    
    public void enable() {
    	invoker.setEnabled(true);
    	invoker.setText(invokerTitleId);
    }
    
    public void setText(String text) {
    	invoker.setText(text);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        list.setOnItemClickListener(listener);
    }  
}
