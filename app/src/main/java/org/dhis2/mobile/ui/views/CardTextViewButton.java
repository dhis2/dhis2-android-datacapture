/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;

import org.dhis2.mobile.R;

public class CardTextViewButton extends CardView {
    private static final int ELEVATION = 2;
    private static final int RADIUS = 4;
    private static final int TEXT_VIEW_MARGIN = 10;

    private FontTextView mTextView;
    private CharSequence mHint;

    public CardTextViewButton(Context context) {
        super(context);
        init(context);
    }

    public CardTextViewButton(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context);

        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(
                    attributes, R.styleable.ButtonHint);
            mHint = attrs.getString(R.styleable.ButtonHint_hint);
            setText(mHint);
            attrs.recycle();
        }
    }

    private void init(Context context) {
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_VIEW_MARGIN,
                getResources().getDisplayMetrics());
        LayoutParams textViewParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(margin, margin, margin, margin);

        float radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RADIUS,
                getResources().getDisplayMetrics());
        float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ELEVATION,
                getResources().getDisplayMetrics());

        setUseCompatPadding(true);
        setCardElevation(elevation);
        setRadius(radius);

        mTextView = new FontTextView(context);
        mTextView.setClickable(true);
        mTextView.setId(getId());
        mTextView.setBackgroundResource(
                R.drawable.spinner_background_holo_light);
        mTextView.setFont(getContext().getString(R.string.regular_font_name));
        mTextView.setLayoutParams(textViewParams);

        addView(mTextView);
    }

    public void setText(CharSequence sequence) {
        if (mTextView != null && sequence != null) {
            mTextView.setText(sequence);
        }
    }

    public void setHint(CharSequence sequence) {
        mHint = sequence;
        if (mTextView != null && mHint != null) {
            mTextView.setHint(mHint);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        mTextView.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        mTextView.setEnabled(isEnabled);
        setText(mHint);
    }
}
